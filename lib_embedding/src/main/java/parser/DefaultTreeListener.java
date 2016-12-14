/*
* Inmemantlr - In memory compiler for Antlr 4
*
* Copyright 2016, Julian Thomé <julian.thome@uni.lu>
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
* the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence. You may
* obtain a copy of the Licence at:
*
* https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/

package parser;

import util.tree.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;
import java.util.function.Predicate;

/**
 * default tree listener implementation
 */
public class DefaultTreeListener extends DefaultListener {

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
