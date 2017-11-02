package transformation;


import exceptions.AnalysisException;
import util.tree.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ThfAnalyzer {

    private static final Logger log = Logger.getLogger( "default" );

    protected List<Node> semanticsNodes;
    protected List<Node> definitionNodes;
    protected List<Node> statementNodes;
    protected List<Node> otherNodes;

    protected Map<String, Node> axiomRoleToNode;
    protected Map<String, Node> typeRoleToNode;
    protected Map<String, Node> otherRoleToNode;

    private Node root;

    public ThfAnalyzer(Node root){
        semanticsNodes = new LinkedList<>();
        definitionNodes = new LinkedList<>();
        statementNodes = new LinkedList<>();
        otherNodes = new LinkedList<>();

        axiomRoleToNode = new HashMap<>();
        typeRoleToNode = new HashMap<>();
        otherRoleToNode = new HashMap<>();

        this.root = root;
    }

    public void analyze() throws AnalysisException{
        this.analyzeAst();
    }

    private void analyzeAst() throws AnalysisException {
        log.fine("Analyzing root.");
        if (this.root.getFirstChild() == null){
            throw new AnalysisException("tptp_file has no children");
        }
        for (Node i : this.root.getChild(0).getChildren()){
            if (!i.hasChildren()) throw new AnalysisException("tptp_input has no children");
            Node tptpInput = i.getChild(0);
            String tptp_input_rule = tptpInput.getRule();
            log.finest("Thf formula " + tptp_input_rule + " found: " + tptpInput.toStringLeafs().trim().replace("\n"," "));
            switch (tptp_input_rule) {
                //log.finest("Switch rule " + tptp_input_rule)
                //case "comment":
                //    break;
                //case "include":
                //    break;
                case "annotated_formula":
                    if (!tptpInput.hasChildren()) throw new AnalysisException("annotated_formula has no children");
                    String annotated_rule = tptpInput.getChild(0).getRule();
                    switch (annotated_rule) {
                        case "thf_annotated":
                            log.finest("thf_annotated formula found.");
                            analyzeThfAnnotated(tptpInput.getChild(0));
                            break;
                        default:
                            log.finest("No thf_annotated formula found. annotated_rule not in switch statement : " + annotated_rule);
                            break;
                    }
                    break;
                case "comment":
                    break;
                default:
                    log.finest("Case default: tptp_input not in switch statement: " + tptp_input_rule + " Tree:" + tptpInput.toString());
                    break;
            }
        }
    }

    private void analyzeThfAnnotated(Node node) throws AnalysisException{
        if (node.getChildren().size() < 3) throw new AnalysisException("Could not analyze thf_annotated because it has less than three children");
        String name = node.getChild(2).toStringLeafs();
        String role = node.getChild(4).toStringLeafs();
        Node formula = node.getChild(6);
        /* formula roles:   axiom 4| hypothesis | definition | assumption |
                            lemma | theorem | corollary | conjecture |
                            negated_conjecture | plain | type |
                            fi_domain | fi_functors | fi_predicates | unknown | logic */
        log.finest("Role " + role + " found.");
        switch (role){
            case "definition":
                definitionNodes.add(formula);
            case "type":
                typeRoleToNode.put(name, formula);
                break;
            case "logic":
                semanticsNodes.add(formula);
                break;
            case "axiom":
            case "hypothesis":
            case "assumption":
            case "lemma":
            case "theorem":
            case "corollary":
            case "conjecture":
            case "negated_conjecture":
            case "plain":
                statementNodes.add(formula);
                break;
            default:
                otherRoleToNode.put(name, formula);
        }
    }
}
