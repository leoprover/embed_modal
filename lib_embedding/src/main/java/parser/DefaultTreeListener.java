package parser;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import util.Node;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * default tree listener implementation
 */
public class DefaultTreeListener implements ParseTreeListener {

    protected Parser parser;
    private Map<String, Integer> rmap;

    private Stack<String> sctx = new Stack<String>();
    private Node nodeptr = null;
    private Node root = null;
    private Predicate<String> filter = null;


    /**
     * constructor
     */
    public DefaultTreeListener() {
        this(x -> !x.isEmpty());
    }

    /**
     * construtor
     * @param filter condition that has to hold for every node
     */
    public DefaultTreeListener(Predicate<String> filter) {
        this.sctx.add("S");
        Node root = new Node("root","root");
        this.root = root;
        this.nodeptr = root;
        this.filter = filter;
        this.parser = null;
        this.rmap = null;
    }

    public String getRuleByKey(int key) {

        for (Map.Entry<String, Integer> e : this.rmap.entrySet()) {
            if (e.getValue() == key)
                return e.getKey();
        }
        return null;
    }

    protected void setParser(Parser p) {
        this.parser = p;
        this.rmap = this.parser.getRuleIndexMap();
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        Node n = new Node(nodeptr, "terminal", terminalNode.getText());
        nodeptr.addChild(n);
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        String rule = this.getRuleByKey(ctx.getRuleIndex());
        if (this.filter.test(rule)) {
            Node n = new Node(nodeptr, rule/*, ctx.getText()*/);
            nodeptr.addChild(n);
            nodeptr = n;
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        String rule = this.getRuleByKey(ctx.getRuleIndex());
        if (this.filter.test(rule)) {
            this.nodeptr = this.nodeptr.getParent();
        }
    }

    public Node getRootNode() {
        return this.root;
    }

    /*
    public Set<Node> getNodes() {
        return this.getNodes();
    }*/


}
