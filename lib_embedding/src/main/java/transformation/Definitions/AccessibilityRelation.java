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

    public static final String mfunctional = "" +
            "thf( mfunctional_type , type , ( mfunctional : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mfunctional_def , definition , ( mfunctional = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + ",C:" + w + "] : ( ( (R@A@B) & (R@A@C) ) => ( B = C ) )" +
            "))).";
    public static final String mshiftreflexive = "" +
            "thf( mshiftreflexive_type , type , ( mshiftreflexive : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mshiftreflexive_def , definition , ( mshiftreflexive = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + "] : ( (R@A@B) => (R@B@B) )" +
            "))).";
    public static final String mdense = "" +
            "thf( mdense_type , type , ( mdense : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mdense_def , definition , ( mdense = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + "] : ( (R@A@B) => ( ? [C:" + w + "] : ( (R@A@C) & (R@C@B) ) ) )" +
            "))).";
    public static final String mconvergent = "" +
            "thf( mconvergent_type , type , ( mconvergent : (" + w + ">" + w + ">$o)>$o ) ).\n" +
            "thf( mconvergent_def , definition , ( mconvergent = (" +
            "^ [R:" + w + ">" + w + ">$o] : ! [A:" + w + ",B:" + w + ",C:" + w + "] : ( ( (R@A@B) & (R@A@C) ) => ( ? [D:" + w + "] : ( (R@B@D) & (R@C@D) ) ) )" +
            "))).";

    static{
        accessibilityRelationPropertyDefinitions = new HashMap<>();
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.T, mreflexive);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.B, msymmetric);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.D, mserial);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.FOUR, mtransitive);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.FIVE, meuclidean);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.CD, mfunctional);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.BOXM, mshiftreflexive);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.C4, mdense);
        accessibilityRelationPropertyDefinitions.put(AccessibilityRelationProperty.C, mconvergent);
    }
    static{
        accessibilityRelationPropertyNames = new HashMap<>();
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.T, "mreflexive");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.B, "msymmetric");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.D, "mserial");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.FOUR, "mtransitive");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.FIVE, "meuclidean");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.CD, "mfunctional");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.BOXM, "mshiftreflexive");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.C4, "mdense");
        accessibilityRelationPropertyNames.put(AccessibilityRelationProperty.C, "mconvergent");
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