package transformation.Definitions;

import java.util.HashMap;
import java.util.Map;

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

    public static String getBoxDefinitionUnimodal(){
        return "" +
                "thf( " + box_unimodal + "_type , type , ( " + box_unimodal + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                "thf( " + box_unimodal + "_def , definition , ( " + box_unimodal + " = (" +
                "^ [A:" + w + ">$o,W:" + w + "] : ! [V:" + w + "] : ( (" + AccessibilityRelation.getRelationNameUnimodal() + "@W@V) => (A@V) )" +
                "))).";
    }

    public static String getDiaDefinitionUnimodal(){
        return "" +
                "thf( " + dia_unimodal + "_type , type , ( " + dia_unimodal + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                "thf( " + dia_unimodal + "_def , definition , ( " + dia_unimodal + " = (" +
                "^ [A:" + w + ">$o,W:" + w + "] : ? [V:" + w + "] : ( (" + AccessibilityRelation.getRelationNameUnimodal() + "@W@V) => (A@V) )" +
                "))).";
    }
}
