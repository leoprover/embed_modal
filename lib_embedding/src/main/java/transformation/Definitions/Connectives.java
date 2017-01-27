package transformation.Definitions;

import exceptions.AnalysisException;
import util.tree.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Connectives {
    private static final String w = Common.w;

    public static final String mtrue = "" +
            "thf( mtrue_type , type , ( mtrue: " + w + ">$o ) ).\n" +
            "thf( mtrue , definition , ( mtrue = (" +
            "^ [W:" + w + "] : $true" +
            "))).";

    public static final String mfalse = "" +
            "thf( mfalse_type , type , ( mfalse: " + w + ">$o ) ).\n" +
            "thf( mfalse , definition , ( mfalse = (" +
            "^ [W:" + w + "] : $false" +
            "))).";

    public static final String mnot = "" +
            "thf( mnot_type , type , ( mnot: (" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mnot , definition , ( mnot = (" +
            "^ [A:" + w + ">$o,W:" + w + "] : ~(A@W)" +
            "))).";

    public static final String mor = "" +
            "thf( mor_type , type , ( mor: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mor , definition , ( mor = ("+
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) | (B@W) )" +
            "))).";
    public static final String mnor = "" +
            "thf( mnor_type , type , ( mnor: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mnor , definition , ( mnor = ("+
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : (  (A@W) ~| (B@W) )" +
            "))).";

    public static final String mand = "" +
            "thf( mand_type,type , ( mand: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mand , definition , ( mand = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) & (B@W) )" +
            "))).";

    public static final String mnand = "" +
            "thf( mnand_type,type , ( mnand: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mnand , definition , ( mn1and = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) ~& (B@W) )" +
            "))).";

    public static final String mimplies = "" +
            "thf( mimplies_type , type , ( mimplies: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mimplies , definition , ( mimplies = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) => (B@W) )" +
            "))).";

    public static final String mimpliesreversed = "" +
            "thf( mimpliesreversed_type , type , ( mimpliesreversed: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mimpliesreversed , definition , ( mimpliesreversed = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) <= (B@W) )" +
            "))).";

    public static final String mequiv = "" +
            "thf( mequiv_type , type , ( mequiv: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mequiv , definition , ( mequiv = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) <=> (B@W) )" +
            "))).";
    public static final String mnequiv = "" +
            "thf( mnequiv_type , type , ( mnequiv: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mnequiv , definition , ( mnequiv = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) <~> (B@W) )" +
            "))).";

    // TODO embed definite description
    // TODO embed choice

    public static final Map<String,String> modalSymbolDefinitions;
    static{
        modalSymbolDefinitions = new HashMap<>();
        modalSymbolDefinitions.put("mtrue",mtrue);
        modalSymbolDefinitions.put("mfalse",mfalse);
        modalSymbolDefinitions.put("mnot",mnot);
        modalSymbolDefinitions.put("mor",mor);
        modalSymbolDefinitions.put("mnor",mnor);
        modalSymbolDefinitions.put("mand",mand);
        modalSymbolDefinitions.put("mnand",mnand);
        modalSymbolDefinitions.put("mimplies",mimplies);
        modalSymbolDefinitions.put("mimpliesreversed",mimpliesreversed);
        modalSymbolDefinitions.put("mequiv",mequiv);
        modalSymbolDefinitions.put("mnequiv",mnequiv);
    }

    /********************************************************
     * Box / Dia
     ********************************************************/

    public static final String box_prefix = "mbox";
    public static final String dia_prefix = "mdia";
    public static final String box_unimodal = box_prefix;
    public static final String dia_unimodal = dia_prefix;
    public static final String box_int_prefix = box_prefix + "_int";
    public static final String dia_int_prefix = dia_prefix + "_int";

    public static String getModalOperatorDefinition(String normalizedModalOperator){
        return "" +
                "thf( " + normalizedModalOperator + "_type , type , ( " + normalizedModalOperator + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                "thf( " + normalizedModalOperator + "_def , definition , ( " + normalizedModalOperator + " = (" +
                "^ [A:" + w + ">$o,W:" + w + "] : ! [V:" + w + "] : ( (" + AccessibilityRelation.getNormalizedRelationName(normalizedModalOperator) + "@W@V) => (A@V) )" +
                "))).";
    }

    /*
     * modalOperator has to be the tree which contains all parts of the modal operator
     * e.g. for $box @ 1 it contains both $box and 1
     */
    public static String getNormalizedModalOperator(Node n) throws AnalysisException {
        // TODO assert this is a valid operator
        String op = n.toStringLeafs();
        Node firstLeaf = n.getFirstLeaf();
        // $box
        if (firstLeaf.getLabel().equals("$box")){
            return box_unimodal;
        }
        // $dia
        if (firstLeaf.getLabel().equals("$dia")){
            return dia_unimodal;
        }

        Optional<Node> unsigned_integer = n.getLastChild().dfsRule("unsigned_integer");
        // $box_int @ <int>
        if (firstLeaf.getLabel().equals("$box_int")){
            if (unsigned_integer.isPresent()){
                return box_int_prefix + "_" + unsigned_integer.get().getFirstLeaf().getLabel();
            } else {
                throw new AnalysisException("$box_int was not applied to an unsigned_integer: " + n.toString());
            }
        }
        // $dia_int @ <int>
        if (firstLeaf.getLabel().equals("$dia_int")){
            if (unsigned_integer.isPresent()){
                return dia_int_prefix + "_" + unsigned_integer.get().getFirstLeaf().getLabel();
            } else {
                throw new AnalysisException("$box_int was not applied to an unsigned_integer: " + n.toString());
            }
        }

        throw new AnalysisException("Invalid modal operator: " + n.toString());

    }
}
