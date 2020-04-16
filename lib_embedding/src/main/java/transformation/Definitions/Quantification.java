package transformation.Definitions;

import exceptions.AnalysisException;
import exceptions.ParseException;
import exceptions.TransformationException;
import transformation.ModalTransformator;
import transformation.SemanticsGenerator;
import transformation.TransformContext;
import transformation.Wrappers;
import util.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;

//import static transformation.Definitions.Common.escapeType;

public class Quantification {

    private static final String w = Common.w;

    /***************************************************************************
     * Constant Quantification TH0
     ***************************************************************************/

    public static String embedded_forall_const_identifier(Type type){
        return "mforall_const_" + type.getLiftedEscapedType();
    }

    public static String embedded_exists_const_identifier(Type type){
        return "mexists_const_" + type.getLiftedEscapedType();
    }

    public static String mforall_const_th0(Type type){
        StringBuilder sb = new StringBuilder();
        // concrete type sentence from
        // thf( mforall_const_type , type , ( mforall_const : !> [T:$tType] : (T > " + w + " > $o) > " + w + " > $o ) ).
        sb.append("thf( mforall_const_type_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , type , ( mforall_const_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" : ( ( ( ");
        sb.append(type.getliftedNormalizedType());
        sb.append(" ) > ( " + w + " > $o ) ) > " + w + " > $o ) ) ).");
        sb.append("\n");
        // concrete definition sentence from
        // thf( mforall_const , definition , ( mforall_const = (
        // ^ [A:T>" + w + ">$o,W:" + w + "] : ! [X:T] : (A @ X @ W)
        //        ))).
        sb.append("thf( mforall_const_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , definition , ( mforall_const_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" = ( ^ [A:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")>" + w + ">$o,W:" + w + "] : ! [X:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")] : (A @ X @ W)");
        sb.append("))).");
        return sb.toString();
    }

    public static String mexists_const_th0(Type type){
        return mforall_const_th0(type).replaceAll("forall","exists").replaceAll("!","?");
    }

    /***************************************************************************
     * Constant Quantification TH1
     ***************************************************************************/

    public static String embedded_forall_const_identifier_th1 = "mforall_const";
    public static String embedded_exists_const_identifier_th1 = "mexists_const";

    public static final String mforall_const_th1 = String.format(
        "thf(mforall_const_type, type, mforall_const: !>[T:$tType]: ((T > %1$s > $o) > %1$s > $o)).\n" +
        "thf(mforall_const, definition, mforall_const = ^[T:$tType, A:T>%1$s>$o, W:%1$s]: (\n" +
        "    ![X:T]: (A @ X @ W)\n" +
        "  )).", w);

    public static final String mexists_const_th1 = mforall_const_th1.replaceAll("forall","exists").replaceAll("!\\[","?\\[");

    /***************************************************************************
     * Varying Quantification TH0
     ***************************************************************************/

    public static String embedded_forall_varying_identifier(Type type){
        return "mforall_vary_" + type.getLiftedEscapedType();
    }

    public static String embedded_exists_varying_identifier(Type type){
        return "mexists_vary_" + type.getLiftedEscapedType();
    }

    public static final String mcurrentworld = "" +
            "thf( mcurrentworld_type , type , ( mcurrentworld: " + w + " ) ).";

    /** Exists in world predicate for a certain type */
    public static String eiw_and_nonempty_th0(Type type){
        StringBuilder sb = new StringBuilder();
        // concrete type sentence from
        // thf( exists_in_world_type , type , ( eiw : !> [T:$tType] : (T > " + w + " > $o) ) ).
        sb.append("thf( exists_in_world_type_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , type , ( eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" : ( ( ( ");
        sb.append(type.getliftedNormalizedType());
        sb.append(" ) > ( " + w + " > $o ) ) ) ) ).");
        sb.append("\n");
        // non-emptyness axiom
        // thf( eiw_nonempty , axiom , ( eiw_nonempty = (
        // ! [W:" + w + "]: ? [X:T] : eiw @ X @ W
        //        ))).
        sb.append("thf( eiw_nonempty_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , axiom , (");
        sb.append("! [W:" + w + "]: ( ? [X:("+type.getliftedNormalizedType()+")] : (");
        sb.append("eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" @ X @ W");
        sb.append(")))).");
        return sb.toString();
    }

    /** Axiom for the existence of a constant `constant` of type `type`. */
    public static String constant_eiw_th0(String constant, Type type){
        StringBuilder sb = new StringBuilder();
        // non-emptyness axiom
        // thf( eiw_nonempty , axiom , ( eiw_nonempty = (
        // ! [W:" + w + "]: ? [X:T] : eiw @ X @ W
        //        ))).
        sb.append("thf( eiw_");
        sb.append(constant);
        sb.append(" , axiom , (");
        sb.append("! [W:" + w + "]: ( (");
        sb.append("eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" @ ");
        sb.append(constant);
        sb.append(" @ W");
        sb.append(")))).");
        return sb.toString();
    }

    /** Semantic Axiom for cumulative domains for type `type`. */
    public static String cumulative_semantic_axiom_th0(Type type) {
        // TODO alter for multimodal systems
        StringBuilder sb = new StringBuilder();
        // cumulative axiom for each relation r
        // thf( eiw_cumul , axiom , (
        // ! [W:" + w + ", V:" + w + ", C: T]: (((eiw @ C @ V) & (rel_r @ V @ W)) => (eiw @ C @ W))
        // )).
        sb.append("thf( cumul_domain_");
        sb.append(type.getLiftedEscapedType());
        sb.append("_");
        sb.append("r");
        //sb.append(normalizedRelation);
        sb.append(" , axiom , (");
        sb.append("! [W:" + w + ",V:" + w + ",C:" + type.getliftedNormalizedType() + "]: ((");

        // (eiw @ C @ V)
        sb.append("(eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append("@C@V)");
        // &
        sb.append(" & ");
        // (rel_r @ V @ W)
        sb.append("(");
        sb.append(AccessibilityRelation.accessibility_relation_prefix); // default, will be changed later
        sb.append("@V@W)");
        // ) => (
        sb.append(") => (");
        // (eiw @ C @ W)
        sb.append("eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append("@C@W");

        sb.append(")))).");

        return sb.toString();
    }

    /** Syntactic Axiom for cumulative domains for type `type`. */
    public static String cumulative_syntactic_axiom_th0(Type type) {
        // TODO alter for multimodal systems
        String identifier = "cumul_syntactic_" + type.getLiftedEscapedType();
        // example for type $i
        //thf(4,axiom, (![Phi:($i>$o)]: (
        //    ( $box @ ( ![X:$i] : (Phi @ X) ) )
        //    =>
        //    ( ![X:$i] : ( $box @ (Phi @ X) ) )
        //))).
        String converseBarcan = "![P:" + type.getNormalizedType() + ">$o]: (($box @ (![X:" + type.getNormalizedType() + "]: (P @ X))) => (![X:" + type.getNormalizedType() + "]: ($box @ (P @ X)))))";
        String converseBarcanTHF = "thf(" + identifier + ", axiom, (" + converseBarcan + ")).";
        System.out.println("cbf");
        System.out.println(converseBarcanTHF);
        String semantics = "thf(simple_s5,logic,(\n" +
                "    $modal :=\n" +
                "      [ $constants := $rigid,\n" +
                "        $quantification := [$constant," + type.getNormalizedType() + " := $varying],\n" +
                "        $consequence := $global,\n" +
                "        $modalities := $modal_system_K\n" +
                "      ] )).";
        try {
            TransformContext tc = Wrappers.convertModalStringToContext(converseBarcanTHF,identifier, null,null,null, semantics);
            Node thf_formula = tc.transformedRoot.dfsRule("thf_formula").get();
            thf_formula.delFirstChild(); // remove (mvalid@(
            thf_formula.getLastChild().setLabel(")"); // replace two closing brackets with one closing bracket
            Node thf_quantified_formula = thf_formula.dfsRule("thf_quantified_formula").get();
            Node thf_typed_formula = thf_quantified_formula.getFirstChild().getFirstChild().getFirstChild().getFirstChild();
            String phi_quantification = thf_typed_formula.getLastChild().getLabel();
            thf_typed_formula.getLastChild().setLabel(phi_quantification.replace("^","!") + "mvalid@("); // replace lambda by quantifier and surround body with mvalid
            thf_typed_formula.delFirstChild(); // remove quantifier
            thf_typed_formula.delFirstChild(); // remove @
            //String dotIn = tc.transformedRoot.toDot();
            //Files.write(Paths.get("/home/tg/cumul.dot"), dotIn.getBytes());
            //String cmd = "dot" + " -Tps " + "/home/tg/cumul.dot" + " -o " + "/home/tg/cumul.dot" + ".ps";
            //Runtime.getRuntime().exec(cmd);
            return tc.transformedRoot.toStringWithLinebreaks();
        } catch (ParseException | IOException | AnalysisException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (TransformationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        assert(false);
        return null;
    }



    /** Semantic Axiom for decreasing domains for type `type`. */
    public static String decreasing_semantic_axiom_th0(Type type) {
        // TODO alter for multimodal systems
        StringBuilder sb = new StringBuilder();
        // decreasing axiom for each relation r
        // thf( eiw_decre , axiom , (
        // ! [W:" + w + ", V:" + w + ", C: T]: (((eiw @ C @ W) & (rel_r @ V @ W)) => (eiw @ C @ V))
        // )).
        sb.append("thf( decr_domain_");
        sb.append(type.getLiftedEscapedType());
        sb.append("_");
        sb.append("r"); // sb.append(relation);
        sb.append(" , axiom , (");
        sb.append("! [W:" + w + ",V:" + w + ",C:" + type.getliftedNormalizedType() + "]: ((");

        // (eiw @ C @ W)
        sb.append("(eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append("@C@W)");
        // &
        sb.append(" & ");
        // (rel_r @ V @ W)
        sb.append("(");
        sb.append(AccessibilityRelation.accessibility_relation_prefix); // default, will be changed later
        sb.append("@V@W)");
        // ) => (
        sb.append(") => (");
        // (eiw @ C @ V)
        sb.append("eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append("@C@V");

        sb.append(")))).");

        return sb.toString();
    }

    /** Syntactic Axiom for decreasing domains for type `type`. */
    public static String decreasing_syntactic_axiom_th0(Type type) {
        // TODO alter for multimodal systems
        String identifier = "decre_syntactic_" + type.getLiftedEscapedType();
        // example for type $i
        //thf(4,axiom, (![Phi:($i>$o)]: (
        //    ( ![X:$i] : ( $box @ (Phi @ X) ) )
        //    =>
        //    ( $box @ ( ![X:$i] : (Phi @ X) ) )
        //))).
        String barcan = "![P:" + type.getNormalizedType() + ">$o]: ((![X:" + type.getNormalizedType() + "]: ($box @ (P @ X))) => ($box @ (![X:" + type.getNormalizedType() + "]: (P @ X))))";
        String barcanTHF = "thf(" + identifier + ", axiom, (" + barcan + ")).";
        String semantics = "thf(simple_s5,logic,(\n" +
                "    $modal :=\n" +
                "      [ $constants := $rigid,\n" +
                "        $quantification := [$constant," + type.getNormalizedType() + " := $varying],\n" +
                "        $consequence := $global,\n" +
                "        $modalities := $modal_system_K\n" +
                "      ] )).";
        try {
            TransformContext tc = Wrappers.convertModalStringToContext(barcanTHF,identifier, null,null,null, semantics);
            Node thf_formula = tc.transformedRoot.dfsRule("thf_formula").get();
            thf_formula.delFirstChild(); // remove (mvalid@(
            thf_formula.getLastChild().setLabel(")"); // replace two closing brackets with one closing bracket
            Node thf_quantified_formula = thf_formula.dfsRule("thf_quantified_formula").get();
            Node thf_typed_formula = thf_quantified_formula.getFirstChild().getFirstChild().getFirstChild().getFirstChild();
            String phi_quantification = thf_typed_formula.getLastChild().getLabel();
            thf_typed_formula.getLastChild().setLabel(phi_quantification.replace("^","!") + "mvalid@("); // replace lambda by quantifier and surround body with mvalid
            thf_typed_formula.delFirstChild(); // remove quantifier
            thf_typed_formula.delFirstChild(); // remove @
            //String dotIn = tc.transformedRoot.toDot();
            //Files.write(Paths.get("/home/tg/cumul.dot"), dotIn.getBytes());
            //String cmd = "dot" + " -Tps " + "/home/tg/cumul.dot" + " -o " + "/home/tg/cumul.dot" + ".ps";
            //Runtime.getRuntime().exec(cmd);
            return tc.transformedRoot.toStringWithLinebreaks();
        } catch (ParseException | IOException | AnalysisException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            //throw new TransformationException(e);
        } catch (TransformationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        assert(false);
        return null;
    }

    /** Declaration of varying domain quantifier */
    public static String mforall_varying_th0(Type type){
        StringBuilder sb = new StringBuilder();
        sb.append("thf(mforall_vary_type_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , type , ( mforall_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" : ( ( ( ");
        sb.append(type.getliftedNormalizedType());
        sb.append(" ) > ( " + w + " > $o ) ) > " + w + " > $o ) ) ).");
        sb.append("\n");
        sb.append("thf(mforall_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , definition , ( mforall_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" = ( ^ [A:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")>" + w + ">$o,W:" + w + "] : ! [X:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")] : ((eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" @ X @ W) => (A @ X @ W))");
        sb.append("))).");
        return sb.toString();
    }

    /** Declaration of varying domain quantifier */
    public static String mexists_varying_th0(Type type){
        StringBuilder sb = new StringBuilder();
        sb.append("thf(mexists_vary_type_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , type , ( mexists_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" : ( ( ( ");
        sb.append(type.getliftedNormalizedType());
        sb.append(" ) > ( " + w + " > $o ) ) > " + w + " > $o ) ) ).");
        sb.append("\n");
        sb.append("thf(mexists_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" , definition , ( mexists_vary_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" = ( ^ [A:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")>" + w + ">$o,W:" + w + "] : ? [X:(");
        sb.append(type.getliftedNormalizedType());
        sb.append(")] : ((eiw_");
        sb.append(type.getLiftedEscapedType());
        sb.append(" @ X @ W) & (A @ X @ W))");
        sb.append("))).");
        return sb.toString();
    }


    /***************************************************************************
     * Varying Quantification TH1
     ***************************************************************************/

    public static String embedded_forall_vary_identifier_th1 = "mforall_vary";
    public static String embedded_exists_vary_identifier_th1 = "mexists_vary";

    public static final String eiw_th1 = String.format(
      "thf(eiw_type, type, eiw: !>[T:$tType]: (T > %1$s > $o)).", w);

    public static final String eiw_nonempty_th1 = String.format(
       "thf(eiw_nonempty, axiom, ![T:$tType,W:%1$s]: ?[X:T]: (eiw @ T @ X @ W)).", w);

    public static String constant_eiw_th1(String constant, Type type) {
        return String.format(
                "thf(eiw_%1$s, axiom, ![W:%2$s]: (eiw @ %3$s @ %1$s @ W)).", constant, w, type.getliftedNormalizedType());
    }

    /* eiw is cumulative for all types */
    public static String cumul_axiom_semantic_th1 = String.format(
        "thf(eiw_cumul, axiom, (\n" +
            "    ![T: $tType, W:%1$s, V:%1$s, C: T]: (((eiw @ T @ C @ W) & (%2$s @ W @ V)) => (eiw @ T @ C @ V))\n" +
            "  )).", w, AccessibilityRelation.accessibility_relation_prefix);

    /* eiw is decreasing for all types */
    public static String decreasing_axiom_semantic_th1 = String.format(
            "thf(eiw_decr, axiom, (\n" +
                    "    ![T: $tType, W:%1$s, V:%1$s, C: T]: (((eiw @ T @ C @ V) & (%2$s @ W @ V)) => (eiw @ T @ C @ W))\n" +
                    "  )).", w, AccessibilityRelation.accessibility_relation_prefix);

    /* eiw cumulative for a given type */
    public static String cumul_type_axiom_semantic_th1(Type type) {
        return String.format(
                "thf(eiw_cumul_%1$s, axiom, (\n" +
                "    ![W:%2$s, V:%2$s, C: %3$s]: (((eiw @ %3$s @ C @ W) & (%4$s @ W @ V)) => (eiw @ %3$s @ C @ V))\n" +
                "  )).", type.getLiftedEscapedType(), w, type.getliftedNormalizedType(), AccessibilityRelation.accessibility_relation_prefix);
    }

    /* eiw decreasing for a given type */
    public static String decreasing_type_axiom_semantic_th1(Type type) {
        return String.format(
                "thf(eiw_decr_%1$s, axiom, (\n" +
                        "    ![W:%2$s, V:%2$s, C: %3$s]: (((eiw @ %3$s @ C @ V) & (%4$s @ W @ V)) => (eiw @ %3$s @ C @ W))\n" +
                        "  )).", type.getLiftedEscapedType(), w, type.getliftedNormalizedType(), AccessibilityRelation.accessibility_relation_prefix);
    }

    public static void main(String[] args) {
        String res = decreasing_axiom_semantic_th1;
        System.out.println(res);
    }

    public static final String mforall_vary_th1 = String.format(
            "thf(mforall_vary_type, type, mforall_vary: !>[T:$tType]: ((T > %1$s > $o) > %1$s > $o)).\n" +
                    "thf(mforall_vary, definition, mforall_vary = ^[T:$tType, A:T>%1$s>$o, W:%1$s]: (\n" +
                    "    ![X:T]: ((eiw @ T @ X @ W) => (A @ X @ W))\n" +
                    "  )).", w);

    public static final String mexists_vary_th1 = String.format(
            "thf(mexists_vary_type, type, mexists_vary: !>[T:$tType]: ((T > %1$s > $o) > %1$s > $o)).\n" +
                    "thf(mexists_vary, definition, mexists_vary = ^[T:$tType, A:T>%1$s>$o, W:%1$s]: (\n" +
                    "    ![X:T]: ((eiw @ T @ X @ W) & (A @ X @ W))\n" +
                    "  )).", w);

}
