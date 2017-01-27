package transformation.Definitions;

import exceptions.AnalysisException;
import transformation.SemanticsAnalyzer.AccessibilityRelationProperty;
import util.tree.Node;

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
            "thf( mreflexive_def , definition , ( mreflexive = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + "] : (R@A@A)" +
            "))).";

    public static final String msymmetric = "" +
            "thf( msymmetric_type , type , ( msymmetric : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( msymmetric_def , definition , ( msymmetric = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + "] : ( (R@A@B) => (R@B@A) )" +
            "))).";

    public static final String mtransitive = "" +
            "thf( mtransitive_type , type , ( mtransitive : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mtransitive_def , definition , ( mtransitive = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + ",C:" + w + "] : ( ( (R@A@B) & (R@B@C) ) => (R@A@C) )" +
            "))).";

    public static final String mserial = "" +
            "thf( mserial_type , type , ( mserial : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mserial_def , definition , ( mserial = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + "] : ? [B:" + w + "] : (R@A@B)" +
            "))).";

    public static final String meuclidean = "" +
            "thf( meuclidean_type , type , ( meuclidean : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( meuclidean_def , definition , ( meuclidean = (" +
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

    public static final String accessibility_relation_prefix = "mrel";

    public static String getAccessibilityRelationPropertyDefinitionByAccessibilityRelationProperty(AccessibilityRelationProperty p){
        return accessibilityRelationPropertyDefinitions.get(p);
    }

    public static String getNormalizedRelationName(String normalizedModalOperator){
        return accessibility_relation_prefix + "_" + normalizedModalOperator;
    }

    /*
     * modalOperator has to be the tree which contains all parts of the modal operator
     * e.g. for $box @ 1 it contains both $box and 1
     */
    public static String getNormalizedRelationName(Node modalOperator) throws AnalysisException {
        return accessibility_relation_prefix + "_" + Connectives.getNormalizedModalOperator(modalOperator);
    }

    public static String getRelationDeclaration(String normalizedRelationName){
        return "thf( " + normalizedRelationName + "_type , type , ( " + normalizedRelationName + ":" + w + ">" + w + ">$o) ).";
    }

    public static String applyPropertyToRelation(AccessibilityRelationProperty p, String normalizedRelationName){
        return "thf( " + normalizedRelationName + "_" + accessibilityRelationPropertyNames.get(p) + " , axiom , ( " +
                accessibilityRelationPropertyNames.get(p) + " @ " + normalizedRelationName + " ) ).";
    }


}
