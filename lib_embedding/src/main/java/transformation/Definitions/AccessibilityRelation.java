package transformation.Definitions;

import transformation.SemanticsAnalyzer.AccessibilityRelationProperty;

import java.util.HashMap;
import java.util.Map;

public class AccessibilityRelation {
    private static final String w = Common.w;

    public static final Map<AccessibilityRelationProperty,String> accessibilityRelationPropertyDefinitions;
    public static final Map<AccessibilityRelationProperty,String> accessibilityRelationPropertyNames;

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

    static{
        accessibilityRelationPropertyDefinitions = new HashMap<>();
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.T, mreflexive);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.B, msymmetric);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.D, mserial);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.FOUR, mtransitive);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.FIVE, meuclidean);
    }
    static{
        accessibilityRelationPropertyNames = new HashMap<>();
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.T, "mreflexive");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.B, "msymmetric");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.D, "mserial");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.FOUR, "mtransitive");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.FIVE, "meuclidean");
    }

    /***************************************************************************
     * box operators // accessibility relations declarations
     ***************************************************************************/

    public static String accessibility_relation_prefix = "mrel";
    public static String default_accessibility_relation_name = "r";
    public static String accessiblity_relation_name_unimodal = accessibility_relation_prefix + "_" + default_accessibility_relation_name;
    public static String accessiblity_relation_box_int_prefix = accessibility_relation_prefix + "_box_int";

    public static String getAccessibilityRelationPropertyDefinitionByAccessibilityRelationProperty(AccessibilityRelationProperty p){
        return accessibilityRelationPropertyDefinitions.get(p);
    }

    public static String getRelationNameUnimodal(){
        return accessiblity_relation_name_unimodal;
    }

    public static String getRelationNameInt(String int_index){
        return accessiblity_relation_box_int_prefix + "_" + int_index;
    }

    /*
     * Multimodal operators with index int
     */
    public static String getRelationDeclarationInt(String int_index){
        return "";
    }

    public static String declareRelation(String relationName){
        // TODO alter for multimodal systems
        //String escapedRelation = escapeRelation(relation);
        return "thf( " + relationName + "_type , type , ( " + relationName + ":" + w + ">" + w + ">$o) ).";
    }

    public static String applyPropertyToRelation(AccessibilityRelationProperty p, String relationName){
        // TODO alter formultimodal systems
        return "thf( " + relationName + "_" + accessibilityRelationPropertyNames.get(p) + " , axiom , ( " +
                accessibilityRelationPropertyNames.get(p) + " @ " + relationName + " ) ).";
    }




    private static String escapeRelation(String relation){
        // TODO for multimodal systems
        return relation;
    }




}
