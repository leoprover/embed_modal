import org.junit.Test;
import util.Node;

import static org.junit.Assert.*;


public class TestNode {

    private static Node getTree(){
        Node root = new Node("0");
        Node child1 = new Node("1");
        Node child2 = new Node("2");
        Node child3 = new Node("3");

        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);

        Node child1a = new Node("1a");
        Node child1b = new Node("1b");
        Node child1c = new Node("1c");
        child1.addChild(child1a);
        child1.addChild(child1b);
        child1.addChild(child1c);

        Node child2a = new Node("2a");
        Node child2b = new Node("2b");
        Node child2c = new Node("2c");
        child2.addChild(child2a);
        child2.addChild(child2b);
        child2.addChild(child2c);

        Node child3a = new Node("3a");
        Node child3b = new Node("3b");
        Node child3c = new Node("3c");
        child3.addChild(child3a);
        child3.addChild(child3b);
        child3.addChild(child3c);

        return root;
    }

    public static final Node originalTree;
    static{
        originalTree = getTree();
    }
    /*
    @Test
    public void copyTest(){

    }*/

    /*
    @Test
    public void deepCopyTest(){

    }*/

    @Test
    public void firstChildTest(){
        Node root = originalTree;
        assertEquals("1",root.getFirstChild().getRule());
        assertEquals("1a",root.getFirstChild().getFirstChild().getRule());
    }

    @Test
    public void lastChildTest(){
        Node root = originalTree;
        assertEquals("3",root.getLastChild().getRule());
        assertEquals("3c",root.getLastChild().getLastChild().getRule());
    }

    @Test
    public void getChildTest(){
        Node root = originalTree;
        assertEquals("1",root.getChild(0).getRule());
        assertEquals("2",root.getChild(1).getRule());
        assertEquals("3",root.getChild(2).getRule());
        assertEquals("1a",root.getChild(0).getChild(0).getRule());
        assertEquals("1b",root.getChild(0).getChild(1).getRule());
        assertEquals("1c",root.getChild(0).getChild(2).getRule());
        assertEquals("2a",root.getChild(1).getChild(0).getRule());
        assertEquals("2b",root.getChild(1).getChild(1).getRule());
        assertEquals("2c",root.getChild(1).getChild(2).getRule());
    }

    @Test
    public void hasParentTest() {
        Node root = originalTree;
        assertEquals(null,root.getParent());
        assertEquals("0",root.getFirstChild().getParent().getRule());
        assertEquals("0",root.getChild(1).getParent().getRule());
        assertEquals("1",root.getFirstChild().getFirstChild().getParent().getRule());
        assertEquals("1",root.getFirstChild().getLastChild().getParent().getRule());
        assertEquals("2",root.getChild(1).getChild(0).getParent().getRule());
        assertEquals("2",root.getChild(1).getChild(1).getParent().getRule());
        assertEquals("2",root.getChild(1).getChild(2).getParent().getRule());
    }

    @Test
    public void addChildTest(){
        Node root = new Node("0");
        Node child1 = new Node("1");
        Node child2 = new Node("2");
        Node child3 = new Node("3");

        assertEquals(0,root.getChildren().size());
        root.addChild(child1);
        assertEquals(1,root.getChildren().size());
        assertEquals("1",root.getChild(0).getRule());
        assertEquals(root,child1.getParent());
        root.addChild(child2);
        assertEquals(2,root.getChildren().size());
        assertEquals("2",root.getChild(1).getRule());
        assertEquals(root,child1.getParent());
        root.addChild(child3);
        assertEquals(3,root.getChildren().size());
        assertEquals("3",root.getChild(2).getRule());
        assertEquals(root,child1.getParent());
    }

    @Test
    public void addChildAtTest(){
        Node root = getTree();
        Node test1 = new Node("test1");
        Node test2 = new Node("test2");
        Node test3 = new Node("test3");

        root.addChildAt(test1,0);
        assertEquals(4,root.getChildren().size());
        assertEquals("test1",root.getChild(0).getRule());
        assertEquals("0",root.getChild(0).getParent().getRule());
        assertEquals("1",root.getChild(1).getRule());
        assertEquals("2",root.getChild(2).getRule());
        assertEquals("3",root.getChild(3).getRule());
        root.addChildAt(test2,2);
        assertEquals(5,root.getChildren().size());
        assertEquals("0",root.getChild(2).getParent().getRule());
        assertEquals("test1",root.getChild(0).getRule());
        assertEquals("1",root.getChild(1).getRule());
        assertEquals("test2",root.getChild(2).getRule());
        assertEquals("2",root.getChild(3).getRule());
        assertEquals("3",root.getChild(4).getRule());
        root.getChild(3).addChildAt(test3,1);
        assertEquals(4,root.getChild(3).getChildren().size());
        assertEquals("2",root.getChild(3).getChild(1).getParent().getRule());
        assertEquals("2a",root.getChild(3).getChild(0).getRule());
        assertEquals("test3",root.getChild(3).getChild(1).getRule());
        assertEquals("2b",root.getChild(3).getChild(2).getRule());
        assertEquals("2c",root.getChild(3).getChild(3).getRule());
    }

    /*
     * Not working
    @Test
    public void addChildAfterTest(){
        Node root = getTree();
        Node test1 = new Node("test1");
        Node test2 = new Node("test2");
        Node test3 = new Node("test3");

        root.addChildAfter(root.getChild(0),test1);
        root.getChildren().stream().forEach(System.out::println);
        assertEquals(4,root.getChildren().size());
        assertEquals("test1",root.getChild(0).getRule());
        assertEquals("0",root.getChild(0).getParent().getRule());
        assertEquals("1",root.getChild(1).getRule());
        assertEquals("2",root.getChild(2).getRule());
        assertEquals("3",root.getChild(3).getRule());
        root.addChildAfter(root.getChild(1),test2);
        assertEquals(5,root.getChildren().size());
        assertEquals("0",root.getChild(2).getParent().getRule());
        assertEquals("test1",root.getChild(0).getRule());
        assertEquals("1",root.getChild(1).getRule());
        assertEquals("test2",root.getChild(2).getRule());
        assertEquals("2",root.getChild(3).getRule());
        assertEquals("3",root.getChild(4).getRule());
    }
    */

    /*
     * untested
    @Test
    public void delChildTest() {

    }
    */

    @Test
    public void delChildAtTest(){
        Node root = getTree();
        root.getChild(0).delChildAt(1);
        assertEquals(2,root.getChild(0).getChildren().size());
        assertEquals("1a",root.getChild(0).getChild(0).getRule());
        assertEquals("1c",root.getChild(0).getChild(1).getRule());
        root.getChild(0).delChildAt(0);
        assertEquals(1,root.getChild(0).getChildren().size());
        assertEquals("1c",root.getChild(0).getChild(0).getRule());
        root.delChildAt(2);
        assertEquals(2,root.getChildren().size());
        assertEquals("1",root.getChild(0).getRule());
        assertEquals("2",root.getChild(1).getRule());
    }

    /* probably not working
    @Test
    public void replaceChildTest(){

    }
    */

    @Test
    public void replaceChildAt(){
        Node root = getTree();
        Node test1 = new Node("test1");
        Node test2 = new Node("test2");
        Node test3 = new Node("test3");
        Node test4 = new Node("test4");

        root.replaceChildAt(1,test1);
        assertEquals(3,root.getChildren().size());
        assertEquals("test1",root.getChild(1).getRule());
        assertEquals("0",root.getChild(1).getParent().getRule());
        assertEquals("1",root.getChild(0).getRule());
        assertEquals("3",root.getChild(2).getRule());
        root.replaceChildAt(4,test2);
        assertEquals(3,root.getChildren().size());
        assertEquals(null,test2.getParent());
        root.getChild(2).replaceChildAt(0,test3);
        assertEquals(3,root.getChild(2).getChildren().size());
        assertEquals("test3",root.getChild(2).getChild(0).getRule());
        assertEquals("3",root.getChild(2).getChild(0).getParent().getRule());
        root.getChild(2).replaceChildAt(2,test4);
        assertEquals(3,root.getChild(2).getChildren().size());
        assertEquals("test4",root.getChild(2).getChild(2).getRule());
        assertEquals("3",root.getChild(2).getChild(2).getParent().getRule());


    }

}
