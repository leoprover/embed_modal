package transformation;

import java.util.Arrays;

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

    // interesting system sets
    public static String[] constant_rigid_global;
    public static String[] rigid_global;
    public static String[] rigid_local;
    public static String[] rigid;
    public static String[] all_supported;
    public static String[] all;


    static{
        systems = new String[9];
        systems[0] = "$modal_system_K";
        systems[1] = "$modal_system_D";
        systems[2] = "$modal_system_T";
        systems[3] = "$modal_system_S4";
        systems[4] = "$modal_system_S5";
        systems[5] = "$modal_system_CD";
        systems[6] = "$modal_system_BOXM";
        systems[7] = "$modal_system_C4";
        systems[8] = "$modal_system_C";
        domains = new String[4];
        domains[0] = "$constant";
        domains[1] = "$varying";
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
        constant_rigid_global = new String[systems.length];
        for (int system = 0; system < systems.length; system++){
            constant_rigid_global[system] = semanticsCube[system][0][0][0];
        }
        rigid_global = new String[systems.length * domains.length];
        for (int system = 0; system < systems.length; system++){
            for (int domain = 0; domain < domains.length; domain++)
                rigid_global[system * domains.length + domain] = semanticsCube[system][domain][0][0];
        }
        rigid_local = new String[systems.length * domains.length];
        for (int system = 0; system < systems.length; system++){
            for (int domain = 0; domain < domains.length; domain++)
                rigid_local[system * domains.length + domain] = semanticsCube[system][domain][0][1];//
        }
        rigid = new String[systems.length * domains.length * consequences.length];
        for (int system = 0; system < systems.length; system++){
            for (int domain = 0; domain < domains.length; domain++)
                for (int consequence = 0; consequence < consequences.length; consequence++)
                    rigid[system * domains.length * consequences.length + domain * consequences.length + consequence] = semanticsCube[system][domain][0][consequence];//
        }
        all = new String[systems.length * domains.length * consequences.length * constants.length];
        for (int system = 0; system < systems.length; system++){
            for (int domain = 0; domain < domains.length; domain++)
                for (int consequence = 0; consequence < consequences.length; consequence++)
                    for (int constant = 0; constant < constants.length; constant++)
                        all[system * domains.length * consequences.length * constants.length + domain * consequences.length * constants.length + constants.length * consequence + constant] = semanticsCube[system][domain][constant][consequence];//
        }
        all_supported = rigid;
    }

    public static String cubeEntryIndexToString(int system, int domain, int constant, int consequence){
        StringBuilder sb = new StringBuilder();
        String ret = systems[system] + domains[domain] + constants[constant] + consequences[consequence];
        ret = ret.replaceAll("[\\$]","_");
        ret = ret.substring(14).toLowerCase();
        return ret;
    }

    public static String semanticsToTPTPSpecification(int system, int domain, int constant, int consequence) {
        return semanticsCube[system][domain][constant][consequence];
    }

    public static int systemToInt(String system) {
        return Arrays.asList(systems).indexOf(system);
    }

    public static int systemCommonNameToInt(String system) {
        String system0 = "$modal_system_" + system;
        return systemToInt(system0);
    }

    public static int domainToInt(String domain) {
        return Arrays.asList(domains).indexOf(domain);
    }

    public static int domainCommonNameToInt(String domain) {
        String domain0 = "$" + domain;
        return domainToInt(domain0);
    }

    public static int consequenceToInt(String consequence) {
        return Arrays.asList(consequences).indexOf(consequence);
    }

    public static int consequenceCommonNameToInt(String consequence) {
        String consequence0 = "$" + consequence;
        return consequenceToInt(consequence0);
    }

    public static int rigidityToInt(String rigidity) {
        return Arrays.asList(constants).indexOf(rigidity);
    }

    public static int rigidityCommonNameToInt(String rigidity) {
        String rigidity0 = "$" + rigidity;
        return rigidityToInt(rigidity0);
    }

    public static String thfName(String sentence){
        if (sentence.equals("")) return "";
        int start = sentence.indexOf("(");
        int end = sentence.indexOf(",",start+1);
        if (start < 0 || end < 0) return "";
        return sentence.substring(start+1,end).trim();
    }

}
