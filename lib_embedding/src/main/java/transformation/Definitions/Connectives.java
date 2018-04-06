package transformation.Definitions;

import exceptions.AnalysisException;
import exceptions.ImplementationError;
import transformation.SemanticsAnalyzer;
import util.tree.Node;

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
            "thf( mnand , definition , ( mnand = (" +
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

    public static String getModalOperatorDefinition(String normalizedModalOperator, String normalizedAccessibilityRelation){
        if (normalizedModalOperator.contains("dia")){
            return "" +
                    "thf( " + normalizedModalOperator + "_type , type , ( " + normalizedModalOperator + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                    "thf( " + normalizedModalOperator + "_def , definition , ( " + normalizedModalOperator + " = (" +
                    "^ [A:" + w + ">$o,W:" + w + "] : ? [V:" + w + "] : ( (" + normalizedAccessibilityRelation + "@W@V) & (A@V) )" +
                    "))).";
        }
        return "" +
                "thf( " + normalizedModalOperator + "_type , type , ( " + normalizedModalOperator + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                "thf( " + normalizedModalOperator + "_def , definition , ( " + normalizedModalOperator + " = (" +
                "^ [A:" + w + ">$o,W:" + w + "] : ! [V:" + w + "] : ( (" + normalizedAccessibilityRelation + "@W@V) => (A@V) )" +
                "))).";
    }

    public static String getModalOperatorDefinitionS5U(String normalizedModalOperator){
        if (normalizedModalOperator.contains("dia")){
            return "" +
                    "thf( " + normalizedModalOperator + "_type , type , ( " + normalizedModalOperator + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                    "thf( " + normalizedModalOperator + "_def , definition , ( " + normalizedModalOperator + " = (" +
                    "^ [A:" + w + ">$o,W:" + w + "] : ? [V:" + w + "] : ( A @ V )" +
                    "))).";
        }
        return "" +
                "thf( " + normalizedModalOperator + "_type , type , ( " + normalizedModalOperator + ": (" + w + ">$o)>" + w + ">$o) ).\n" +
                "thf( " + normalizedModalOperator + "_def , definition , ( " + normalizedModalOperator + " = (" +
                "^ [A:" + w + ">$o,W:" + w + "] : ! [V:" + w + "] : ( A @ V )" +
                "))).";
    }

    /*
     * modalOperator has to be the tree which contains all parts of the modal operator
     * e.g. for $box @ 1 it contains both $box and 1
     */
    public static String getNormalizedModalOperator(Node n) throws AnalysisException {
        // TODO assert this is a valid operator
        Node firstLeaf = n.getFirstLeaf();
        String normalizedRelationSuffix = AccessibilityRelation.getNormalizedRelationSuffix(n);

        // $box
        if (firstLeaf.getLabel().equals("$box")){
            return box_unimodal;
        }
        // $dia
        if (firstLeaf.getLabel().equals("$dia")){
            return dia_unimodal;
        }
        // $box_int @ <int>
        if (firstLeaf.getLabel().equals("$box_int")){
            return box_int_prefix + "_" + normalizedRelationSuffix;
        }
        // $dia_int @ <int>
        if (firstLeaf.getLabel().equals("$dia_int")){
            return dia_int_prefix + "_" + normalizedRelationSuffix;
        }

        throw new AnalysisException("Connectives: Invalid modal operator: " + n.toString());
    }

    public static String getOppositeNormalizedModalOperator(String normalizedModalOperator) {
        if (normalizedModalOperator.startsWith(box_int_prefix)){
            return dia_int_prefix + normalizedModalOperator.substring(box_int_prefix.length());
        } else if (normalizedModalOperator.startsWith(dia_int_prefix)){
            return box_int_prefix + normalizedModalOperator.substring(dia_int_prefix.length());
        } else if (normalizedModalOperator.startsWith(box_unimodal)){
            return dia_unimodal + normalizedModalOperator.substring(box_unimodal.length());
        } else if (normalizedModalOperator.startsWith(dia_unimodal)){
            return box_unimodal + normalizedModalOperator.substring(dia_unimodal.length());
        } else {
            throw new ImplementationError("Could not convert normalized modal operator \"" + normalizedModalOperator + "\" to its opposite.");
        }
    }

    // for the syntactical embedding of accessibility relation properties
    // normalizedModalOperator may be dia or box
    public static String applyPropertyToModality(SemanticsAnalyzer.AccessibilityRelationProperty p, String normalizedModalOperator) {
        String box = normalizedModalOperator;
        String dia = normalizedModalOperator;
        if (normalizedModalOperator.startsWith(dia_int_prefix) || normalizedModalOperator.startsWith(dia_unimodal)) {
            box = getOppositeNormalizedModalOperator(normalizedModalOperator);
        } else {
            dia = getOppositeNormalizedModalOperator(normalizedModalOperator);
        }

        String axiom = null;
        switch (p){
            case K:
                // nothing to do in K, this case should not happen
                throw new ImplementationError("Unsupported modality Operator \"" + p.name() + "\"");
            case T:
                // (box @ A) => A
                axiom = "mimplies @ (" + box + " @ A) @ A";
                break;
            case B:
                // A => (box @ (dia @ A))
                axiom = "mimplies @ A @ (" + box + " @ (" + dia + " @ A))";
                break;
            case D:
                // (box @ A) => (dia @ A)
                axiom = "mimplies @ (" + box + " @ A) @ (" + dia + " @ A)";
                break;
            case FOUR:
                // (box @ A) => (box @ (box @ A))
                axiom = "mimplies @ (" + box + " @ A) @ (" + box + " @ (" + box + " @ A))";
                break;
            case FIVE:
                // (dia @ A) => (box @ (dia @ A))
                axiom = "mimplies @ (" + dia + " @ A) @ (" + box + " @ (" + dia + " @ A))";
                break;
            case CD:
                // (dia @ A) => (box @ A)
                axiom = "mimplies @ (" + dia + " @ A) @ (" + box + " @ A)";
                break;
            case BOXM:
                // box @ ((box @ A) => A)
                axiom = box + " @ (mimplies @ (" + box + " @ A) @ A)";
                break;
            case C4:
                // (box @ (box @ A)) => (box @ A)
                axiom = "mimplies @ (" + box + " @ (" + box + " @ A)) @ (" + box + " @ A)";
                break;
            case C:
                // (dia @ (box @ A)) => (box @ (dia @ A))
                axiom = "mimplies @ (" + dia + " @ (" + box + " @ A)) @ (" + box + " @ (" + dia + " @ A))";
                break;
            case S5U:
                // nothing to do in S5U, this case should not happen
                throw new ImplementationError("Unsupported modality Operator \"" + p.name() + "\"");
            default:
                // should not happen
                throw new ImplementationError("Unsupported modality Operator \"" + p.name() + "\"");
        }
        String nameOfFrameCondition = AccessibilityRelation.accessibilityRelationPropertyNames.get(p);
        String grounding = null;
        // ! [A:world>$o]: (mvalid @ (mimplies @ (mbox @ A) @ A))
        return "thf( " + nameOfFrameCondition + "_syntactic , axiom , (" +
                    "![A:" + Common.embedded_truth_type + "]: (" +
                        "![W:" + Common.w + "]: ((" + axiom + ") @ W)" +
                    ")" +
                ")).";
    }
}
