package parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;


public class DescriptiveCallbackErrorListener extends BaseErrorListener {

    private ParseContext parseContext;

    public DescriptiveCallbackErrorListener(ParseContext parseContext){
        this.parseContext = parseContext;
    }


    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        String sourceName = recognizer.getInputStream().getSourceName();
        if (!sourceName.isEmpty()) {
            sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
        }

        String error = sourceName + "symbol:" + offendingSymbol.toString() + " at line " + line + ":" + charPositionInLine + " " + msg;
        parseContext.setParseError(error);
    }
}
