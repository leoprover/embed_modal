import org.junit.Test;
import transformation.ModalTransformator;
import util.Node;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class TestModalTransformatorSingleFunctions {

    public static Node getA_and_B(){
        Node A_and_B;
        A_and_B = new Node("thf_binary_tuple");
        A_and_B.addChild(new Node("thf_unitary_formula"));
        A_and_B.addChild(new Node("terminal","&"));
        A_and_B.addChild(new Node("thf_unitary_formula"));
        return A_and_B;
    }

    public static Node getA_and_B_and_C(){
        Node A_and_B_and_C;
        A_and_B_and_C = new Node("thf_binary_tuple");
        A_and_B_and_C.addChild(new Node("thf_binary_tuple"));
        A_and_B_and_C.addChild(new Node("terminal","&"));
        A_and_B_and_C.addChild(new Node("thf_unitary_formula"));
        A_and_B_and_C.getChild(0).addChild(new Node("thf_unitary_formula"));
        A_and_B_and_C.getChild(0).addChild(new Node("terminal","&"));
        A_and_B_and_C.getChild(0).addChild(new Node("thf_unitary_formula"));
        return A_and_B_and_C;
    }

    public static Node get_A_equivalent_B(){
        Node A_and_B;
        A_and_B = new Node("thf_binary_pair");
        A_and_B.addChild(new Node("thf_unitary_formula"));
        A_and_B.addChild(new Node("terminal","<=>"));
        A_and_B.addChild(new Node("thf_unitary_formula"));
        return A_and_B;
    }

    @Test
    public void binary_tupleSingleTest() throws Exception{
        ModalTransformator mt = new ModalTransformator(new Node());
        Method embed_thf_binary_tuple = mt.getClass().getDeclaredMethod("embed_thf_binary_tuple", Node.class);
        embed_thf_binary_tuple.setAccessible(true);

        Node root = getA_and_B();
        embed_thf_binary_tuple.invoke(mt,root);
        assertEquals(9,root.getChildren().size());
        assertEquals("mand", root.getChild(0).getLabel());
        assertEquals("@", root.getChild(1).getLabel());
        assertEquals("(", root.getChild(2).getLabel());
        assertEquals("thf_unitary_formula", root.getChild(3).getRule());
        assertEquals(")", root.getChild(4).getLabel());
        assertEquals("@", root.getChild(5).getLabel());
        assertEquals("(", root.getChild(6).getLabel());
        assertEquals("thf_unitary_formula", root.getChild(7).getRule());
        assertEquals(")", root.getChild(8).getLabel());
    }

    @Test
    public void binary_tupleMultipleTest() throws Exception{
        ModalTransformator mt = new ModalTransformator(new Node());
        Method embed_thf_binary_tuple = mt.getClass().getDeclaredMethod("embed_thf_binary_tuple", Node.class);
        embed_thf_binary_tuple.setAccessible(true);

        Node root = getA_and_B_and_C();
        embed_thf_binary_tuple.invoke(mt,root);
        //root.getChildren().stream().forEach(n -> System.out.println(n.getLabel()));
        assertEquals(9,root.getChildren().size());
        assertEquals("mand", root.getChild(0).getLabel());
        assertEquals("@", root.getChild(1).getLabel());
        assertEquals("(", root.getChild(2).getLabel());
        assertEquals("thf_binary_tuple", root.getChild(3).getRule());
        assertEquals(")", root.getChild(4).getLabel());
        assertEquals("@", root.getChild(5).getLabel());
        assertEquals("(", root.getChild(6).getLabel());
        assertEquals("thf_unitary_formula", root.getChild(7).getRule());
        assertEquals(")", root.getChild(8).getLabel());

    }

    /*
    @Test
    public void binary_pairTest() throws Exception{
        ModalTransformator mt = new ModalTransformator(new Node());
        Method embed_thf_binary_tuple = mt.getClass().getDeclaredMethod("embed_thf_binary_pair", Node.class);
        embed_thf_binary_tuple.setAccessible(true);

        Node root = get_A_equivalent_B();
        embed_thf_binary_tuple.invoke(mt,root);
        assertEquals(9,root.getChildren().size());
        assertEquals("mequiv", root.getChild(0).getLabel());
        assertEquals("@", root.getChild(1).getLabel());
        assertEquals("(", root.getChild(2).getLabel());
        assertEquals("thf_unitary_formula", root.getChild(3).getRule());
        assertEquals(")", root.getChild(4).getLabel());
        assertEquals("@", root.getChild(5).getLabel());
        assertEquals("(", root.getChild(6).getLabel());
        assertEquals("thf_unitary_formula", root.getChild(7).getRule());
        assertEquals(")", root.getChild(8).getLabel());
    }
    */
}
