package util;

import exceptions.ImplementationError;
import exceptions.ParseException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import parser.ParseContext;
import parser.ThfAstGen;

import java.util.Optional;

public class THFRoleManipulation {

    public static Node stripThfSentenceFromProblemByRoleReturnNode(String problem, String role) throws ParseException {
        CodePointCharStream inputStream = CharStreams.fromString(problem);
        ParseContext parseContext = ThfAstGen.parse(inputStream, "tPTP_file", "");
        Node root = parseContext.getRoot();
        root.dfsRuleAllToplevel("thf_annotated").forEach(n->{
            Optional<Node> rn = n.dfsRule("formula_role");
            if (rn.isPresent()){
                Node roleNode = rn.get();
                if (roleNode.toStringLeafs().equals(role)) n.getParent().delChild(n);
            } else {
                throw new ImplementationError("No role node found in thf_annotated.");
            }
        });
        return root;
    }

    public static String stripThfSentenceFromProblemByRoleReturnString(String problem, String role) throws ParseException{
        return stripThfSentenceFromProblemByRoleReturnNode(problem, role).toStringLeafs();
    }
}
