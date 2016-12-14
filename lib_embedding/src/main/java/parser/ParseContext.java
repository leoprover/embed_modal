package parser;

import org.antlr.v4.runtime.ParserRuleContext;
import util.tree.Node;


public class ParseContext {
    private String name;
    private String parseError;
    private Node root;
    private ParserRuleContext parserRuleContext;

    public ParseContext() {
        name = null;
        parseError = null;
        root = null;
        parserRuleContext = null;
    }

    void setName(String name) {
        this.name = name;
    }

    void setParseError(String parseError) {
        this.parseError = parseError;
    }

    void setRoot(Node root) {
        this.root = root;
    }

    void setParserRuleContext(ParserRuleContext parserRuleContext) {
        this.parserRuleContext = parserRuleContext;
    }

    public String getName() {
        return name;
    }

    public String getParseError() {
        return parseError;
    }

    public Node getRoot() {
        return this.root;
    }

    public ParserRuleContext getParserRuleContext() {
        return parserRuleContext;
    }
}
