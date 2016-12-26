package transformation;


import exceptions.AnalysisException;
import util.tree.Node;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

/*
 * currently supports analyzing
 *  - rigid/flexible constants, default and individual constants
 *  - constant/varying/cumulative/decreasing domains, default and individual domains
 *  - local/global consequenceTypes of axioms, default and individual axioms
 *  - one modality by default value
 */

public class SemanticsAnalyzer {

    private static final Logger log = Logger.getLogger( "default" );

    public enum ConsequenceType{GLOBAL, LOCAL}
    public enum ConstantType{RIGID, FLEXIBLE}
    public enum DomainType{CONSTANT, VARYING, CUMULATIVE, DECREASING}
    public enum AccessibilityRelationProperty{K,T,B,D,FOUR,FIVE}

    protected Map<String, ConstantType> constantToConstantType;
    protected Map<String, ConsequenceType> axiomNameToConsequenceType;
    protected Map<String, DomainType> domainToDomainType;
    protected Map<String, Set<AccessibilityRelationProperty>> modalityToAxiomList;

    protected static String constantDefault = "$default";
    protected static String consequenceDefault = "$default";
    protected static String domainDefault = "$default";
    protected static String modalitiesDefault = "$default";

    static Map<String,AccessibilityRelationProperty> modal_axioms;
    private static Map<String,Set<AccessibilityRelationProperty>> modal_systems;
    private static Map<String,ConsequenceType> consequenceTypes;
    private static Map<String,ConstantType> constantTypes;
    private static Map<String,DomainType> domainTypes;
    private static Set<String> thfListSymbols;
    private static Predicate<Node> isAxiom;
    private static Predicate<Node> isSystem;
    private static Predicate<Node> isThfListSymbol;

    static{
        /*
        The following are supported modal semantics
         */
        constantTypes = new HashMap<>();
        constantTypes.put("$rigid",ConstantType.RIGID);
        constantTypes.put("$flexible",ConstantType.FLEXIBLE);
        domainTypes = new HashMap<>();
        domainTypes.put("$constant",DomainType.CONSTANT);
        domainTypes.put("$varying",DomainType.VARYING);
        domainTypes.put("$cumulative",DomainType.CUMULATIVE);
        domainTypes.put("$decreasing",DomainType.DECREASING);
        consequenceTypes = new HashMap<>();
        consequenceTypes.put("$local",ConsequenceType.LOCAL);
        consequenceTypes.put("$global",ConsequenceType.GLOBAL);
        modal_axioms = new HashMap<>();
        modal_axioms.put("$modal_axiom_K",AccessibilityRelationProperty.K);
        modal_axioms.put("$modal_axiom_T",AccessibilityRelationProperty.T);
        modal_axioms.put("$modal_axiom_B",AccessibilityRelationProperty.B);
        modal_axioms.put("$modal_axiom_D",AccessibilityRelationProperty.D);
        modal_axioms.put("$modal_axiom_4",AccessibilityRelationProperty.FOUR);
        modal_axioms.put("$modal_axiom_5",AccessibilityRelationProperty.FIVE);
        modal_systems = new HashMap<>();
        modal_systems.put("$modal_system_K", new HashSet<>(Arrays.asList(AccessibilityRelationProperty.K)));
        modal_systems.put("$modal_system_T", new HashSet<>(Arrays.asList(AccessibilityRelationProperty.K, AccessibilityRelationProperty.T)));
        modal_systems.put("$modal_system_D", new HashSet<>(Arrays.asList(AccessibilityRelationProperty.K, AccessibilityRelationProperty.D)));
        modal_systems.put("$modal_system_S4", new HashSet<>(Arrays.asList(AccessibilityRelationProperty.K, AccessibilityRelationProperty.T, AccessibilityRelationProperty.FOUR)));
        modal_systems.put("$modal_system_S5", new HashSet<>(Arrays.asList(AccessibilityRelationProperty.K, AccessibilityRelationProperty.T, AccessibilityRelationProperty.FIVE)));

        thfListSymbols = new HashSet<>();
        thfListSymbols.add(",");
        thfListSymbols.add("[");
        thfListSymbols.add("]");

        isAxiom = x->modal_axioms.containsKey(x);
        isSystem = x->modal_systems.containsKey(x);
        isThfListSymbol = x->thfListSymbols.contains(x);
    }

    private Node root;
    private List<Node> semanticsNodes;

    public SemanticsAnalyzer(Node root, List<Node> semanticsNodes){
        this.root = root;
        this.semanticsNodes = semanticsNodes;
        axiomNameToConsequenceType = new HashMap<>();
        constantToConstantType = new HashMap<>();
        domainToDomainType = new HashMap<>();
        modalityToAxiomList = new HashMap<>();
    }

    public void analyzeModalSemantics() throws AnalysisException{
        log.fine("Analyzing modal semantics.");
        log.fine("There are " + this.semanticsNodes.size() + " semantical thf sentences.");

        // iterate over all thf sentences containing semantics
        for (Node semanticsNode : this.semanticsNodes){
            log.finest("Analyzing semantics sentence " + semanticsNode.getParent() );

            // find $modal token first
            Optional<Node> modalToken = semanticsNode.dfsLabel("$modal");
            if (!modalToken.isPresent()){
                log.finest("$modal token not found.");
                continue;
            }
            if (!modalToken.get().getParent().getParent().getRule().equals("logic_defn_rule")){
                log.finest("$modal has not logic_defn_rule as parent");
                continue;
            }
            log.finest("Found $modal token.");

            // look for all logic_defn_rule nodes and analyze them
            // this is sufficient since we do not allow logical constructs
            // on constants, quantification and consequence
            // logical constructs of modalities are not implemented yet
            List<Node> logic_defn_rules = modalToken.get().getParent().getParent().getLastChild().dfsRuleAllToplevel("logic_defn_rule");
            log.finest("There are " + logic_defn_rules.size() + " logic_defn_rules in sentence " + modalToken.get());
            for (Node rule : logic_defn_rules){
                String rule_name = rule.getChild(0).toStringLeafs();
                log.finest("Found semantical rule " + rule_name + " in sentence " + rule);
                switch (rule_name){
                    case "$constants":
                        analyzeConstant(rule.getLastChild());
                        break;
                    case "$quantification":
                        analyzeDomain(rule.getLastChild());
                        break;
                    case "$consequence":
                        analyzeConsequence(rule.getLastChild());
                        break;
                    case "$modalities":
                        analyzeModalities(rule.getLastChild());
                        break;
                    default:
                        log.finest("Semantic rule " + rule_name + " is not a valid rule by name.");
                }
            }
        }
        log.fine("Semantics summary:\n" + this.toString());
    }

    private void analyzeConstant(Node node){
        log.finest("Analyzing constants in " + node);

        // not a list => only default value
        if (node.hasOneLeaf()){
            log.finest("Found constant default value " + node.toStringLeafs() + ". No further values available.");
            this.putConstant(SemanticsAnalyzer.constantDefault, node.toStringLeafs());
        }

        // list
        else{
            // find all entries of the list which means find all thf_formula_list rules
            List<Node> thf_formula_lists = node.getParent().getLastChild().dfsRuleAll("thf_formula_list");
            thf_formula_lists.replaceAll(n->n.getFirstChild()); // always get the first child of a thf_formula_list
            for (Node entry : thf_formula_lists){

                // if default value
                if (entry.hasOneLeaf()){
                    log.finest("Found constant default value " + entry.toStringLeafs() + ".");
                    this.putConstant(SemanticsAnalyzer.constantDefault, entry.toStringLeafs());
                }

                // else particular constant
                else{
                    Optional<Node> rule = entry.dfsRule("logic_defn_rule");
                    if (!rule.isPresent()){
                        log.finest("Invalid constant semantics " + entry);
                    }
                    else{
                        log.finest("Found constant " + rule.get().getFirstChild().toStringLeafs() + " with value " + rule.get().getLastChild().toStringLeafs() );
                        this.putConstant(rule.get().getFirstChild().toStringLeafs(), rule.get().getLastChild().toStringLeafs());
                    }
                }
            }
        }
    }

    private void analyzeDomain(Node node){
        log.finest("Analyzing domains in " + node);

        // not a list => only default value
        if (node.hasOneLeaf()){
            log.finest("Found domain default value " + node.toStringLeafs() + ". No further values available.");
            this.putDomain(SemanticsAnalyzer.domainDefault, node.toStringLeafs());
        }

        // list
        else{
            // find all entries of the list which means find all thf_formula_list rules
            List<Node> thf_formula_lists = node.getParent().getLastChild().dfsRuleAll("thf_formula_list");
            thf_formula_lists.replaceAll(n->n.getFirstChild()); // always get the first child of a thf_formula_list
            for (Node entry : thf_formula_lists){

                // if default value
                if (entry.hasOneLeaf()){
                    log.finest("Found domain default value " + entry.toStringLeafs() + ".");
                    this.putDomain(SemanticsAnalyzer.domainDefault, entry.toStringLeafs());
                }

                // else particular constant
                else{
                    Optional<Node> rule = entry.dfsRule("logic_defn_rule");
                    if (!rule.isPresent()){
                        log.finest("Invalid domain semantics " + entry);
                    }
                    else{
                        log.finest("Found domain " + rule.get().getFirstChild().toStringLeafs() + " with value " + rule.get().getLastChild().toStringLeafs() );
                        this.putDomain(rule.get().getFirstChild().toStringLeafs(), rule.get().getLastChild().toStringLeafs());
                    }
                }
            }
        }
    }

    private void analyzeConsequence(Node node){
        log.finest("Analyzing consequenceTypes in " + node);

        // not a list => only default value
        if (node.hasOneLeaf()){
            log.finest("Found consequence default value " + node.toStringLeafs() + ". No further values available.");
            this.putConsequence(SemanticsAnalyzer.consequenceDefault, node.toStringLeafs());
        }

        // list
        else{
            // find all entries of the list which means find all thf_formula_list rules
            List<Node> thf_formula_lists = node.getParent().getLastChild().dfsRuleAll("thf_formula_list");
            thf_formula_lists.replaceAll(n->n.getFirstChild()); // always get the first child of a thf_formula_list
            for (Node entry : thf_formula_lists){

                // if default value
                if (entry.hasOneLeaf()){
                    log.finest("Found consequence default value " + entry.toStringLeafs() + ".");
                    this.putConsequence(SemanticsAnalyzer.consequenceDefault, entry.toStringLeafs());
                }

                // else particular constant
                else{
                    Optional<Node> rule = entry.dfsRule("logic_defn_rule");
                    if (!rule.isPresent()){
                        log.finest("Invalid consequence semantics " + entry);
                    }
                    else{
                        log.finest("Found consequence " + rule.get().getFirstChild().toStringLeafs() + " with value " + rule.get().getLastChild().toStringLeafs() );
                        this.putConsequence(rule.get().getFirstChild().toStringLeafs(), rule.get().getLastChild().toStringLeafs());
                    }
                }
            }
        }
    }

    /*
     * supports only one modality by default value
     */
    private void analyzeModalities(Node node){
        log.finest("Analyzing modalities in " + node);
        //System.out.println(node);

        // only default value which is an axiom or a system or a list of axioms
        if (!node.dfsRule("logic_defn_rule").isPresent()){
            Set<AccessibilityRelationProperty> propertyList = resolveModalityEntry(node.toStringLeafs());
            log.finest("Found default value " + accessibilityRelationPropertyListToString(propertyList) + " for modalities only");
            this.modalityToAxiomList.put(modalitiesDefault,propertyList);
        }

        // at least one modality declaration which is a list with rules for specific modalities and
        // probably a default value which is an axiom or a system or a list of axioms
        // TODO
        else{
            log.warning("Modalities are default value + others. This is not supported yet");
            // find all logical modalSymbolDefinitions
            List<Node> thf_logic_defns = node.dfsRuleAll("logic_defn");
            for (Node d : thf_logic_defns){
                Optional<Node> logic_defn_rule = d.dfsRule("logic_defn_rule");

                // rule means specific modality
                if (logic_defn_rule.isPresent()){

                }
                // otherwise it is default value
                else{

                }
            }
        }

    }

    private void putConstant(String name, String value){
        ConstantType t = constantTypes.getOrDefault(value,null);
        if (t == null){
            log.finest("Value " + value + " is not a valid value for constants.");
            return;
        }
        constantToConstantType.put(name, t);
    }

    private void putDomain(String name, String value){
        DomainType t = domainTypes.getOrDefault(value,null);
        if (t == null){
            log.finest("Value " + value + " is not a valid value for constants.");
            return;
        }
        domainToDomainType.put(name, t);
    }

    private void putConsequence(String name, String value){
        ConsequenceType t = consequenceTypes.getOrDefault(value,null);
        if (t == null){
            log.finest("Value " + value + " is not a valid value for constants.");
            return;
        }
        axiomNameToConsequenceType.put(name, t);
    }

    public static String accessibilityRelationPropertyListToString(Set<AccessibilityRelationProperty> set){
        return "[" + String.join(",",set.stream().map(a->a.name()).toArray(String[]::new)) + "]";
    }

    private static Set<AccessibilityRelationProperty> resolveModalityEntry(String entry){
        Set<AccessibilityRelationProperty> axioms = new HashSet<>();
        log.finest("Resolving modality entry " + entry);

        // $modal_axiom_4
        log.finest("Trying resolving to single modal axiom.");
        AccessibilityRelationProperty p = modal_axioms.getOrDefault(entry,null);
        if (!(p == null)){
            log.finest("Modality entry was the valid axiom "+ p.name());
            axioms.add(p);
            return axioms;
        }

        // $modal_system_S4
        log.finest("Fail.Trying resolving to modal system.");
        axioms = modal_systems.getOrDefault(entry, null);
        if (!(axioms == null)){
            log.finest("Modality entry was the valid system " + entry + " corresponding to " + accessibilityRelationPropertyListToString(axioms));
            return axioms;
        }

        // [$modal_system_S4]
        log.finest("Fail.Trying resolving to modal system as unary list.");
        axioms = modal_systems.getOrDefault(entry.replaceAll("\\[|\\]|,","").trim(), null);
        if (!(axioms == null)){
            log.finest("Modality entry was the valid system " + entry + " corresponding to " + accessibilityRelationPropertyListToString(axioms));
            return axioms;
        }

        // [$modal_axiom_4]
        // [$modal_axiom_4,$modal_axiom_5]
        log.finest("Fail.Trying resolving to modal axiom list.");
        String[] axiomList = entry.replaceAll("\\[","").replaceAll("\\]","").split(",");
        axioms = new HashSet<>();
        for (String a : axiomList){
            p = modal_axioms.getOrDefault(a.trim(),null);
            if (!(p == null)){
                log.finest("Found axiom " + p.name());
                axioms.add(p);
            } else {
                log.finest("This is not a valid axiom: " + p);
            }
        }
        if (!axioms.isEmpty()){
            return axioms;
        }

        log.finest("Fail.No valid modality property found.");
        return new HashSet<>();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("CONSTANTS:      ");
        sb.append(constantDefault);
        sb.append("=");
        if (this.constantToConstantType.containsKey(constantDefault)){
            sb.append(this.constantToConstantType.get(constantDefault).name());        }
        else{
            sb.append("N/A");
        }
        for (String x : this.constantToConstantType.keySet()){
            if (x.equals(constantDefault)) continue;
            sb.append(", ");
            sb.append(x);
            sb.append("=");
            sb.append(this.constantToConstantType.get(x).name());
        }
        sb.append("\n");
        sb.append("QUANTIFICATION: ");
        sb.append(domainDefault);
        sb.append("=");
        if (this.domainToDomainType.containsKey(domainDefault)){
            sb.append(this.domainToDomainType.get(domainDefault).name());
        }
        else{
            sb.append("N/A");
        }
        for (String x : this.domainToDomainType.keySet()){
            if (x.equals(domainDefault)) continue;
            sb.append(", ");
            sb.append(x);
            sb.append("=");
            sb.append(this.domainToDomainType.get(x).name());
        }
        sb.append("\n");
        sb.append("CONSEQUENCE:    ");
        sb.append(consequenceDefault);
        sb.append("=");
        if (this.axiomNameToConsequenceType.containsKey(consequenceDefault)){
            sb.append(this.axiomNameToConsequenceType.get(consequenceDefault).name());
        }
        else{
            sb.append("N/A");
        }
        for (String x : this.axiomNameToConsequenceType.keySet()){
            if (x.equals(consequenceDefault)) continue;
            sb.append(", ");
            sb.append(x);
            sb.append("=");
            sb.append(this.axiomNameToConsequenceType.get(x).name());
        }
        sb.append("\n");
        sb.append("MODALITIES:     ");
        sb.append(modalitiesDefault);
        sb.append("=");
        if (this.modalityToAxiomList.containsKey(modalitiesDefault)) {
            sb.append(accessibilityRelationPropertyListToString(this.modalityToAxiomList.get(modalitiesDefault)));
        }
        else{
            sb.append("N/A");
        }
        for (String x : this.modalityToAxiomList.keySet()){
            if (x.equals(modalitiesDefault)) continue;
            sb.append(", ");
            sb.append(x);
            sb.append("=");
            sb.append(accessibilityRelationPropertyListToString(this.modalityToAxiomList.get(x)));
        }
        return sb.toString();
    }
}
