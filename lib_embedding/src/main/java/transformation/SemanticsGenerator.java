package transformation;

public class SemanticsGenerator {

    public static final String standard_s5 = "thf(simple_s5,logic, ( $modal :=\n" +
            "    [$constants := [$rigid],\n" +
            "     $quantification := $constant,\n" +
            "     $consequence := $global,\n" +
            "     $modalities := $modal_system_S5\n" +
            "    ] )).";

    public static String[] systems;
    public static String[] domains;
    public static String[] constants;
    public static String[] consequences;
    // [modal_system][domains][constants][consequence]
    public static String[][][][] semanticsCube;
    // [modal_system][0][0][0]
    public static String[] rigid_constant_global;
    static{
        systems = new String[5];
        systems[0] = "$modal_system_K";
        systems[1] = "$modal_system_D";
        systems[2] = "$modal_system_T";
        systems[3] = "$modal_system_S4";
        systems[4] = "$modal_system_S5";
        domains = new String[4];
        domains[0] = "$constant";
        domains[1] = "varying";
        domains[2] = "$cumulative";
        domains[3] = "$decreasing";
        constants = new String[2];
        constants[0] = "$rigid";
        constants[1] = "$flexible";
        consequences = new String[2];
        consequences[0] = "$global";
        consequences[1] = "$local";
        semanticsCube = new String[systems.length][domains.length][constants.length][consequences.length];
        for (int system = 0; system < systems.length; system++){
            for(int domain = 0; domain < domains.length; domain++){
                for (int constant = 0; constant < constants.length; constant++){
                    for (int consequence = 0; consequence < consequences.length; consequence++){
                        semanticsCube[system][domain][constant][consequence] = "thf(" +
                                cubeEntryIndexToString(system,domain,constant,consequence) +
                                ",logic, ( $modal :=\n" +
                                "    [$constants := " + constants[constant] + ",\n" +
                                "     $quantification := " + domains[domain] + ",\n" +
                                "     $consequence := " + consequences[consequence] + ",\n" +
                                "     $modalities := "+ systems[system] + "\n" +
                                "    ] )).";
                    }
                }
            }
        }
        rigid_constant_global = new String[systems.length];
        for (int system = 0; system < systems.length; system++){
            rigid_constant_global[system] = semanticsCube[system][0][0][0];
        }
    }

    public static String cubeEntryIndexToString(int system, int domain, int constant, int consequence){
        StringBuilder sb = new StringBuilder();
        String ret = systems[system] + domains[domain] + constants[constant] + consequences[consequence];
        ret = ret.replaceAll("[\\$]","_");
        ret = ret.substring(14).toLowerCase();
        return ret;
    }

    public static String thfName(String sentence){
        int start = sentence.indexOf("(");
        int end = sentence.indexOf(",",start+1);
        return sentence.substring(start+1,end).trim();
    }

}
