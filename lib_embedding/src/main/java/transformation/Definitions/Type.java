package transformation.Definitions;

import exceptions.ImplementationError;
import exceptions.ParseException;
import parser.ParseContext;
import parser.ThfAstGen;
import util.Node;

import java.util.*;

public class Type {
    private static Map<String,Type> usedTypes;
    private static Type truthType;
    static {
        Type.usedTypes = new HashMap<>();
        truthType = Type.getType("$o");
    }
    private String normalizedType;
    private String escapedType = null;
    private String liftedNormalizedType = null;
    private String liftedEscapedType = null;

    private Type(String normalizedType) {
        this.normalizedType = normalizedType;
    }

    public static Type getType(String type, boolean add) {
        String normalizedType = Type.normalizeType(type);
        Type ret = Type.usedTypes.get(normalizedType);
        if (ret == null) {
            ret = new Type(normalizedType);
            if (add) Type.usedTypes.put(normalizedType, ret);
        }
        return ret;
    }

    public boolean equals(Type type) {
        if (this.getNormalizedType().equals(type.getNormalizedType())) return true;
        return false;
    }

    public static Type getType(String type) {
        return Type.getType(type, true);
    }

    public String getNormalizedType() {
        return this.normalizedType;
    }

    public String getEscapedType() {
        if (this.escapedType == null) this.escapedType = Type.escapeType(this.getNormalizedType());
        return this.escapedType;
    }

    public String getliftedNormalizedType() {
        if (this.liftedNormalizedType == null) this.liftedNormalizedType = Type.liftType(getNormalizedType());
        return this.liftedNormalizedType;
    }

    public String getLiftedEscapedType() {
        if (this.liftedEscapedType == null) {
            this.liftedEscapedType = Type.escapeType(this.getliftedNormalizedType());
        }
        return this.liftedEscapedType;
    }

    // for better performance performance
    public static Type getTruthType()  {
        return truthType;
    }

    private static String escapeType(String normalizedType){
        return normalizedType
                .replaceAll("[$]","_d_")
                .replaceAll("[>]","_t_")
                .replaceAll("[ ]","")
                .replaceAll("[(]","_o_")
                .replaceAll("[)]","_c_");
    }

    private static String liftType(String normalizedType) {
        ParseContext pc = null;
        try {
            pc = ThfAstGen.parse(normalizedType,"thf_top_level_type","name");
        } catch (ParseException e) {
            throw new ImplementationError("normalized type not parsable: " + normalizedType);
        }
        Node root = pc.getRoot();
        for (Node l : root.getLeafsDfs()) {
            if (Type.getType(l.getLabel(), false).equals(Type.getTruthType())) {
                l.setLabel(Common.embedded_truth_type);
            }
        }
        return root.toStringLeafs();
    }

    @Override
    public String toString() {
        return this.normalizedType;
    }

    private static String normalizeType(String type){
        String debugType = type;
        try {
            // remove multiple parentheses
            while (type.startsWith("(") && type.endsWith(")")){
                type = type.substring(1,type.length()-1);
            }

            // parse
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
            //System.out.println("TYPE:"+type);
            //if (type.equals("@")) throw new Error("TYPE");
            //System.out.println("TOSTRINGLEAFS:"+root.toStringLeafs());
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
            return root.toStringLeafs();
        } catch (ParseException e) {
            throw new ImplementationError("Could not parse type \"" + debugType + "\"");
        }
    }
}
