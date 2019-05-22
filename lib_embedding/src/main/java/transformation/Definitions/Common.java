package transformation.Definitions;

import exceptions.ImplementationError;
import exceptions.ParseException;
import parser.ParseContext;
import parser.ThfAstGen;
import util.Node;

import java.util.Stack;


public class Common {

    /***************************************************************************
     * World type definition
     ***************************************************************************/
    public static final String w = "mworld";
    public static final String world_type_declaration = "" +
            "thf( " + w + "_type , type , ( " + w + ":$tType ) ).";
    public static final String embedded_truth_type = "(" + w + ">$o)";

    /***************************************************************************
     * Grounding definitions
     ***************************************************************************/
    public static final String mvalid = "" +
            "thf( mvalid_type , type , ( mvalid: (" + w + ">$o)>$o ) ).\n" +
            "thf( mvalid_def , definition , ( mvalid = (" +
            "^ [S:" + w + ">$o] : ! [W:" + w + "] : (S@W)" +
            "))).";

    public static final String mactual = "" +
            "thf( mactual_type , type , ( mactual: ( ( " + w + ">$o ) >$o ) ) ).\n" +
            "thf( mactual_def , definition , ( mactual = ( " +
            "^ [Phi:(" + w + ">$o)] : ( Phi @ mcurrentworld ) ) ) ).";


}
