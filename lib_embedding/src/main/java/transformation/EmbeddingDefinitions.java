package transformation;

import java.util.HashMap;
import java.util.Map;

public class EmbeddingDefinitions {

    /***************************************************************************
     * World type definition
     ***************************************************************************/
    public static final String w = "w_type";
    public static final String world_type = "" +
            "thf( w_type_type , type , ( " + w + ":$tType ) ).";
    public static final String truth_type = "(" + w + ">$o)";

    /***************************************************************************
     * Valid definition
     ***************************************************************************/
    public static final String mvalid = "" +
            "thf( mvalid_type , type , ( mvalid: (" + w + ">$o)>$o ) ).\n" +
            "thf( mvalid , definition , ( mvalid = (" +
            "^ [S:" + w + ">$o] : ! [W:" + w + "] : (S@W)" +
            "))).";

    /***************************************************************************
     * Accessibility relation properties
     ***************************************************************************/
    public static final String mreflexive = "" +
            "thf( mreflexive_type , type , ( mreflexive : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mreflexive , definition , ( mreflexive = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + "] : (R@A@A)" +
            "))).";

    public static final String msymmetric = "" +
            "thf( msymmetric_type , type , ( msymmetric : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( msymmetric , definition , ( msymmetric = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + "] : ( (R@A@B) => (R@B@A) )" +
            "))).";

    public static final String mtransitive = "" +
            "thf( mtransitive_type , type , ( mtransitive : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mtransitive , definition , ( mtransitive = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + ",C:" + w + "] : ( ( (R@A@B) & (R@B@C) ) => (R@A@C) )" +
            "))).";

    public static final String mserial = "" +
            "thf( mserial_type , type , ( mserial : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mserial , definition , ( mserial = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + "] : ? [B:" + w + "] : (R@A@B)" +
            "))).";

    public static final String meuclidean = "" +
            "thf( meuclidean_type , type , ( meuclidean : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( meuclidean , definition , ( meuclidean = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + ",C:" + w + "] : ( ( (R@A@B) & (R@A@C) ) => (R@B@C) )" +
            "))).";

    /***************************************************************************
     * Modal operators
     ***************************************************************************/
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
    public static final String meq = "" +
            "thf( meq_type , type , ( meq: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( meq , definition , ( meq = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) = (B@W) )" +
            "))).";
    public static final String mneq = "" +
            "thf( mneq_type , type , ( mneq: (" + w + ">$o)>(" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mneq , definition , ( mneq = (" +
            "^ [A:" + w + ">$o,B:" + w + ">$o,W:" + w + "] : ( (A@W) != (B@W) )" +
            "))).";
    public static final String mbox = "" +
            "thf( mbox_type , type , ( mbox: (" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mbox , definition , ( mbox = (" +
            "^ [A:" + w + ">$o,W:" + w + "] : ! [V:" + w + "] : ( (rel_r@W@V) => (A@V) )" +
            "))).";

    public static final String mdia = "" +
            "thf( mdia_type , type , ( mdia: (" + w + ">$o)>" + w + ">$o) ).\n" +
            "thf( mdia , definition , ( mdia = (" +
            "^ [A:" + w + ">$o,W:" + w + "] : ? [V:" + w + "] : ( (rel_r@W@V) & (A@V) )" +
            "))).";

    /***************************************************************************
     * Quantification TH1
     ***************************************************************************/
    /*
    public static final String mforall_const_th1 = "" +
            "thf( mforall_const_type , type , ( mforall_const : !> [T:$tType] : (T > " + w + " > $o) > " + w + " > $o ) ).\n" +
            "thf( mforall_const , definition , ( mforall_const = (" +
            "^ [A:T>" + w + ">$o,W:" + w + "] : ! [X:T] : (A @ X @ W)" +
            "))).";

    public static final String mexists_const_th1 = "" +
            "thf( mforall_const_type , type , ( mexists_const : !> [T:$tType] : (T > " + w + " > $o) > " + w + " > $o ) ).\n" +
            "thf( mforall_const , definition , ( mexists_const = (" +
            "^ [A:T>" + w + ">$o,W:" + w + "] : ? [X:T] : (A @ X @ W)" +
            "))).";
    */

    public static final Map<String,String> modalSymbolDefinitions;
    public static final Map<SemanticsAnalyzer.AccessibilityRelationProperty,String> accessibilityRelationPropertyDefinitions;
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
        //modalSymbolDefinitions.put("meq",meq);
        //modalSymbolDefinitions.put("mneq",mneq);
        modalSymbolDefinitions.put("mbox",mbox);
        modalSymbolDefinitions.put("mdia",mdia);
        /*
        modalSymbolDefinitions.put("mforall_const_th1",mforall_const_th1);
        modalSymbolDefinitions.put("mexists_const_th1",mexists_const_th1);
        */

        accessibilityRelationPropertyDefinitions = new HashMap<>();
        accessibilityRelationPropertyDefinitions.put(SemanticsAnalyzer.AccessibilityRelationProperty.T, "mreflexive");
        accessibilityRelationPropertyDefinitions.put(SemanticsAnalyzer.AccessibilityRelationProperty.B, "msymmetric");
        accessibilityRelationPropertyDefinitions.put(SemanticsAnalyzer.AccessibilityRelationProperty.D, "mserial");
        accessibilityRelationPropertyDefinitions.put(SemanticsAnalyzer.AccessibilityRelationProperty.FOUR, "mtransitive");
        accessibilityRelationPropertyDefinitions.put(SemanticsAnalyzer.AccessibilityRelationProperty.FIVE, "meuclidean");

    }

    /*
     * like a map from name to definition e.g. "mserial" -> EmbeddingDefinitions.mserial
     * returns null if
     */
    public static String getAccessibilityRelationPropertyDefinitionByName(String name){
        try {
            return (String) EmbeddingDefinitions.class.getDeclaredField(name).get(null);
        } catch (IllegalAccessException e) {
            // should not happen since definitions are public
            return null;
        } catch (NoSuchFieldException e) {
            // if it not a valid property return null as if using a map
            return null;
        }
    }

    public static String getAccessibilityRelationPropertyDefinitionByAccessibilityRelationProperty(SemanticsAnalyzer.AccessibilityRelationProperty p){
        return getAccessibilityRelationPropertyDefinitionByName(accessibilityRelationPropertyDefinitions.get(p));
    }


    /***************************************************************************
     * Quantification TH0
     ***************************************************************************/

    public static String mforall_const_th0(String type){
        StringBuilder sb = new StringBuilder();
        type = normalizeType(type);
        String escapedType = escapeType(type);
        // concrete type sentence from
        // thf( mforall_const_type , type , ( mforall_const : !> [T:$tType] : (T > " + w + " > $o) > " + w + " > $o ) ).
        sb.append("thf( mforall_const_type_");
        sb.append(escapedType);
        sb.append(" , type , ( mforall_const_");
        sb.append(escapedType);
        sb.append(" : ( ( ( ");
        sb.append(type);
        sb.append(" ) > ( " + w + " > $o ) ) > " + w + " > $o ) ) ).");
        sb.append("\n");
        // concrete definition sentence from
        // thf( mforall_const , definition , ( mforall_const = (
        // ^ [A:T>" + w + ">$o,W:" + w + "] : ! [X:T] : (A @ X @ W)
        //        ))).
        sb.append("thf( mforall_const_");
        sb.append(escapedType);
        sb.append(" , definition , ( mforall_const_");
        sb.append(escapedType);
        sb.append(" = ( ^ [A:(");
        sb.append(type);
        sb.append(")>" + w + ">$o,W:" + w + "] : ! [X:(");
        sb.append(type);
        sb.append(")] : (A @ X @ W)");
        sb.append("))).");
        return sb.toString();
    }

    public static String mexists_const_th0(String type){
        return mforall_const_th0(type).replaceAll("forall","exists").replaceAll("!","?");
    }

    public static String embedded_forall(String type){
        return "mforall_const_" + escapeType(type);
    }

    public static String embedded_exists(String type){
        return "mexists_const_" + escapeType(type);
    }

    public static String normalizeType(String type){
        return type.replaceAll("[ ]","");
    }

    public static String escapeType(String type){
        return type
                .replaceAll("[$]","_d_")
                .replaceAll("[>]","_t_")
                .replaceAll("[ ]","")
                .replaceAll("[(]","_o_")
                .replaceAll("[)]","_c_");
    }

    /***************************************************************************
     * Accessibility Relation declaration and properties
     ***************************************************************************/
    public static String declareRelation(String relation){
        // TODO alter for multimodal systems
        String escapedRelation = escapeRelation(relation);
        return "thf( rel_" + escapedRelation + "_type , type , ( rel_" + escapedRelation + ":" + w + ">" + w + ">$o) ).";
    }

    public static String applyPropertyToRelation(SemanticsAnalyzer.AccessibilityRelationProperty p, String relation){
        // TODO alter formultimodal systems
        String escapedRelation = escapeRelation(relation);
        return "thf( " + escapedRelation + "_" + accessibilityRelationPropertyDefinitions.get(p) + " , axiom , ( " +
                accessibilityRelationPropertyDefinitions.get(p) + " @ " + escapedRelation + " ) ).";
    }




    private static String escapeRelation(String relation){
        // TODO for multimodal systems
        return relation;
    }

    /*
     * for debug
     */
    public static String getAllDefinitions(){
        StringBuilder defs = new StringBuilder();
        defs.append("% declare world type\n");
        defs.append(world_type);
        defs.append("\n\n% declare relation\n");
        defs.append(declareRelation("r"));
        defs.append("\n\n% accessibility relation definitions\n");
        accessibilityRelationPropertyDefinitions.entrySet().forEach(n->{defs.append(getAccessibilityRelationPropertyDefinitionByName(n.getValue()));defs.append("\n");});
        defs.append("\n\n% assign properties to relations\n");
        accessibilityRelationPropertyDefinitions.entrySet().forEach(n->{defs.append(applyPropertyToRelation(n.getKey(),"r"));defs.append("\n");});
        defs.append("\n\n% define valid\n");
        defs.append(mvalid);
        defs.append("\n\n% modalSymbolDefinitions\n");
        modalSymbolDefinitions.entrySet().forEach(n->{defs.append(n.getValue());defs.append("\n");});
        defs.append("\n\n\n");
        return defs.toString();
    }
}
