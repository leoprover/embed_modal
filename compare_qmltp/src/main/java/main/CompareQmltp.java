package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompareQmltp {

    public static void main(String[] args) {

        if (args.length != 4){
            System.err.println("Unmatched argument size\nFour arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n"+
                    "<system>\n" +
                    "<domain>\n"
                    );
            System.exit(1);
        }

        String qmltp = args[0];
        String test_results = args[1];
        String system = args[2];
        String domain = args[3];

        if (!Files.isDirectory(Paths.get(qmltp))){
            System.err.println("Unmatched argument size\nFour arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n"+
                    "<system>\n" +
                    "<domain>\n"
            );
            System.err.println(qmltp + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isRegularFile(Paths.get(test_results))){
            System.err.println("Unmatched argument size\nFour arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n"+
                    "<system>\n" +
                    "<domain>\n"
            );
            System.err.println(test_results + " is not a valid file");
            System.exit(1);
        }

        try{
            Results results = new Results();
            List<String> result_content = Files.readAllLines(Paths.get(test_results));
            int processing = 0;
            for (String res : result_content){
                processing++;
                String[] split = res.split(",");
                System.out.println("Processing " + processing + " ::: " + split[0]);
                String category = split[0].substring(0,3);
                String problem_name = split[0];
                Path problem_filename = Paths.get(qmltp,category,problem_name);
                String problem = new String(Files.readAllBytes(problem_filename));

                Problem p = new Problem();
                p.name = problem_name;
                p.category = category;
                p.status_qmltp = getStatusFromComments(problem,system,domain);
                p.status_satallax = split[1];
                p.time_satallax = Double.valueOf(split[2]);
                p.status_leo = split[3];
                p.time_leo = Double.valueOf(split[4]);
                p.status_nitpick = split[5];
                p.time_nitpick = Double.valueOf(split[6]);
                results.addProblem(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getStatusFromComments(String problem, String system, String domain){
        String[][] matrix = new String[5][3];

        boolean status_found = false;
        int systems = 0;
        String[] lines = problem.split("\n");
        for (String line : lines){
            if (systems == 5) break;
            if (status_found){
                String[] split = line.split("\\s+");
                List<String> entries = Arrays.asList(split).subList(2, 5);
                matrix[systems] = entries.toArray(new String[entries.size()]);
                systems++;
            }
            if (line.contains("Status")) status_found = true;
        }

        int sys_index = 0;
        int dom_index = 0;
        if (system.equals("k")) sys_index = 0;
        else if (system.equals("d")) sys_index = 1;
        else if (system.equals("t")) sys_index = 2;
        else if (system.equals("s4")) sys_index = 3;
        else if (system.equals("s5")) sys_index = 4;
        else {
            System.err.println("Invalid system: " + system);
            System.exit(1);
        }
        if (domain.equals("varying")) dom_index = 0;
        else if (domain.equals("cumulative")) dom_index = 1;
        else if (domain.equals("constant")) dom_index = 2;
        else {
            System.err.println("Invalid domain: " + domain);
            System.exit(1);
        }
        /*
        for (String[] line : matrix){
            String out = "";
            for (String e:line){
                out = out + " " + e;
            }
            System.out.println(out);
        }
        */
        return convertQmltpStatus(matrix[sys_index][dom_index]);
    }

    private static String convertQmltpStatus(String status){
        if (status.equals("Theorem")) return "THM";
        if (status.equals("Non-Theorem")) return "CSA";
        if (status.equals("Unsolved")) return "UNK";
        System.out.println("NEW STATUS: " + status);
        return "";
    }

    /*
    %--------------------------------------------------------------------------
% File     : APM001+1 : QMLTP v1.1
% Domain   : Applications mixed
% Problem  : Belief Change in man-machine-dialogues
% Version  : Especial.
% English  :

% Refs     : [FHL+98] L. Farinas del Cerro, A. Herzig, D. Longin, O. Rifi.
%             Belief Reconstruction in Cooperative Dialogues. AIMSA 1998,
%             LNCS 1480, pp. 254-266. Springer, 1998.
% Source   : [FHL98]
% Names    :

% Status   :      varying      cumulative   constant
%             K   Theorem      Theorem      Theorem       v1.1
%             D   Theorem      Theorem      Theorem       v1.1
%             T   Theorem      Theorem      Theorem       v1.1
%             S4  Theorem      Theorem      Theorem       v1.1
%             S5  Theorem      Theorem      Theorem       v1.1
%
% Rating   :      varying      cumulative   constant
%             K   0.00         0.00         0.00          v1.1
%             D   0.00         0.17         0.17          v1.1
%             T   0.00         0.00         0.00          v1.1
%             S4  0.00         0.00         0.00          v1.1
%             S5  0.00         0.00         0.00          v1.1
%
%  term conditions for all terms: designation: rigid, extension: local
%
% Comments :

%--------------------------------------------------------------------------

     */
}


