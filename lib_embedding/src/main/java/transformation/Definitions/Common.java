package transformation.Definitions;

public class Common {

    /***************************************************************************
     * World type definition
     ***************************************************************************/
    public static final String w = "w_type";
    public static final String world_type_declaration = "" +
            "thf( w_type_type , type , ( " + w + ":$tType ) ).";
    public static final String truth_type = "(" + w + ">$o)";

    /***************************************************************************
     * Grounding definitions
     ***************************************************************************/
    public static final String mvalid = "" +
            "thf( mvalid_type , type , ( mvalid: (" + w + ">$o)>$o ) ).\n" +
            "thf( mvalid , definition , ( mvalid = (" +
            "^ [S:" + w + ">$o] : ! [W:" + w + "] : (S@W)" +
            "))).";

    public static final String mactual = "" +
            "thf( mactual_type , type , ( mactual: ( ( " + w + ">$o ) >$o ) ) ).\n" +
            "thf( mactual , definition , ( mactual = ( " +
            "^ [Phi:(" + w + ">$o)] : ( Phi @ mcurrentworld ) ) ) ).";


    // TODO normalize parentheses in types
    public static String normalizeType(String type){
        return type.replaceAll("[ ]","");
    }

    // THIS IS UGLY - replace it by numbering quantifications later
    public static String escapeType(String type){
        return type
                .replaceAll("[$]","_d_")
                .replaceAll("[>]","_t_")
                .replaceAll("[ ]","")
                .replaceAll("[(]","_o_")
                .replaceAll("[)]","_c_");
    }

}
