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
        systems = new String[26];
        systems[0] = "$modal_system_K";
        systems[1] = "$modal_system_KB";
        systems[2] = "$modal_system_K4";
        systems[3] = "$modal_system_K5";
        systems[4] = "$modal_system_K45";
        systems[5] = "$modal_system_KB5"; // K+B+5
        systems[6] = "$modal_system_KB5_KB5"; // K+B+5
        systems[7] = "$modal_system_KB5_KB4"; // K+B+4
        systems[8] = "$modal_system_KB5_KB45"; // K+B+4+5
        systems[9] = "$modal_system_D";
        systems[10] = "$modal_system_DB";
        systems[11] = "$modal_system_D4";
        systems[12] = "$modal_system_D5";
        systems[13] = "$modal_system_D45";
        systems[14] = "$modal_system_T";
        systems[15] = "$modal_system_B"; // K+B+T
        systems[16] = "$modal_system_S4";
        systems[17] = "$modal_system_S5"; // K+T+5
        systems[18] = "$modal_system_S5_KT5"; // K+T+5
        systems[19] = "$modal_system_S5_KTB5"; // K+T+B+5
        systems[20] = "$modal_system_S5_KT45"; // K+T+4+5
        systems[21] = "$modal_system_S5_KTB4"; // K+T+4+B
        systems[22] = "$modal_system_S5_KDB4"; // K+D+4+B
        systems[23] = "$modal_system_S5_KDB45"; // K+D+4+B+5
        systems[24] = "$modal_system_S5_KDB5"; // K+D+B+5
        systems[25] = "$modal_system_S5U";

        domains = new String[4];
        domains[0] = "$constant";
        domains[1] = "$varying";
        domains[2] = "$cumulative";
        domains[3] = "$decreasing";

        constants = new String[1];
        constants[0] = "$rigid";
        //constants[1] = "$flexible";

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

    public static String semanticsToTPTPSpecification(int system, int domain, int constants, int consequence) {
        return semanticsCube[system][domain][constants][consequence];
    }

    // names as in the tptp semantics specification
    public static String semanticsToTPTPSpecification(String system, String domain, String constants, String consequence) {
        /*
        System.out.println(system + " " + systemToInt(system));
        System.out.println(domain + " " + domainToInt(domain));
        System.out.println(constants + " " + rigidityToInt(constants));
        System.out.println(consequence + " " + consequenceToInt(consequence));
        System.out.println(semanticsCube[systemToInt(system)][domainToInt(domain)][rigidityToInt(constants)][consequenceToInt(consequence)]);*/
        return semanticsCube[systemToInt(system)][domainToInt(domain)][rigidityToInt(constants)][consequenceToInt(consequence)];
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
