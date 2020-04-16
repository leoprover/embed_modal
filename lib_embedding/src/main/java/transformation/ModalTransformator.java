package transformation;


import exceptions.AnalysisException;
import exceptions.ImplementationError;
import exceptions.TransformationException;
import transformation.Definitions.*;
import util.Node;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModalTransformator {

    public enum TransformationParameter{
        SEMANTIC_MODALITY_AXIOMATIZATION, // frame properties as properties on accessibility relations
        SYNTACTIC_MODALITY_AXIOMATIZATION, // frame properties as axioms about modalities
        SEMANTIC_CUMULATIVE_QUANTIFICATION, // cumulative domain quantification with restriction axiom and varying domains
        SYNTACTIC_CUMULATIVE_QUANTIFICATION, // // cumulative domain quantification with converse barcan formula and varying domains
        SEMANTIC_DECREASING_QUANTIFICATION, // decreasing domain quantification with restriction axiom and varying domains
        SYNTACTIC_DECREASING_QUANTIFICATION, // decreasing domain quantification with barcan formula and varying domains
        SEMANTIC_CONSTANT_QUANTIFICATION, // constant quantification semantically expressed without eiw guard
        SYNTACTIC_CONSTANT_QUANTIFICATION, // constant quantification using varying quantifiers with barcan and converse barcan formula
        MONOMORPHIC_TRANSFORMATION, // use TH0 export (default)
        POLYMORPHIC_TRANSFORMATION // use TH1 export
        }

    /*
     * returns null if no contradictory parameters was found
     * returns a String with reason otherwise
     */
    public static String transformationParameterSetIsNotContradictory(Set<TransformationParameter> params){
        String ret = "";
        if (params.contains(TransformationParameter.SEMANTIC_MODALITY_AXIOMATIZATION) &&
                params.contains(TransformationParameter.SYNTACTIC_MODALITY_AXIOMATIZATION)) {
            //if (!ret.equals("")) ret += "\n";
            ret += "Transformation parameter set cannot contain semantic and syntactic modality axiomatization.";
        }
        if (params.contains(TransformationParameter.SEMANTIC_CUMULATIVE_QUANTIFICATION) &&
                params.contains(TransformationParameter.SYNTACTIC_CUMULATIVE_QUANTIFICATION)) {
            if (!ret.equals("")) ret += "\n";
            ret += "Transformation parameter set cannot contain semantic and syntactic cumulative domain quantification.";
        }
        if (params.contains(TransformationParameter.SEMANTIC_DECREASING_QUANTIFICATION) &&
                params.contains(TransformationParameter.SYNTACTIC_DECREASING_QUANTIFICATION)) {
            if (!ret.equals("")) ret += "\n";
            ret += "Transformation parameter set cannot contain semantic and syntactic decreasing domain quantification.";
        }
        if (params.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) &&
                params.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION)) {
            if (!ret.equals("")) ret += "\n";
            ret += "Transformation parameter set cannot contain semantic and syntactic constant domain quantification.";
        }
        if (params.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION) &&
                params.contains(TransformationParameter.POLYMORPHIC_TRANSFORMATION)) {
            if (!ret.equals("")) ret += "\n";
            ret += "Transformation parameter set cannot contain monomorphic and polymorphic encoding.";
        }
        if (!ret.equals("")) return ret;
        return null;
    }

    /*
     * Adds default values if necessary
     */
    public static Set<TransformationParameter> completeToDefaultParameterSet(Set<TransformationParameter> params){
        Set<TransformationParameter> newParams = new HashSet<>(params);
        // modality axiomatization
        if (!newParams.contains(TransformationParameter.SEMANTIC_MODALITY_AXIOMATIZATION) &&
                !params.contains(TransformationParameter.SYNTACTIC_MODALITY_AXIOMATIZATION))
            newParams.add(TransformationParameter.SEMANTIC_MODALITY_AXIOMATIZATION);
        // cumulative domain quantification
        if (!newParams.contains(TransformationParameter.SEMANTIC_CUMULATIVE_QUANTIFICATION) &&
                !params.contains(TransformationParameter.SYNTACTIC_CUMULATIVE_QUANTIFICATION))
            newParams.add(TransformationParameter.SEMANTIC_CUMULATIVE_QUANTIFICATION);
        // decreasing domain quantification
        if (!newParams.contains(TransformationParameter.SEMANTIC_DECREASING_QUANTIFICATION) &&
                !params.contains(TransformationParameter.SYNTACTIC_DECREASING_QUANTIFICATION))
            newParams.add(TransformationParameter.SEMANTIC_DECREASING_QUANTIFICATION);
        // constant domain quantification
        if (!newParams.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) &&
                !params.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION))
            newParams.add(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION);
        // TH0/TH1 parameter
        if (!newParams.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION) &&
                !params.contains(TransformationParameter.POLYMORPHIC_TRANSFORMATION))
            newParams.add(TransformationParameter.MONOMORPHIC_TRANSFORMATION);
        return newParams;
    }

    private static final Logger log = Logger.getLogger( "default" );
    private boolean logging;

    private final Node transformedRoot;
    private Node originalRoot;
    public ThfAnalyzer thfAnalyzer;
    public SemanticsAnalyzer semanticsAnalyzer;
    private Set<TransformationParameter> transformationParameters;
    private Set<TransformationParameter> originalTransformationParameters;

    private Set<Type> typesExistsQuantifiers;
    private Set<Type> typesForAllQuantifiers;
    private Set<Type> typesForVaryingQuantifiers;
    private Map<Type, Set<String>> declaredUserConstants; // Type -> Set of symbols
    private Set<String> usedConnectives;
    private Set<String> usedModalities;
    private HashMap<String,String> usedModalConnectivesToUsedModalities; // contains suffixes, not the names of the relations. To get the names use the value of this map and put it into AccessibilityRelation.getNormalizedRelation(...)
    private Set<String> usedSymbols;
    private List<Node> userTypes; // $tType nodes (leafs)

    // TPTP THF operators
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
    }

    public ModalTransformator(Node root){
        this(root, completeToDefaultParameterSet(new HashSet<>()));
    }

    public ModalTransformator(Node root, Set<TransformationParameter> transformationParameters){
        this.transformedRoot = root.deepCopy();
        this.originalRoot = root;
        typesExistsQuantifiers = new HashSet<>();
        typesForAllQuantifiers = new HashSet<>();
        typesForVaryingQuantifiers = new HashSet<>();
        declaredUserConstants = new HashMap<>();
        usedConnectives = new HashSet<>();
        usedModalities = new HashSet<>();
        usedModalConnectivesToUsedModalities = new HashMap<>();
        usedSymbols = new HashSet<>();
        userTypes = new ArrayList<>();
        this.originalTransformationParameters = transformationParameters;
        this.transformationParameters = completeToDefaultParameterSet(transformationParameters);
    }
    
    public void setLogLevel(Level logLevel){
        log.setLevel(logLevel);
    }

    /*
     * Top-level method that performs the embedding
     */
    public TransformContext transform() throws TransformationException, AnalysisException {
        String paramsContradictoryReason = transformationParameterSetIsNotContradictory(this.transformationParameters);
        if (paramsContradictoryReason != null) throw new TransformationException(paramsContradictoryReason);

        this.thfAnalyzer = new ThfAnalyzer(this.transformedRoot);
        this.thfAnalyzer.analyze();
        this.semanticsAnalyzer = new SemanticsAnalyzer(this.transformedRoot, this.thfAnalyzer.semanticsNodes);
        this.semanticsAnalyzer.analyzeModalSemantics();
        return this.actualTransformation();
    }

    /***********************************************************************************
     * Transformation
     ***********************************************************************************/

    private SemanticsAnalyzer.ConsequenceType getConsequenceFromAxiomIdentifier(String identifier) throws TransformationException{
        SemanticsAnalyzer.ConsequenceType consequence = semanticsAnalyzer.axiomNameToConsequenceType.getOrDefault(
                identifier,semanticsAnalyzer.axiomNameToConsequenceType.getOrDefault(
                        SemanticsAnalyzer.consequenceDefault,null));
        if (consequence == null) throw new TransformationException("No explicit or default consequence semantics found for identifier " + identifier );
        return consequence;
    }

    /*
     * Might only be called after embedding modalities since usedModalities is created during this process
     */
    private boolean problemIsMonomodal(){
        return getAllRelationSuffixes().size() == 1;
    }

    /*
     * requires that the problem is actually monomodal and exactly one modality is actually used
     */
    private boolean theMonomodalProblemIsS5U() throws TransformationException{
        if (!problemIsMonomodal()) throw new ImplementationError("Called theMonomodalProblemIsS5U despite problem not monomodal.");
        return normalizedRelationSuffixcontainsS5U(getAllRelationSuffixes().stream().findAny().get());
    }

    private TransformContext actualTransformation() throws TransformationException, AnalysisException {

        // collect all symbols to avoid variable capture when defining new bound variabls
        this.transformedRoot.getLeafsDfs().forEach(n->this.usedSymbols.add(n.getLabel()));

        // if problem does not contain any modal operators return optimized embedding which is the identity
        boolean modalityFound = false;
        for (Node t : this.transformedRoot.getLeafsDfs()) {
            if ((t.getLabel().contains("$box")) || (t.getLabel().contains("$dia"))) {
                modalityFound = true;
                break;
            }
        }
        if (!modalityFound) {
            // remove semantic thf sentences
            for (Node thf : this.thfAnalyzer.semanticsNodes){
                Node semanticalRoot = thf.getParent().getParent().getParent(); // tPTP_input
                Node parent = semanticalRoot.getParent(); // tPTP_file
                parent.delChild(semanticalRoot);
            }
            return new TransformContext("","",
                    new ArrayList<>(),this.originalRoot,this.transformedRoot,
                    this.thfAnalyzer,this.semanticsAnalyzer);
        }

        // transform role type
        for (Node type_statement : thfAnalyzer.typeRoleToNode.values()) {
            for (Node l : type_statement.getLeafsDfs()) {

                // Lift types
                // $o only since we assume rigid constants
                //if (Common.normalizeType(l.getLabel()).equals(Common.normalizeType("$o"))) {
                //    l.setLabel(Common.embedded_truth_type);
                //}

                // user types
                Type t = Type.getType(l.getLabel(), false);
                if (t.equals(Type.getType("$tType"))) userTypes.add(l);
            }
            // if it is a type declaration (and not a definition) add labels (name and type) to the map of constants (for varying domains)
            Optional<Node> typeable = type_statement.dfsRule("thf_typeable_formula");
            if (typeable.isPresent()) {
                String constant = typeable.get().getFirstLeaf().getLabel();
                Type type = Type.getType(type_statement.dfsRule("thf_top_level_type").get().getFirstChild().toStringLeafs());
                if (this.declaredUserConstants.containsKey(type)) {
                    this.declaredUserConstants.get(type).add(constant);
                } else {
                    Set<String> newSet = new HashSet<>();
                    newSet.add(constant);
                    this.declaredUserConstants.put(type, newSet);
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
                //if (Common.normalizeType(l.getLabel()).equals(Common.normalizeType("$o"))){
                //    l.setLabel(Common.embedded_truth_type);

                // substitute nullary and unary operators
                // $true $false $box $dia ~
                //}else {
                replaceNullaryAndUnaryOperators(l);
                //}
            }

            // substitute binary pairs (<=> | => | <= | <~> | ~| | ~& | = | !=)
            for (Node thf_binary_pair : statement.dfsRuleAll("thf_binary_pair")){
                // if equality is a definition
                if (thfAnalyzer.definitionNodes.contains(statement) &&
                        thf_binary_pair.getChild(1).getFirstChild().getLabel().equals("=") &&
                        statement.dfsRule("thf_binary_pair").get().equals(thf_binary_pair)){
                    // do nothing
                    // TODO is this right?
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


        }

        // substitute quantified terms (! ?)
        for (Node statement : all){
            // has to happen after embedding modalities since S5U needs a list of all modalities
            for (Node thf_quantified_formula : statement.dfsRuleAll("thf_quantified_formula")){
                embed_thf_quantified_formula(thf_quantified_formula);
            }
        }

        // lift types
        List<Node> thfToLift = new ArrayList<>();
        thfToLift.addAll(thfAnalyzer.typeRoleToNode.values()); // type statements
        thfToLift.addAll(thfAnalyzer.statementNodes);
        thfToLift.addAll(thfAnalyzer.definitionNodes);
        for (Node thf : thfToLift) {
            //System.out.println("THF: " + thf.toStringLeafs()); // debug only
            // embed what can be done by string replacement of leafs
            // this includes types, nullary and unary operators
            for (Node l : thf.getLeafsDfs()) {
                // $o only since we assume rigid constants
                Type t = Type.getType(l.getLabel(), false);

                /*if (l.getLabel().equals("$o")){
                    System.out.println("FOUND tRUTH!"); // debug only
                }*/
                if (t.equals(Type.getTruthType())) {
                    //System.out.println("EQUAL label: " + l.getLabel() + " normalized type: " + t.getNormalizedType() + " default truth: " + Type.getTruthType()); // debug only
                    l.setLabel(Common.embedded_truth_type);
                }
                /*else {
                    System.out.println("NOT label: " + l.getLabel() + " normalized type: " + t.getNormalizedType() + " default truth: " + Type.getTruthType()); // debug only
                }*/
            }
        }

        // add valid / actual to all statements which are not definitions or type declarations
        for (Node statement : thfAnalyzer.statementNodes){
            String identifier = statement.getParent().getChild(2).toStringLeafs();
            SemanticsAnalyzer.ConsequenceType consequence = getConsequenceFromAxiomIdentifier(identifier);
            if (consequence.equals(SemanticsAnalyzer.ConsequenceType.GLOBAL)) {
                // insert valid operator
                Node validLeft = new Node("t_validLeft", "( mvalid @ (");
                statement.addChildAt(validLeft, 0);
                Node validRight = new Node("t_validRight", ") )");
                statement.addChild(validRight);
            }
            else if (consequence.equals(SemanticsAnalyzer.ConsequenceType.LOCAL)){
                Node validLeft = new Node("t_actualLeft", "( mactual @ (");
                statement.addChildAt(validLeft, 0);
                Node validRight = new Node("t_actualRight", ") )");
                statement.addChild(validRight);
            }
        }

        // remove semantic thf sentences
        for (Node thf : this.thfAnalyzer.semanticsNodes){
            Node semanticalRoot = thf.getParent().getParent().getParent(); // tPTP_input
            Node parent = semanticalRoot.getParent(); // tPTP_file
            parent.delChild(semanticalRoot);
        }

        // get embedding definitions for prepending and appending, appending those with reference to constants within the problem
        String modalDefinitions = preProblemInsertions();
        String auxiliaryDefinitions = postProblemInsertion();

        // extract user types (sorts with $tType) to be placed in front of problem
        extractUserTypes();

        return new TransformContext(modalDefinitions, auxiliaryDefinitions, this.userTypes, this.originalRoot, this.transformedRoot, this.thfAnalyzer, this.semanticsAnalyzer);

    }

    /***********************************************************************************
     * Embedding of nullary and unary operators: $true $false $box $dia ~
     ***********************************************************************************/

    private void replaceNullaryAndUnaryOperators(Node leaf) throws AnalysisException {
        switch (leaf.getLabel()){
            // embed true and false
            case "$true":
                leaf.setLabel("mtrue");
                usedConnectives.add("mtrue");
                break;
            case "$false":
                leaf.setLabel("mfalse");
                usedConnectives.add("mfalse");
                break;
            case "~":
                leaf.setLabel("mnot @");
                usedConnectives.add("mnot");
                break;
            // embed simple modality
            // box and dia already have @
            case "$box":
                embed_modality_box_or_dia(leaf);
                break;
            case "$dia":
                embed_modality_box_or_dia(leaf);
                break;
            // embed integer indexed modalities
            case "$box_int":
                embed_modality_box_or_dia_int(leaf);
                break;
            case "$dia_int":
                embed_modality_box_or_dia_int(leaf);
                break;
            default:
                // do nothing
        }

    }

    /***********************************************************************************
     * Embedding of modalities: $box $dia $box_int $box_dia
     ***********************************************************************************/
    private void embed_modality_box_or_dia(Node boxOrDiaLeaf) throws AnalysisException {
        String normalizedModalOperator = Connectives.getNormalizedModalOperator(boxOrDiaLeaf);
        String normalizedAccessibilityRelationSuffix = AccessibilityRelation.getNormalizedRelationSuffix(boxOrDiaLeaf);
        usedModalConnectivesToUsedModalities.put(normalizedModalOperator,normalizedAccessibilityRelationSuffix);
        boxOrDiaLeaf.setLabel(normalizedModalOperator);
        usedModalities.add(normalizedModalOperator);
    }

    private void embed_modality_box_or_dia_int(Node boxOrDiaIntLeaf) throws AnalysisException {
        Node operatorTree = boxOrDiaIntLeaf.getNextTopBranchingNode(); // should be thf_apply_formula
        if (!operatorTree.getRule().equals("thf_apply_formula")) throw new ImplementationError("box_int @ x is not a thf_apply_formula.");
        String normalizedModalOperator = Connectives.getNormalizedModalOperator(operatorTree);
        usedModalities.add(normalizedModalOperator);
        String normalizedAccessibilityRelationSuffix = AccessibilityRelation.getNormalizedRelationSuffix(operatorTree);
        usedModalConnectivesToUsedModalities.put(normalizedModalOperator,normalizedAccessibilityRelationSuffix);
        operatorTree.delAllChildren();
        Node newOperator = new Node("t_box_dia_int" , normalizedModalOperator);
        operatorTree.addChild(newOperator);
    }

    /***********************************************************************************
     * Embedding of quantified formulas: forall, exists, lambda
     ***********************************************************************************/

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
                Type type = Type.getType(thf_variable_list.getFirstChild().getFirstChild().getLastChild().toStringLeafs());
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
                //Node lambda = new Node("t_lambda_over_statement","^ [ " + variable + " : " + type.getliftedNormalizedType() + " ] : (");
                // #TYPING
                Node lambda = new Node("t_lambda_over_statement","^ [ " + variable + " : " + type.getliftedNormalizedType() + " ] : (");
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
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(type.getNormalizedType());

                // forall quantifier
                if (quantifier.equals("!")){

                    // constant domain case
                    if (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION)) {
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_forall_const_identifier(type));
                        } else {
                            String label = Quantification.embedded_forall_const_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }
                    // cumulative/decreasing S5U case for exactly one modality
                    } else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                            transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) && // constant quantification is semantically embedded
                            theMonomodalProblemIsS5U() && // the single modality is S5U
                            (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)){ // domains are either cumulative or decreasing
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_forall_const_identifier(type));
                        } else {
                            String label = Quantification.embedded_forall_const_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }

                    // varying domain case and
                    // cumulative/decreasing domain case for non S5U and
                    // cumulative/decreasing domain case for S5U with SYNTACTIC_CONSTANT_QUANTIFICATION and
                    // consstant domain case with SYNTACTIC_CONSTANT_QUANTIFICATION
                    } else {
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_forall_varying_identifier(type));
                        } else {
                            String label = Quantification.embedded_forall_vary_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }
                        typesForVaryingQuantifiers.add(type);
                    }
                    typesForAllQuantifiers.add(type);

                // exists quantifier
                }else{

                    // constant domain case
                    if (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION)) {
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_exists_const_identifier(type));
                        } else {
                            String label = Quantification.embedded_exists_const_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }

                    // cumulative/decreasing domain S5U case for exactly one modality
                    } else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                            transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) && // constant quantification is semantically embedded
                            theMonomodalProblemIsS5U() && // the single modality is S5U
                            (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)){ // domains are either cumulative or decreasing
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_exists_const_identifier(type));
                        } else {
                            String label = Quantification.embedded_exists_const_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }

                    // varying domain case and
                    // cumulative/decreasing domain case for non S5U and
                    // cumulative/decreasing domain case for S5U with SYNTACTIC_CONSTANT_QUANTIFICATION and
                    // consstant domain case with SYNTACTIC_CONSTANT_QUANTIFICATION
                    } else {
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            quant = new Node("t_quantifier", Quantification.embedded_exists_varying_identifier(type));
                        } else {
                            String label = Quantification.embedded_exists_vary_identifier_th1 + " @ " + type.getliftedNormalizedType();
                            quant = new Node("t_quantifier", label);
                        }

                        typesForVaryingQuantifiers.add(type);
                    }
                    typesExistsQuantifiers.add(type);
                }
                thf_typed_variable.addChildAt(quant,0);

                // add bracket left
                Node bracketLeft2 = new Node("t_opening_bracket","(");
                thf_typed_variable.addChildAt(bracketLeft2,0);

                // add bracket right
                Node bracketRight2 = new Node("t_closing_bracket",")");
                n.addChild(bracketRight2);
            }else{
                throw new TransformationException("Only typed variables are supported. " + thf_variable_list.toStringLeafs() + " does have untyped variables.");
            }
        }



    }

    /***********************************************************************************
     * Embedding of binary tuples: & |
     ***********************************************************************************/
    /*
     * @param n has rule thf_binary_tuple
     */
    private void embed_thf_binary_tuple(Node n){

        // retrieve operator & | @
        Node branchNode = n.getNextBotBranchingNode();
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

        usedConnectives.add(opName);
    }

    /***********************************************************************************
     * Embedding of binary pairs: <=>  =>  <=  <~>  ~|  ~& = !=
     ***********************************************************************************/

    /*
     * @param n has rule thf_binary_pair
     */
    private void embed_thf_binary_pair(Node n){
        // Connectives = | !=
        // exploit alpha beta eta equality for embedding equality
        if (n.getChild(1).getFirstChild().getLabel().equals("=") || n.getChild(1).getFirstChild().getLabel().equals("!=")){
            String w = this.getUnusedVariableName("W");
            Node lambda = new Node("t_lambda","( ^ [ "+ w + " : " + Common.w + " ] : (");
            n.addChildAt(lambda,0);
            Node closingBrackets = new Node("t_brackets_closing","))");
            n.addChild(closingBrackets);
            return;
        }

        // Connectives <=> | => | <= | <~> | ~| | ~&
        embed_thf_binary_tuple(n); // same method
    }

    /***********************************************************************************
     * Sorting user types ($tType)
     ***********************************************************************************/

    private void extractUserTypes(){
        List<Node> newUserTypes = new ArrayList<>();
        for (Node leaf : this.userTypes){
            Node userTypeNode = leaf.getParentOfRule("tPTP_input");
            newUserTypes.add(userTypeNode);
            userTypeNode.getParent().delChild(userTypeNode);
        }
        userTypes = newUserTypes;
    }

    /***********************************************************************************
     * Auxiliary functions
     ***********************************************************************************/

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

    /***********************************************************************************
     * Modal declarations, definitions and axioms
     ***********************************************************************************/

    private Set<SemanticsAnalyzer.AccessibilityRelationProperty> getPropertiesFromNormalizedRelationSuffix(String normalizedRelationSuffix) throws TransformationException {
        Set<SemanticsAnalyzer.AccessibilityRelationProperty> properties = this.semanticsAnalyzer.modalityToAxiomList.getOrDefault(normalizedRelationSuffix,
                this.semanticsAnalyzer.modalityToAxiomList.getOrDefault(SemanticsAnalyzer.modalitiesDefault,null));
        if (properties == null) throw new TransformationException("No explicit or default domain semantics found for accessiblity relation " + normalizedRelationSuffix);
        return properties;
    }

    private boolean normalizedRelationSuffixcontainsS5U(String normalizedRelationSuffix)throws TransformationException{
        return getPropertiesFromNormalizedRelationSuffix(normalizedRelationSuffix).contains(SemanticsAnalyzer.AccessibilityRelationProperty.S5U);
    }

    private String getNormalizedRelationFromNormalizedModalOperator(String normalizedModalOperator){
        return AccessibilityRelation.getNormalizedRelation(getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator));
    }

    private HashSet<String> getAllRelationSuffixes(){
        return new HashSet<>(usedModalConnectivesToUsedModalities.values());
    }

    private String getNormalizedRelationSuffixFromNormalizedModalOperator(String normalizedModalOperator){
        String ret =  usedModalConnectivesToUsedModalities.get(normalizedModalOperator);
        if (ret == null){
            ret = usedModalConnectivesToUsedModalities.get(Connectives.getOppositeNormalizedModalOperator(normalizedModalOperator));
        }
        return ret;
    }

    private SemanticsAnalyzer.DomainType getDomainTypeFromNormalizedType(String normalizedType) throws TransformationException{
        SemanticsAnalyzer.DomainType domainType = this.semanticsAnalyzer.domainToDomainType.getOrDefault(normalizedType,
                this.semanticsAnalyzer.domainToDomainType.getOrDefault(SemanticsAnalyzer.domainDefault,null));
        if (domainType == null) throw new TransformationException("No explicit or default domain semantics found for domain " + normalizedType);
        return domainType;
    }

    private String preProblemInsertions() throws TransformationException {
        Set<String> additionalModalitiesFromSyntacticEmbedding = new HashSet<>();
        Set<String> additionalConnectivesFromSyntacticEmbedding = new HashSet<>();
        Set<Type> additionalVaryingForallQuantifiersFromSyntacticEmbedding = new HashSet<>();

        StringBuilder def = new StringBuilder();

        // declare world_type_declaration type
        def.append("% declare type for possible worlds\n");
        def.append(Common.world_type_declaration);
        def.append("\n\n");

        // introduce accessibility relations
        StringBuilder relationSb = new StringBuilder();
        for (String normalizedRelationSuffix : getAllRelationSuffixes()) {
            String normalizedRelation = AccessibilityRelation.getNormalizedRelation(normalizedRelationSuffix);
            if (normalizedRelationSuffixcontainsS5U(normalizedRelationSuffix)) continue; // S5U does not have an accessibility relation
            relationSb.append(AccessibilityRelation.getRelationDeclaration(normalizedRelation));
            relationSb.append("\n");
        }
        if (relationSb.length() != 0){
            def.append("% declare accessibility relations\n");
            def.append(relationSb);
            def.append("\n");
        }

        // define accessibility relation properties
        boolean propertyDefined = false; // for cosmetics on the output
        Set<SemanticsAnalyzer.AccessibilityRelationProperty> allProperties = new HashSet<>();
        for (Set<SemanticsAnalyzer.AccessibilityRelationProperty> set : semanticsAnalyzer.modalityToAxiomList.values()){
            allProperties.addAll(set);
        }

        // Only define relation properties if we use a semantic embedding. If a syntactical
        // embedding is used, the respective properties are formulated as axioms on the box/dia operators
        // further below.
        if (this.transformationParameters.contains(TransformationParameter.SEMANTIC_MODALITY_AXIOMATIZATION)) {
            for(SemanticsAnalyzer.AccessibilityRelationProperty p :  allProperties){
                if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) { // K and S5U do not have accessibility relation properties
                    if (!propertyDefined) def.append("% define accessibility relation properties\n");
                    propertyDefined = true;
                    def.append(AccessibilityRelation.getAccessibilityRelationPropertyDefinitionByAccessibilityRelationProperty(p));
                    def.append("\n");
                }
            }
            if (propertyDefined) def.append("\n");
            // introduce properties on the accessibility relations
            if (propertyDefined) def.append("% assign properties to accessibility relations\n");
            for (String normalizedRelationSuffix : getAllRelationSuffixes()) {
                String normalizedRelation = AccessibilityRelation.getNormalizedRelation(normalizedRelationSuffix);
                for (SemanticsAnalyzer.AccessibilityRelationProperty p : getPropertiesFromNormalizedRelationSuffix(normalizedRelationSuffix) ) {
                    if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) { // K and S5U do not have accessibility relation properties
                        def.append(AccessibilityRelation.applyPropertyToRelation(p, normalizedRelation));
                        def.append("\n");
                    }
                }
            }
        }
        if (propertyDefined) def.append("\n");

        // Collect used modalities for syntactical embedding and create a list of axioms which are introduced later after defining the embedded modal operators
        Set<String> syntacticalModalityAxioms = new HashSet<>();
        // debug output for the syntactical modality axiomatization
        //for (SemanticsAnalyzer.AccessibilityRelationProperty p : SemanticsAnalyzer.AccessibilityRelationProperty.values()){
        //    if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) {
        //        System.out.println(Connectives.applyPropertyToModality(p, usedModalities.stream().findAny().get()));
        //    }
        //}
        if (this.transformationParameters.contains(TransformationParameter.SYNTACTIC_MODALITY_AXIOMATIZATION) && !usedModalities.isEmpty()) {
            for (String normalizedModalOperator : usedModalities) {
                String normalizedRelationSuffix = getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator);
                for (SemanticsAnalyzer.AccessibilityRelationProperty p : getPropertiesFromNormalizedRelationSuffix(normalizedRelationSuffix) ) {
                    if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) { // K and S5U do not have accessibility relation properties
                        syntacticalModalityAxioms.add(Connectives.applyPropertyToModality(p, normalizedModalOperator)); // may be dia or box
                        additionalModalitiesFromSyntacticEmbedding.add(normalizedModalOperator); // box or dia
                        additionalModalitiesFromSyntacticEmbedding.add(Connectives.getOppositeNormalizedModalOperator(normalizedModalOperator)); // the other modal operator
                        additionalConnectivesFromSyntacticEmbedding.add("mimplies");
                    }
                }
            }
        }

        // introduce mvalid for global consequence
        if (this.semanticsAnalyzer.axiomNameToConsequenceType.values().contains(SemanticsAnalyzer.ConsequenceType.GLOBAL) ||
                transformationParameters.contains(TransformationParameter.SYNTACTIC_MODALITY_AXIOMATIZATION) ||
                transformationParameters.contains(TransformationParameter.SYNTACTIC_CUMULATIVE_QUANTIFICATION) ||
                transformationParameters.contains(TransformationParameter.SYNTACTIC_DECREASING_QUANTIFICATION) ||
                transformationParameters.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION)) {
            def.append("% define valid operator\n");
            def.append(Common.mvalid);
            def.append("\n\n");
        }

        // introduce current world constant and actuality operator for local consequence
        if (this.semanticsAnalyzer.axiomNameToConsequenceType.values().contains(SemanticsAnalyzer.ConsequenceType.LOCAL)) {
            def.append("% define current-world constant and actuality operator\n");
            def.append(Quantification.mcurrentworld);
            def.append("\n");
            def.append(Common.mactual);
            def.append("\n\n");
        }

        // collect domain restrictions for cumulative/decreasing
        List<String> domRestr = new ArrayList<>(); // can be either syntactic of semantic restrictions
        if (!typesForVaryingQuantifiers.isEmpty()) {
            for (Type type: typesForVaryingQuantifiers) { // insert domain restriction (cumulative etc) if necessary
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(type.getNormalizedType());
                boolean synS5UcumulOrDecr = false;
                if (problemIsMonomodal() &&
                        theMonomodalProblemIsS5U() &&
                        (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING) &&
                        transformationParameters.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION)) {
                    synS5UcumulOrDecr = true;
                }

                // cumulative semantic embedding
                if (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE && transformationParameters.contains(TransformationParameter.SEMANTIC_CUMULATIVE_QUANTIFICATION) && !synS5UcumulOrDecr) {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        domRestr.add(Quantification.cumulative_semantic_axiom_th0(type));
                    } else {
                        domRestr.add(Quantification.cumul_type_axiom_semantic_th1(type));
                    }
                }

                // decreasing semantic embedding
                if (domainType == SemanticsAnalyzer.DomainType.DECREASING && transformationParameters.contains(TransformationParameter.SEMANTIC_DECREASING_QUANTIFICATION) && !synS5UcumulOrDecr) {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        domRestr.add(Quantification.decreasing_semantic_axiom_th0(type));
                    } else {
                        domRestr.add(Quantification.decreasing_type_axiom_semantic_th1(type));
                    }

                }

                // cumulative syntactic or
                // constant syntactic embedding or
                // cumulative S5U with syntactic constant quantification
                if (( domainType == SemanticsAnalyzer.DomainType.CUMULATIVE && transformationParameters.contains(TransformationParameter.SYNTACTIC_CUMULATIVE_QUANTIFICATION)) ||
                        (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION)) ||
                        (synS5UcumulOrDecr)) {
                    // prerequisites for converse barcan formula
                    additionalConnectivesFromSyntacticEmbedding.add("mimplies"); // implication operator
                    additionalModalitiesFromSyntacticEmbedding.add(Connectives.box_unimodal); // box operator
                    additionalVaryingForallQuantifiersFromSyntacticEmbedding.add(Type.getType(type.getliftedNormalizedType())); // varying forall quantifier for (type)
                    // converse barcan formula
                    domRestr.add(Quantification.cumulative_syntactic_axiom_th0(type)); // the converse barcan formula for this type
                }

                // decreasing syntactic or
                // constant syntactic embedding or
                // decreasing S5U with syntactic constant
                if ((domainType == SemanticsAnalyzer.DomainType.DECREASING && transformationParameters.contains(TransformationParameter.SYNTACTIC_DECREASING_QUANTIFICATION)) ||
                        (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SYNTACTIC_CONSTANT_QUANTIFICATION)) ||
                        synS5UcumulOrDecr) {
                    // prerequisites for barcan formula
                    additionalConnectivesFromSyntacticEmbedding.add("mimplies"); // implication operator
                    additionalModalitiesFromSyntacticEmbedding.add(Connectives.box_unimodal); // box operator
                    additionalVaryingForallQuantifiersFromSyntacticEmbedding.add(Type.getType(type.getliftedNormalizedType())); // varying forall quantifier for (type)
                    // barcan formula
                    domRestr.add(Quantification.decreasing_syntactic_axiom_th0(type)); // the converse barcan formula for this type
                }
            }
        }

        // introduce used operators which are not valid operator nor quantifiers
        Set<String> connectivesToDefine = new HashSet<>(usedConnectives);
        connectivesToDefine.addAll(additionalConnectivesFromSyntacticEmbedding);
        Set<String> modalitiesToDefine = new HashSet<>(usedModalities);
        modalitiesToDefine.addAll(additionalModalitiesFromSyntacticEmbedding);
        if (!(connectivesToDefine.isEmpty() && modalitiesToDefine.isEmpty())) {
            def.append("% define nullary, unary and binary connectives which are no quantifiers\n");
            for (String o : connectivesToDefine) {
                def.append(Connectives.modalSymbolDefinitions.get(o));
                def.append("\n");
            }
            for (String normalizedModalOperator : modalitiesToDefine) {
                String normalizedRelation = getNormalizedRelationFromNormalizedModalOperator(normalizedModalOperator);
                String normalizedRelationSuffix = getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator);
                if (normalizedRelationSuffixcontainsS5U(normalizedRelationSuffix)) {
                    def.append(Connectives.getModalOperatorDefinitionS5U(normalizedModalOperator));
                } else {
                    def.append(Connectives.getModalOperatorDefinition(normalizedModalOperator, normalizedRelation));
                }
                def.append("\n");
            }

            def.append("\n");
        }

        // introduce eiw and nonempty for varying domains
        Set<Type> eiw_nonempty_types = new HashSet<>(typesForVaryingQuantifiers);
        eiw_nonempty_types.addAll(additionalVaryingForallQuantifiersFromSyntacticEmbedding);
        if (!eiw_nonempty_types.isEmpty()) {
            def.append("% define exists-in-world predicates for quantified types and non-emptiness axioms\n");
            if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                for (Type type : eiw_nonempty_types) {
                    def.append(Quantification.eiw_and_nonempty_th0(type));
                    def.append("\n");
                }
            } else {
                def.append(Quantification.eiw_th1);
                def.append("\n");
                def.append(Quantification.eiw_nonempty_th1);
                def.append("\n");
            }
            def.append("\n");
        }


        // introduce exist quantifiers
        Set<Type> typesExistsQuantifiersToDefine = new HashSet<>(typesExistsQuantifiers);
        //typesExistsQuantifiersToDefine.addAll(additionalExistQuantifiersFromSyntacticEmbedding);
        if (!typesExistsQuantifiersToDefine.isEmpty()) {
            boolean defConstQuant = false;
            boolean defVaryQuant = false;
            def.append("% define exists quantifiers\n");
            for (Type type : typesExistsQuantifiers) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(type.getNormalizedType());

                // constant semantic domain case
                if (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION)) {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mexists_const_th0(type));
                    } else {
                        defConstQuant = true;
                    }
                }

                // cumulative/decreasing S5U case for exactly one modality
                else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                        transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) && // constant quantification is semantically embedded
                        theMonomodalProblemIsS5U() && // the modality is S5U
                        (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)) { // domains are either cumulative or decreasing
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mexists_const_th0(type));
                    } else {
                        defConstQuant = true;
                    }
                }

                // varying domain case and
                // cumulative/decreasing domain case for non S5U and
                // cumulative/decreasing domain case for S5U with SYNTACTIC_CONSTANT_QUANTIFICATION and
                // constant domain case with SYNTACTIC_CONSTANT_QUANTIFICATION
                else {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mexists_varying_th0(type));
                    } else {
                        defVaryQuant = true;
                    }
                }

                if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION))
                    def.append("\n");
            }

            // for polymorphic embedding
            if (defConstQuant) {
                def.append(Quantification.mexists_const_th1);
                def.append("\n");
            }
            if (defVaryQuant) {
                def.append(Quantification.mexists_vary_th1);
                def.append("\n");
            }

            def.append("\n");
        }

        // introduce forall quantifiers
        Set<Type> typesForAllQuantifiersToDefine = new HashSet<>(typesForAllQuantifiers);
        Set<Type> typesForallVaryQuantifiersAlreadyDefined = new HashSet<>();
        if (!typesForAllQuantifiersToDefine.isEmpty() || !additionalVaryingForallQuantifiersFromSyntacticEmbedding.isEmpty()) {
            boolean defConstQuant = false;
            boolean defVaryQuant = false;
            def.append("% define for all quantifiers\n");
            // quantifiers for the problem
            for (Type type : typesForAllQuantifiers) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(type.getNormalizedType());

                // constant domain case
                if (domainType == SemanticsAnalyzer.DomainType.CONSTANT && transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION)) {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mforall_const_th0(type));
                    } else {
                        defConstQuant = true;
                    }
                }

                // cumulative/decreasing S5U case for exactly one modality
                else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                        transformationParameters.contains(TransformationParameter.SEMANTIC_CONSTANT_QUANTIFICATION) && // constant quantification is semantically embedded
                        theMonomodalProblemIsS5U() && // the modality is S5U
                        (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)) { // domains are either cumulative or decreasing
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mforall_const_th0(type));
                    } else {
                        defConstQuant = true;
                    }
                }

                // varying domain case and
                // cumulative/decreasing domain case for non S5U and
                // cumulative/decreasing domain case for S5U with SYNTACTIC_CONSTANT_QUANTIFICATION and
                // constant domain case with SYNTACTIC_CONSTANT_QUANTIFICATION
                else {
                    if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                        def.append(Quantification.mforall_varying_th0(type));
                        typesForallVaryQuantifiersAlreadyDefined.add(type);
                    } else {
                        defVaryQuant = true;
                    }
                }

                if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION))
                    def.append("\n");
            }

            // additional quantifiers needed for syntactic cumul/decr/const quantification embedding (varying quantifiers)
            for (Type type : additionalVaryingForallQuantifiersFromSyntacticEmbedding) {
                if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                    if (!typesForallVaryQuantifiersAlreadyDefined.contains(type)) { // only vary quantifiers that have not been already defined above
                        def.append(Quantification.mforall_varying_th0(type));
                    }
                    def.append("\n");
                } else {
                    defVaryQuant = true;
                }
            }

            // for polymorphic embedding
            if (defConstQuant) {
                def.append(Quantification.mforall_const_th1);
                def.append("\n");
            }
            if (defVaryQuant) {
                def.append(Quantification.mforall_vary_th1);
                def.append("\n");
            }

            def.append("\n");
        }

        // if using a syntactic modality axiomatization, postulate axioms on the operators
        if (this.transformationParameters.contains(TransformationParameter.SYNTACTIC_MODALITY_AXIOMATIZATION) && !syntacticalModalityAxioms.isEmpty()) {
            def.append("% define axioms on the modalities\n");
            for (String axiom : syntacticalModalityAxioms) {
                def.append(axiom);
                def.append("\n");
            }
            def.append("\n");
        }

        // postulate syntactic or semantic domain restrictions
        if (domRestr.size() != 0){
            def.append("% define domain restrictions\n");
            for (String d: domRestr) {
                def.append(d);
                def.append("\n\n");
            }
        }

        return def.toString();
    }

    private String postProblemInsertion() throws TransformationException {
        StringBuilder postDefinitions = new StringBuilder();
        for (Type type: this.declaredUserConstants.keySet()) {
            if (!type.equals(Type.getType("$tType"))) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(type.getNormalizedType());

                // constant domain type
                if (domainType == SemanticsAnalyzer.DomainType.CONSTANT){
                    // do nothing
                    // constant domain does not require user constants to exist in worlds
                }

                // the problem is monomodal and S5U and cumulative or decreasing
                else if (problemIsMonomodal() && theMonomodalProblemIsS5U() && (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)){
                    // do nothing
                    // in S5U a cumulative/decreasing domain is the same as constant domain
                }

                // domain is varying or (non-S5U + cumul/decr)
                else {
                    if (this.typesForVaryingQuantifiers.contains(type)) {
                        // an eiw-predicate of type q already exists, we can just postulate an axiom
                        // that these constants exist at all worlds
                        for (String constant : declaredUserConstants.get(type)) {
                            if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                                postDefinitions.append(Quantification.constant_eiw_th0(constant, type));
                            } else {
                                postDefinitions.append(Quantification.constant_eiw_th1(constant, type));
                            }
                            postDefinitions.append("\n");
                        }
                    } else {
                        // define eiw_predicate of that type first
                        if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                            postDefinitions.append(Quantification.eiw_and_nonempty_th0(type));
                        } else {
                            // TODO: Is there something do to here?
                        }

                        postDefinitions.append("\n");
                        // now postulate as anbove
                        for (String constant : declaredUserConstants.get(type)) {
                            if (transformationParameters.contains(TransformationParameter.MONOMORPHIC_TRANSFORMATION)) {
                                postDefinitions.append(Quantification.constant_eiw_th0(constant, type));
                            } else {
                                postDefinitions.append(Quantification.constant_eiw_th1(constant, type));
                            }
                            postDefinitions.append("\n");
                        }
                    }
                }
            }

        }
        if (postDefinitions.toString().length() == 0) return "";
        return "% define exists-in-world assertion for user-defined constants\n" + postDefinitions.toString();
    }



}

