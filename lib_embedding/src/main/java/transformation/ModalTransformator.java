package transformation;


import exceptions.AnalysisException;
import exceptions.TransformationException;
import util.tree.Node;

import java.util.*;
import java.util.logging.Logger;

public class ModalTransformator {

    private static final Logger log = Logger.getLogger( "default" );

    private final Node originalRoot;
    private Node transformedRoot;
    private ThfAnalyzer thfAnalyzer;
    private SemanticsAnalyzer semanticsAnalyzer;

    private Set<String> typesExistsQuantifiers;
    private Set<String> typesForAllQuantifiers;
    private Set<String> usedOperators;
    private Set<String> usedSymbols;

    // TPTP operators
    private static Map<String,String> operatorToEmbeddingName;
    static{
        operatorToEmbeddingName = new HashMap<>();
        operatorToEmbeddingName.put("~","mnot");
        operatorToEmbeddingName.put("&","mand");
        operatorToEmbeddingName.put("~&","mnand");
        operatorToEmbeddingName.put("|","mor");
        operatorToEmbeddingName.put("~|","mnor");
        operatorToEmbeddingName.put("=>","mimplies");
        operatorToEmbeddingName.put("<=","mimpliesreversed");
        operatorToEmbeddingName.put("<=>","mequiv");
        operatorToEmbeddingName.put("<~>","mnequiv");
        //operatorToEmbeddingName.put("=","meq");
        //operatorToEmbeddingName.put("!=","mneq");
    }

    public ModalTransformator(Node root){
        this.originalRoot = root.deepCopy();
        this.transformedRoot = root;
        typesExistsQuantifiers = new HashSet<>();
        typesForAllQuantifiers = new HashSet<>();
        usedOperators = new HashSet<>();
        usedSymbols = new HashSet<>();
    }

    public TransformContext transform() throws TransformationException,AnalysisException {
        this.thfAnalyzer = new ThfAnalyzer(this.originalRoot);
        this.thfAnalyzer.analyze();
        this.semanticsAnalyzer = new SemanticsAnalyzer(this.originalRoot, this.thfAnalyzer.semanticsNodes);
        this.semanticsAnalyzer.analyzeModalSemantics();
        TransformContext ctx = this.actualTransformation();
        return ctx;
    }

    private TransformContext actualTransformation() throws TransformationException {

        // collect all symbols to avoid variable capture when defining new bound variabls
        this.originalRoot.getLeafsDfs().stream().forEach(n->this.usedSymbols.add(n.getLabel()));

        // transform role type
        for (Node type_statement : thfAnalyzer.typeRoleToNode.values()){
            for (Node l : type_statement.getLeafsDfs()) {

                // Lift types
                // $o only since we assume rigid constants
                if (l.getLabel().equals("$o")) {
                    l.setLabel(EmbeddingDefinitions.truth_type);
                }
            }
        }

        // transform definitions and axioms,... by lifting types and substituting operators
        //for (Node statement : thfAnalyzer.statementNodes) {
        List<Node> all = new ArrayList<>();
        all.addAll(thfAnalyzer.statementNodes);
        all.addAll(thfAnalyzer.definitionNodes);
        for (Node statement : all){
            // embed what can be done by string replacement of leafs
            // this includes types, nullary and unary operators
            for (Node l : statement.getLeafsDfs()) {

                // Lift types
                // $o only since we assume rigid constants
                if (l.getLabel().equals("$o")){
                    l.setLabel(EmbeddingDefinitions.truth_type);

                // substitute nullary and unary operators
                // $true $false $box $dia ~
                }else {
                    replaceNullaryAndUnaryOperators(l);
                }
            }

            // substitute binary pairs (<=> | => | <= | <~> | ~| | ~& | = | !=)
            for (Node thf_binary_pair : statement.dfsRuleAll("thf_binary_pair")){
                // if equality is a definition
                if (thfAnalyzer.definitionNodes.contains(statement) &&
                        thf_binary_pair.getChild(1).getFirstChild().getLabel().equals("=") &&
                        statement.dfsRule("thf_binary_pair").get().equals(thf_binary_pair)){
                } else {
                    embed_thf_binary_pair(thf_binary_pair);
                }
            }

            // substitute binary tuples (or,and,apply), apply does not need to be embedded
            for (Node thf_binary_tuple : statement.dfsRuleAll("thf_and_formula")){
                embed_thf_binary_tuple(thf_binary_tuple);
            }
            for (Node thf_binary_tuple : statement.dfsRuleAll("thf_or_formula")){
                embed_thf_binary_tuple(thf_binary_tuple);
            }


            // substitute quantifications (! ?)
            for (Node thf_quantified_formula : statement.dfsRuleAll("thf_quantified_formula")){
                embed_thf_quantified_formula(thf_quantified_formula);
            }

        }

        // add valid to all statements which are not definitions or type declarations
        for (Node statement : thfAnalyzer.statementNodes){
            // insert valid operator
            Node validLeft = new Node("t_validLeft","( mvalid @ (");
            statement.addChildAt(validLeft,0);
            Node validRight = new Node("t_validRight",") )");
            statement.addChild(validRight);
        }

        // remove semantical thf sentences
        for (Node thf : this.thfAnalyzer.semanticsNodes){
            Node semanticalRoot = thf.getParent().getParent().getParent(); // tPTP_input
            Node parent = semanticalRoot.getParent(); // tPTP_file
            parent.delChild(semanticalRoot);
        }

        String modalDefinitions = getModalDefinitions();
        //System.out.println(modalDefinitions.toString());
        //System.out.println(this.transformedRoot.toStringWithLinebreaksFormatted());

        return new TransformContext(modalDefinitions, this.transformedRoot, this.originalRoot, this.thfAnalyzer, this.semanticsAnalyzer);
    }

    /*
     * @param n has rule thf_quantified_formula
     */
    private void embed_thf_quantified_formula(Node n) throws TransformationException {

        // retrieve quantifier ! | ?
        Node thf_quantification = n.getFirstChild();
        String quantifier = thf_quantification.getFirstChild().getLabel().trim();
        if ( ! ( quantifier.equals("!") || ( quantifier.equals("?") ) ) ) // lambda or more exotic stuff
            return;

        // retrieve first variable list element
        Node firstVariableList = thf_quantification.getChild(2);

        // remove quantifier, both brackets and colon
        thf_quantification.delFirstChild(); // quantifier
        thf_quantification.delFirstChild(); // [
        thf_quantification.delLastChild(); // ]
        thf_quantification.delLastChild(); // :

        // retrieve types of variables in order of occurence and substitute the thf_typed_variable descendants for the embedding
        List<Node> thf_variable_lists = firstVariableList.dfsRuleAll("thf_variable_list");
        for (Node thf_variable_list : thf_variable_lists){

            Node thf_typed_variable = thf_variable_list.getFirstChild().getFirstChild();
            if (thf_typed_variable.getRule().equals("thf_typed_variable")){

                // retrieve Type
                String type = thf_variable_list.getFirstChild().getFirstChild().getLastChild().toStringLeafs();
                //System.out.println(type);

                // retrieve variable
                String variable = thf_variable_list.getFirstChild().getFirstChild().getFirstChild().toStringLeafs();
                //System.out.println(variable);

                // remove descendants of thf_typed variable
                thf_typed_variable.delAllChildren();

                // remove commas
                if (thf_variable_list.getChildren().size() == 3){
                    thf_variable_list.delChildAt(1);
                }

                // add lambda abstraction over statement
                Node lambda = new Node("t_lambda_over_statement","^ [ " + variable + " : " + type + " ] : (");
                thf_typed_variable.addChildAt(lambda,0);

                // add bracket left
                Node bracketLeft = new Node("t_opening_bracket","(");
                thf_typed_variable.addChildAt(bracketLeft,0);

                // add bracket right
                Node bracketRight = new Node("t_closing_bracket","))");
                n.addChild(bracketRight);

                // add @
                Node at = new Node("t_at","@");
                thf_typed_variable.addChildAt(at,0);

                // add embedded quantifier functor and add quantifier for the type to
                Node quant;
                String normalizedType = EmbeddingDefinitions.normalizeType(type);
                if (quantifier.equals("!")){
                    quant = new Node("t_quantifier",EmbeddingDefinitions.embedded_forall(type));
                    typesForAllQuantifiers.add(normalizedType);
                }else{
                    quant = new Node("t_quantifier",EmbeddingDefinitions.embedded_exists(type));
                    typesExistsQuantifiers.add(normalizedType);
                }
                thf_typed_variable.addChildAt(quant,0);

            }else{
                throw new TransformationException("Only typed variables are supported. " + thf_variable_list.toStringLeafs() + " does have untyped variables.");
            }
        }



    }

    /*
     * @param n has rule thf_binary_tuple
     */
    private void embed_thf_binary_tuple(Node n){

        // retrieve operator & | @
        Node branchNode = n.getNextBranchingNode();
        if (branchNode == null){
            log.finest("thf_binary_tuple does not branch"); // should not be the case
            return;
        }
        String op = branchNode.getChild(1).toStringLeafs().trim();
        String opName = operatorToEmbeddingName.get(op);

        // @ operator does not need to be embedded
        if (op.equals("@")) return;

        // insert operator
        // A & B is converted to mand A & B
        Node newOperator = new Node(opName,opName);
        branchNode.addChildAt(newOperator, 0);
        //System.out.println(branchNode);

        // add an @ before the A and replace & leaf with @
        // mand A & B is converted to mand @ A @ B
        Node firstAt = new Node("t_apply","@");
        branchNode.addChildAt(firstAt,1);
        Node secondAt = new Node("t_apply","@");
        branchNode.replaceChildAt(3,secondAt);
        //System.out.println(branchNode);

        // If A is a binary tuple again add braces, B cannot be a binary tuple by grammar
        // mand @ A @ B is converted to mand @ (A) @ (B)
        Node openingBracketA = new Node("t_opening bracket", "(");
        branchNode.addChildAt(openingBracketA, 2);
        Node closingBracketA = new Node("t_closing bracket", ")");
        branchNode.addChildAt(closingBracketA, 4);
        //System.out.println(branchNode);

        Node openingBracketB = new Node("t_opening bracket", "(");
        branchNode.addChildAt(openingBracketB, 6);
        Node closingBracketB = new Node("t_closing bracket", ")");
        branchNode.addChildAt(closingBracketB, 8);
        //System.out.println(branchNode);

        usedOperators.add(opName);
    }

    /*
     * @param n has rule thf_binary_pair
     */
    private void embed_thf_binary_pair(Node n){
        // exploit alpha beta eta equality for embedding equality
        if (n.getChild(1).getFirstChild().getLabel().equals("=") || n.getChild(1).getFirstChild().getLabel().equals("!=")){
            String w = this.getUnusedVariableName("W");
            Node lambda = new Node("t_lambda","( ^ [ "+ w + " : " + EmbeddingDefinitions.w + " ] : (");
            n.addChildAt(lambda,0);
            Node closingBrackets = new Node("t_brackets_closing","))");
            n.addChild(closingBrackets);
            return;
        }
        embed_thf_binary_tuple(n); // same method
        // retrieve operator <=> | => | <= | <~> | ~| | ~& | = | !=

        /*
        String op = n.getChild(1).getFirstLeaf().getLabel();
        String opName = operatorToEmbeddingName.get(op);

        // insert operator
        // A = B is converted to meq A = B
        Node newOperator = new Node(opName,opName);
        n.addChildAt(newOperator, 0);

        // add an @ before the A and replace = leaf with @
        // meq A = B is converted to meq @ A @ B
        Node firstAt = new Node("apply","@");
        n.addChildAt(firstAt,1);
        Node secondAt = new Node("apply","@");
        n.replaceChildAt(3,secondAt);


        // add brackets around both A and B
        Node openingBracketA = new Node("opening bracket", "(");
        n.addChildAt(openingBracketA, 2);
        Node closingBracketA = new Node("closing bracket", ")");
        n.addChildAt(closingBracketA, 4);
        Node openingBracketB = new Node("opening bracket", "(");
        n.addChildAt(openingBracketB, 6);
        Node closingBracketB = new Node("closing bracket", ")");
        n.addChildAt(closingBracketB, 8);
        */

    }

    private void replaceNullaryAndUnaryOperators(Node leaf){
        switch (leaf.getLabel()){
            // embed true and false
            case "$true":
                leaf.setLabel("mtrue");
                usedOperators.add("mtrue");
                break;
            case "$false":
                leaf.setLabel("mfalse");
                usedOperators.add("mfalse");
                break;
            // embed simple modality
            // box and dia already have @
            case "$box":
                leaf.setLabel("mbox");
                usedOperators.add("mbox");
                break;
            case "$dia":
                leaf.setLabel("mdia");
                usedOperators.add("mdia");
                break;
            case "~":
                leaf.setLabel("mnot @");
                usedOperators.add("mnot");
                break;
            default:
                // do nothing
        }

    }

    private String getUnusedVariableName(String prefix){
        int i = 0;
        String ret = prefix + i;
        while(this.usedSymbols.contains(ret)){
            i++;
            ret = prefix + i;
        }
        this.usedSymbols.add(ret);
        return ret;
    }

    private String getModalDefinitions() throws TransformationException{
        StringBuilder def = new StringBuilder();

        // declare world_type_declaration type
        def.append("% declare type for possible worlds\n");
        def.append(EmbeddingDefinitions.world_type);
        def.append("\n\n");

        // introduce accessibility relations
        // Only one modality
        def.append("% declare accessibility relations\n");
        def.append(EmbeddingDefinitions.declareRelation("r"));
        def.append("\n\n");

        // introduce used accessibility relation properties
        // has to be reimplemented for multiple modalities
        if (semanticsAnalyzer.modalityToAxiomList.get(SemanticsAnalyzer.modalitiesDefault) == null){
            throw new TransformationException("No default value for modalities found.");
        }
        def.append("% define accessibility relation properties\n");
        for(SemanticsAnalyzer.AccessibilityRelationProperty p :  semanticsAnalyzer.modalityToAxiomList.get(SemanticsAnalyzer.modalitiesDefault)){
            if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K) {
                def.append(EmbeddingDefinitions.getAccessibilityRelationPropertyDefinitionByAccessibilityRelationProperty(p));
                def.append("\n");
            }
        }
        def.append("\n");

        // introduce properties on the accessibility relations
        // Only one modality, has to be reimplemented for multiple modalities
        def.append("% assign properties to accessibility relations\n");
        if (semanticsAnalyzer.modalityToAxiomList.get(SemanticsAnalyzer.modalitiesDefault) == null){
            throw new TransformationException("No default value for modalities found.");
        }
        for(SemanticsAnalyzer.AccessibilityRelationProperty p : semanticsAnalyzer.modalityToAxiomList.get(SemanticsAnalyzer.modalitiesDefault)){
            if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K) {
                def.append(EmbeddingDefinitions.applyPropertyToRelation(p, "r"));
                def.append("\n");
            }
        }
        def.append("\n");

        // introduce mvalid
        def.append("% define valid operator\n");
        def.append(EmbeddingDefinitions.mvalid);
        def.append("\n\n");

        // introduce used operators which are not valid operator nor quantifiers
        def.append("% define nullary, unary and binary operators which are not quantifiers or valid operator\n");
        for (String o : usedOperators){
            def.append(EmbeddingDefinitions.modalSymbolDefinitions.get(o));
            def.append("\n");
        }
        def.append("\n");

        // introduce quantifiers
        //typesForAllQuantifiers.add("$o>$o");
        //typesExistsQuantifiers.add("plushie>$o");
        //typesForAllQuantifiers.add("plushie>$o");
        def.append("% define exists quantifiers\n");
        for (String q : typesExistsQuantifiers){
            def.append(EmbeddingDefinitions.mexists_const_th0(q));
            def.append("\n");
        }
        def.append("\n% define for all quantifiers\n");
        for (String q: typesForAllQuantifiers){
            def.append(EmbeddingDefinitions.mforall_const_th0(q));
            def.append("\n");
        }

        return def.toString();
    }
}
