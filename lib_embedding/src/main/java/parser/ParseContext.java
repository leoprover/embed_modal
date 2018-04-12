package parser;

import org.antlr.v4.runtime.ParserRuleContext;
import util.Node;


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

    public void setName(String name) {
        this.name = name;
    }

    public void setParseError(String parseError) {
        this.parseError = parseError;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void setParserRuleContext(ParserRuleContext parserRuleContext) {
        this.parserRuleContext = parserRuleContext;
    }

    public boolean hasParseError(){
        return parseError != null;
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
