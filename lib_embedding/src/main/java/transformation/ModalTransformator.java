package transformation;


import exceptions.AnalysisException;
import exceptions.TransformationException;
import transformation.Definitions.AccessibilityRelation;
import transformation.Definitions.Common;
import transformation.Definitions.Connectives;
import transformation.Definitions.Quantification;
import util.tree.Node;

import java.util.*;
import java.util.logging.Logger;

public class ModalTransformator {

    public enum TransformationParameter{SEMANTICAL, SYNTACTICAL} // More probably to come, default is semantical
    private static final Logger log = Logger.getLogger( "default" );

    private final Node originalRoot;
    private Node transformedRoot;
    public ThfAnalyzer thfAnalyzer;
    public SemanticsAnalyzer semanticsAnalyzer;

    private Set<String> typesExistsQuantifiers;
    private Set<String> typesForAllQuantifiers;
    private Set<String> typesForVaryingQuantifiers;
    private Map<String, Set<String>> declaredUserConstants; // Type -> Set of symbols
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
        this.originalRoot = root.deepCopy();
        this.transformedRoot = root;
        typesExistsQuantifiers = new HashSet<>();
        typesForAllQuantifiers = new HashSet<>();
        typesForVaryingQuantifiers = new HashSet<>();
        declaredUserConstants = new HashMap<>();
        usedConnectives = new HashSet<>();
        usedModalities = new HashSet<>();
        usedModalConnectivesToUsedModalities = new HashMap<>();
        usedSymbols = new HashSet<>();
        userTypes = new ArrayList<>();
    }

    public TransformContext transform() throws TransformationException, AnalysisException {
        HashSet<TransformationParameter> defaultParameters = new HashSet<>();
        defaultParameters.add(TransformationParameter.SEMANTICAL); // default
        return transform(defaultParameters);
    }

    public TransformContext transform(Set<TransformationParameter> params) throws TransformationException, AnalysisException {
        if (params.contains(TransformationParameter.SEMANTICAL) && params.contains(TransformationParameter.SYNTACTICAL))
            throw new TransformationException("Semantical and syntactical axiomatization of the modalities cannot be used together.");
        if (!params.contains(TransformationParameter.SEMANTICAL) && !params.contains(TransformationParameter.SYNTACTICAL)){
            params = new HashSet<>(params);
            params.add(TransformationParameter.SEMANTICAL); // default
        }
        this.thfAnalyzer = new ThfAnalyzer(this.originalRoot);
        this.thfAnalyzer.analyze();
        this.semanticsAnalyzer = new SemanticsAnalyzer(this.originalRoot, this.thfAnalyzer.semanticsNodes);
        this.semanticsAnalyzer.analyzeModalSemantics();
        return this.actualTransformation(params);
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
        return usedModalities.size() == 1;
    }

    /*
     * requires that the problem is actually monomodal and exactly one modality is actually used
     */
    private boolean theMonomodalProblemIsS5U() throws TransformationException{
        return normalizedRelationSuffixcontainsS5U(getNormalizedRelationSuffixFromNormalizedModalOperator(usedModalities.stream().findAny().get()));
    }

    private TransformContext actualTransformation(Set<TransformationParameter> params) throws TransformationException, AnalysisException {

        // collect all symbols to avoid variable capture when defining new bound variabls
        this.originalRoot.getLeafsDfs().forEach(n->this.usedSymbols.add(n.getLabel()));

        // transform role type
        for (Node type_statement : thfAnalyzer.typeRoleToNode.values()){
            for (Node l : type_statement.getLeafsDfs()) {

                // Lift types
                // $o only since we assume rigid constants
                if (Common.normalizeType(l.getLabel()).equals(Common.normalizeType("$o"))) {
                    l.setLabel(Common.embedded_truth_type);
                }

                // user types
                if (Common.normalizeType(l.getLabel()).equals(Common.normalizeType("$tType"))) userTypes.add(l);
            }
            // if it is a type declaration (and not a definition) add labels (name and type) to the map of constants (for varying domains)
            Optional<Node> typeable = type_statement.dfsRule("thf_typeable_formula");
            if (typeable.isPresent()) {
                String constant = typeable.get().getFirstLeaf().getLabel();
                String type = type_statement.dfsRule("thf_top_level_type").get().getFirstChild().toStringLeafs();
                String normalizedType = Common.normalizeType(type);
                if (this.declaredUserConstants.containsKey(normalizedType)) {
                    this.declaredUserConstants.get(normalizedType).add(constant);
                } else {
                    Set<String> newSet = new HashSet<>();
                    newSet.add(constant);
                    this.declaredUserConstants.put(normalizedType, newSet);
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
                if (Common.normalizeType(l.getLabel()).equals(Common.normalizeType("$o"))){
                    l.setLabel(Common.embedded_truth_type);

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

        for (Node statement : all){
            // substitute quantifications (! ?)
            // has to happen after embedding modalities since S5U needs a list of all modalities
            for (Node thf_quantified_formula : statement.dfsRuleAll("thf_quantified_formula")){
                embed_thf_quantified_formula(thf_quantified_formula);
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

        // remove semantical thf sentences
        for (Node thf : this.thfAnalyzer.semanticsNodes){
            Node semanticalRoot = thf.getParent().getParent().getParent(); // tPTP_input
            Node parent = semanticalRoot.getParent(); // tPTP_file
            parent.delChild(semanticalRoot);
        }

        // get embedding definitions for prepending and appending, appending those with reference to constants within the problem
        String modalDefinitions = preProblemInsertions(params);
        String auxiliaryDefinitions = postProblemInsertion();

        // extract user types (sorts with $tType) to be placed in front of problem
        extractUserTypes();

        return new TransformContext(modalDefinitions, auxiliaryDefinitions, this.userTypes, this.transformedRoot, this.originalRoot, this.thfAnalyzer, this.semanticsAnalyzer);
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
        String normalizedModalOperator = Connectives.getNormalizedModalOperator(operatorTree);
        usedModalities.add(normalizedModalOperator);
        String normalizedAccessibilityRelationSuffix = AccessibilityRelation.getNormalizedRelationSuffix(boxOrDiaIntLeaf);
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
                String normalizedType = Common.normalizeType(type);
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(normalizedType);

                // forall quantifier
                if (quantifier.equals("!")){

                    // constant domain case
                    if (domainType == SemanticsAnalyzer.DomainType.CONSTANT)
                        quant = new Node("t_quantifier", Quantification.embedded_forall(normalizedType));

                    // cumulative/decreasing S5U case for exactly one modality
                    else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                            theMonomodalProblemIsS5U() && // the modality is S5U
                            (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)){ // domains are either cumulative or decreasing
                    quant = new Node("t_quantifier", Quantification.embedded_forall(normalizedType));

                    // varying case and cumulative/decreasing case for non S5U
                    } else {
                        quant = new Node("t_quantifier", Quantification.embedded_forall_varying(normalizedType));
                        typesForVaryingQuantifiers.add(normalizedType);
                    }
                    typesForAllQuantifiers.add(normalizedType);

                // exists quantifier
                }else{

                    // constant domain case
                    if (domainType == SemanticsAnalyzer.DomainType.CONSTANT)
                        quant = new Node("t_quantifier", Quantification.embedded_exists(normalizedType));

                    // cumulative/decreasing S5U case for exactly one modality
                    else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                            normalizedRelationSuffixcontainsS5U(getNormalizedRelationSuffixFromNormalizedModalOperator(usedModalities.stream().findAny().get())) && // the modality is S5U
                            (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)){ // domains are either cumulative or decreasing
                        quant = new Node("t_quantifier", Quantification.embedded_exists(normalizedType));

                    // varying case and cumulative/decreasing case for non S5U
                    } else {
                        quant = new Node("t_quantifier", Quantification.embedded_exists_varying(normalizedType));
                        typesForVaryingQuantifiers.add(normalizedType);
                    }
                    typesExistsQuantifiers.add(normalizedType);
                }
                thf_typed_variable.addChildAt(quant,0);

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

    private String preProblemInsertions(Set<TransformationParameter> params) throws TransformationException {
        StringBuilder def = new StringBuilder();

        // declare world_type_declaration type
        def.append("% declare type for possible worlds\n");
        def.append(Common.world_type_declaration);
        def.append("\n\n");

        // introduce accessibility relations
        StringBuilder relationSb = new StringBuilder();
        for (String normalizedModalOperator : usedModalities) {
            String normalizedRelation = getNormalizedRelationFromNormalizedModalOperator(normalizedModalOperator);
            String normalizedRelationSuffix = getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator);
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
        boolean propertyDefined = false;
        Set<SemanticsAnalyzer.AccessibilityRelationProperty> allProperties = new HashSet<>();
        for (Set<SemanticsAnalyzer.AccessibilityRelationProperty> set : semanticsAnalyzer.modalityToAxiomList.values()){
            allProperties.addAll(set);
        }

        // Only define relation properties if we use a semantical embedding. If a syntactical
        // embedding is used, the respective properties are formulated as axioms on the box/dia operators
        // further below.
        if (params.contains(TransformationParameter.SEMANTICAL)) {
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
            for (String normalizedModalOperator : usedModalities) {
                String normalizedRelation = getNormalizedRelationFromNormalizedModalOperator(normalizedModalOperator);
                String normalizedRelationSuffix = getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator);
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
        Set<String> additionalModalitiesFromSyntacticalEmbedding = new HashSet<>();
        // debug output for the syntactical modality axiomatization
        //for (SemanticsAnalyzer.AccessibilityRelationProperty p : SemanticsAnalyzer.AccessibilityRelationProperty.values()){
        //    if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) {
        //        System.out.println(Connectives.applyPropertyToModality(p, usedModalities.stream().findAny().get()));
        //    }
        //}
        boolean constainsSyntacticalAxiom = false;
        if (params.contains(TransformationParameter.SYNTACTICAL) && !usedModalities.isEmpty()) {
            for (String normalizedModalOperator : usedModalities) {
                String normalizedRelationSuffix = getNormalizedRelationSuffixFromNormalizedModalOperator(normalizedModalOperator);
                for (SemanticsAnalyzer.AccessibilityRelationProperty p : getPropertiesFromNormalizedRelationSuffix(normalizedRelationSuffix) ) {
                    if (p != SemanticsAnalyzer.AccessibilityRelationProperty.K && p != SemanticsAnalyzer.AccessibilityRelationProperty.S5U) { // K and S5U do not have accessibility relation properties
                        syntacticalModalityAxioms.add(Connectives.applyPropertyToModality(p, normalizedModalOperator)); // may be dia or box
                        additionalModalitiesFromSyntacticalEmbedding.add(Connectives.getOppositeNormalizedModalOperator(normalizedModalOperator));
                        constainsSyntacticalAxiom = true;
                    }
                }
            }
        }
        if (constainsSyntacticalAxiom) {
            usedConnectives.add("mimplies");
        }

        // introduce mvalid for global consequence
        if (this.semanticsAnalyzer.axiomNameToConsequenceType.values().contains(SemanticsAnalyzer.ConsequenceType.GLOBAL)) {
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

        // introduce used operators which are not valid operator nor quantifiers
        if (!(usedConnectives.isEmpty() && usedModalities.isEmpty())) {
            def.append("% define nullary, unary and binary connectives which are no quantifiers\n");
            for (String o : usedConnectives) {
                def.append(Connectives.modalSymbolDefinitions.get(o));
                def.append("\n");
            }
            Set<String> modalitiesToDefine = new HashSet<>(); // contains all actually used modalities from the problem and the ones needed for defining the syntactical modality axioms
            modalitiesToDefine.addAll(additionalModalitiesFromSyntacticalEmbedding);
            modalitiesToDefine.addAll(usedModalities);
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

        // introduce quantifiers
        if (!typesForVaryingQuantifiers.isEmpty()) {
            def.append("% define exists-in-world predicates for quantified types and non-emptiness axioms\n");
            for (String normalizedType : typesForVaryingQuantifiers){
                def.append(Quantification.eiw_and_nonempty_th0(normalizedType));
                def.append("\n");
            }
            def.append("\n");
            /*
            // the above should be right because only varying quantifiers need eiw and nonempty
            for (String q: typesExistsQuantifiers) { // for each type that a  quantor is used, introduce
                // an according eiw predicate
                def.append(Quantification.eiw_and_nonempty_th0(q));
                def.append("\n");
            }
            for (String q: typesForAllQuantifiers) { // for each type that a  quantor is used, introduce
                // an according eiw predicate
                if (!typesExistsQuantifiers.contains(q)) {
                    def.append(Quantification.eiw_and_nonempty_th0(q));
                    def.append("\n");
                }
            }
            */
            StringBuilder domRestr = new StringBuilder();
            for (String normalizedType: typesForVaryingQuantifiers) { // insert domain restriction (cumulative etc) if necessary
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(normalizedType);
                if (problemIsMonomodal() && theMonomodalProblemIsS5U()){
                    // do not impose restrictions
                } else {
                    if (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE) {
                        domRestr.append(Quantification.cumulative_eiw_th0(normalizedType));
                        domRestr.append("\n");
                    } else if (domainType == SemanticsAnalyzer.DomainType.DECREASING) {
                        domRestr.append(Quantification.decreasing_eiw_th0(normalizedType));
                        domRestr.append("\n");
                    } // else nothing, since either constant or unrestricted varying
                }
            }
            if (domRestr.length() != 0){
                def.append("% define domain restrictions\n");
                def.append(domRestr);
                def.append("\n");
            }
        }

        if (!typesExistsQuantifiers.isEmpty()) {
            def.append("% define exists quantifiers\n");
            for (String normalizedType : typesExistsQuantifiers) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(normalizedType);

                // constant domain case
                if (domainType == SemanticsAnalyzer.DomainType.CONSTANT) {
                    def.append(Quantification.mexists_const_th0(normalizedType));
                }

                // cumulative/decreasing S5U case for exactly one modality
                else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                        theMonomodalProblemIsS5U() && // the modality is S5U
                        (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)) { // domains are either cumulative or decreasing
                    def.append(Quantification.mexists_const_th0(normalizedType));
                }

                // varying case and cumulative/decreasing case for non S5U
                else {
                    def.append(Quantification.mexists_varying_th0(normalizedType));
                }
                def.append("\n");
            }
            def.append("\n");
        }

        if (!typesForAllQuantifiers.isEmpty()) {
            def.append("% define for all quantifiers\n");
            for (String normalizedType : typesForAllQuantifiers) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(normalizedType);

                // constant domain case
                if (domainType == SemanticsAnalyzer.DomainType.CONSTANT) {
                    def.append(Quantification.mforall_const_th0(normalizedType));
                }

                // cumulative/decreasing S5U case for exactly one modality
                else if (problemIsMonomodal() && // there is EXACTLY one modality actually in use
                        theMonomodalProblemIsS5U() && // the modality is S5U
                        (domainType == SemanticsAnalyzer.DomainType.CUMULATIVE || domainType == SemanticsAnalyzer.DomainType.DECREASING)) { // domains are either cumulative or decreasing
                    def.append(Quantification.mforall_const_th0(normalizedType));
                }

                // varying case and cumulative/decreasing case for non S5U
                else {
                    def.append(Quantification.mforall_varying_th0(normalizedType));
                }
                def.append("\n");
            }
            def.append("\n");
        }

        // if using a syntactic embedding, postulate axioms on the operators now
        if (params.contains(TransformationParameter.SYNTACTICAL) && !syntacticalModalityAxioms.isEmpty()) {
            def.append("% define axioms on the modalities\n");
            for (String axiom : syntacticalModalityAxioms) {
                def.append(axiom);
                def.append("\n");
            }
            def.append("\n");
        }

        return def.toString();
    }

    private String postProblemInsertion() throws TransformationException {
        StringBuilder def = new StringBuilder();
        for (String normalizedType: this.declaredUserConstants.keySet()) {
            if (!normalizedType.equals(Common.normalizeType("$tType"))) {
                SemanticsAnalyzer.DomainType domainType = getDomainTypeFromNormalizedType(normalizedType);

                // the problem is monomodal and S5U
                if (problemIsMonomodal() && theMonomodalProblemIsS5U() && domainType != SemanticsAnalyzer.DomainType.VARYING){
                    // do nothing
                }

                // non- (S5U && monomodal)
                else {
                    if (this.typesForVaryingQuantifiers.contains(normalizedType)) {
                        // an eiw-predicate of type q already exists, we can just postulate an axiom
                        // that these constants exist at all worlds
                        for (String constant : declaredUserConstants.get(normalizedType)) {
                            def.append(Quantification.constant_eiw_th0(constant, normalizedType));
                            def.append("\n");
                        }
                    } else {
                        // define eiw_predicate of that type first
                        def.append(Quantification.eiw_and_nonempty_th0(normalizedType));
                        def.append("\n");
                        // now postulate as anbove
                        for (String constant : declaredUserConstants.get(normalizedType)) {
                            def.append(Quantification.constant_eiw_th0(constant, normalizedType));
                            def.append("\n");
                        }
                    }
                }
            }

        }
        if (def.toString().length() == 0) return "";
        return "% define exists-in-world assertion for user-defined constants\n" + def.toString();
    }
}
