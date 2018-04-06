package transformation.Definitions;

import exceptions.ParseException;
import parser.ParseContext;
import parser.ThfAstGen;
import util.tree.Node;

import java.util.Stack;


public class Common {

    /***************************************************************************
     * World type definition
     ***************************************************************************/
    public static final String w = "mworld";
    public static final String world_type_declaration = "" +
            "thf( " + w + "_type , type , ( " + w + ":$tType ) ).";
    public static final String embedded_truth_type = "(" + w + ">$o)";

    /***************************************************************************
     * Grounding definitions
     ***************************************************************************/
    public static final String mvalid = "" +
            "thf( mvalid_type , type , ( mvalid: (" + w + ">$o)>$o ) ).\n" +
            "thf( mvalid_def , definition , ( mvalid = (" +
            "^ [S:" + w + ">$o] : ! [W:" + w + "] : (S@W)" +
            "))).";

    public static final String mactual = "" +
            "thf( mactual_type , type , ( mactual: ( ( " + w + ">$o ) >$o ) ) ).\n" +
            "thf( mactual_def , definition , ( mactual = ( " +
            "^ [Phi:(" + w + ">$o)] : ( Phi @ mcurrentworld ) ) ) ).";


    public static String normalizeType(String type){
        String t = "";
        try {
            ParseContext pc = ThfAstGen.parse(type,"thf_top_level_type","name");
            Node root = pc.getRoot();
            Stack<Node> nodes_to_visit = new Stack<>();
            nodes_to_visit.push(root);

            // remove all parentheses
            while( !nodes_to_visit.isEmpty() ) {
                Node current_node = nodes_to_visit.pop();
                if (current_node.isLeaf() && current_node.getLabel().equals("(")){
                    current_node.getNextTopBranchingNode().delFirstChild();
                }
                if (current_node.isLeaf() && current_node.getLabel().equals(")")){
                    current_node.getNextTopBranchingNode().delLastChild();
                }
                nodes_to_visit.addAll(current_node.getChildren());
            }

            // add parentheses on every branching node in the parse tree
            nodes_to_visit = new Stack<>();
            if (root.hasOneLeaf()) return "(" + root.toStringLeafs() + ")";
            nodes_to_visit.push(root.getNextBotBranchingNode());
            while( !nodes_to_visit.isEmpty() ) {
                Node current_node = nodes_to_visit.pop();
                for (Node child : current_node.getChildren()){
                    Node next = child.getNextBotBranchingNode();
                    if (next != null) nodes_to_visit.add(next);
                }
                current_node.addChildAt(new Node("(","("),0);
                current_node.addChild(new Node(")",")"));
            }
            t = root.toStringLeafs();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

    // THIS IS UGLY - replace it by numbering quantifications later
    public static String escapeType(String type){
        String normalizedType = normalizeType(type);
        return normalizedType
                .replaceAll("[$]","_d_")
                .replaceAll("[>]","_t_")
                .replaceAll("[ ]","")
                .replaceAll("[(]","_o_")
                .replaceAll("[)]","_c_");
    }

}
