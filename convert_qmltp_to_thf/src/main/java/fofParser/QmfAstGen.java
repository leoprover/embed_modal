package fofParser;

import exceptions.ParseException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.DescriptiveCallbackErrorListener;
import parser.ParseContext;
import parser.QmfLexer;
import parser.QmfParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class QmfAstGen {

    /**
     * parse ANTLRInputStream containing thf and return ast
     * @param inputStream ANTLRInputStream object
     * @param rule start parsing at this rule
     * @return ast
     * @throws ParseException if there is no such rule
     */
    public static ParseContext parse(ANTLRInputStream inputStream, String rule, String name) throws ParseException {

        QmfLexer lexer = new QmfLexer(inputStream);
        lexer.removeErrorListeners(); // only for production
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();


        QmfParser parser = new parser.QmfParser(tokens);
        parser.removeErrorListeners(); // only for production
        ParseContext parseContext = new ParseContext();
        parser.addErrorListener(new DescriptiveCallbackErrorListener(parseContext));

        FofTreeListener treeListener = new FofTreeListener();
        treeListener.setParser(parser);

        //parser.addErrorListener(new DiagnosticErrorListener());
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        parser.setBuildParseTree(true);
        parser.setTokenStream(tokens);

        // parsing starting from a rule requires invoking that rulename as parser method
        ParserRuleContext parserRuleContext = null;
        try {
            Class<?> parserClass = parser.getClass();
            Method method = parserClass.getMethod(rule, (Class<?>[]) null);
            parserRuleContext = (ParserRuleContext) method.invoke(parser, (Object[]) null);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e) {
            e.printStackTrace();
            throw new ParseException(e.getMessage());
        }        // the above or the below

        // if there is a fixed rule
        // ParserRuleContext context = parser.tPTP_file();

        // create ast
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(treeListener, parserRuleContext);

        // create and return ParseContext
        parseContext.setParserRuleContext(parserRuleContext);
        parseContext.setRoot(treeListener.getRootNode());
        parseContext.setName(name);
        return parseContext;
    }

    /**
     * parse String containing thf and return ast
     * @param inputString String object
     * @param rule start parsing at this rule
     * @return ast
     * @throws ParseException if there is no such rule
     */
    public static ParseContext parse(String inputString, String rule, String name) throws ParseException {
         return QmfAstGen.parse(new ANTLRInputStream(inputString), rule, name);
    }

}
