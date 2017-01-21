package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CompareQmltp {

    public static void main(String[] args) {

        if (args.length != 4){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n" +
                    "/path/to/output\n"
                    );
            System.exit(1);
        }

        String qmltp = args[0];
        String test_results_multi = args[1];
        String test_results_multi_native = args[2];
        String output = args[3];

        if (!Files.isDirectory(Paths.get(qmltp))){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n" +
                    "/path/to/output\n"
            );
            System.err.println(qmltp + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(test_results_multi))){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n" +
                    "/path/to/output\n"
            );
            System.err.println(test_results_multi + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(test_results_multi_native))){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n" +
                    "/path/to/output\n"
            );
            System.err.println(test_results_multi + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(output))){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n" +
                    "/path/to/output\n"
            );
            System.err.println(output + " is not a valid directory");
            System.exit(1);
        }

        Results results = new Results();
        try(Stream<Path> paths = Files.walk(Paths.get(test_results_multi))) {
            paths.filter(Files::isRegularFile).forEach(f -> {
                try {
                    Path f2 = Paths.get(test_results_multi_native,f.getFileName().toString()); // mleancop
                    List<Path> result_files = new ArrayList<>();
                    result_files.add(f);
                    result_files.add(f2);
                    readResults(result_files,qmltp,results);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        results.evaluate(output);
    }

    private static void readResults(List<Path> result_files, String qmltp_directory, Results results) throws IOException {
        if (result_files.isEmpty()){
            System.err.println("No results");
            System.exit(1);
        }
        Path f0 = result_files.get(0);
        Path f1 = result_files.get(1);
        System.out.println("### Processing " + f0.toString());
        Test test = new Test();
        String system = getSystemFromTestFilename(f0.getFileName().toString());
        String domain = getDomainFromTestFilename(f0.getFileName().toString());
        String constants = getConstantsFromTestFilename(f0.getFileName().toString());
        String consequence = getConsequenceFromTestFilename(f0.getFileName().toString());
        test.test_name = f0.getFileName().toString();
        test.system = system;
        test.domains = domain;
        test.constants = constants;
        test.consequence = consequence;
        int processing = 0;
        List<String> content_multitester = Files.readAllLines(f0);
        List<String> content_multinative = null;
        if (Files.exists(f1)) {
            System.out.println("FILENAME exists");
            System.out.println(f1);
            content_multinative = Files.readAllLines(f1);
        } else {
            System.out.println("FILENAME exists");
            System.out.println(f1);
        }
        for (int i = 0; i < content_multitester.size(); i++){
            String result_multitester = content_multitester.get(i);
            processing++;
            String[] split = result_multitester.split(",");
            System.out.println("Processing " + processing + " ::: " + split[0]);
            String category = split[0].substring(0,3);
            String problem_name = split[0];
            Path problem_filename = Paths.get(qmltp_directory,category,problem_name);
            String problem = new String(Files.readAllBytes(problem_filename));

            Problem p = new Problem();
            p.name = problem_name;
            p.problem = problem;
            p.system = system;
            p.domains = domain;
            p.constants = constants;
            p.consequence = consequence;
            p.category = category;
            p.status_qmltp = getStatusFromComments(problem,system,domain,constants,consequence);
            p.rating = getRatingFromComments(problem,system,domain,constants,consequence);
            p.status_satallax = split[1];
            p.time_satallax = Double.valueOf(split[2]);
            p.status_leo = split[3];
            p.time_leo = Double.valueOf(split[4]);
            p.status_nitpick = split[5];
            p.time_nitpick = Double.valueOf(split[6]);

            if (content_multinative != null) {
                String result_multinative = content_multinative.get(i);
                String[] split2 = result_multinative.split(",");
                p.status_mleancop = split2[1];
                p.time_mleancop = Double.valueOf(split2[2]);
            }

            test.addProblem(p);
        }
        results.addTest(test);
    }

    private static String getSystemFromTestFilename(String filename){
        String[] split = filename.split("_");
        return split[0];
    }

    private static String getDomainFromTestFilename(String filename){
        String[] split = filename.split("_");
        return split[1];
    }

    private static String getConstantsFromTestFilename(String filename){
        String[] split = filename.split("_");
        return split[2];
    }

    private static String getConsequenceFromTestFilename(String filename){
        String[] split = filename.split("_");
        return split[3];
    }

    private static String getRatingFromComments(String problem, String system, String domain, String constants, String consequence){
        if (!(domain.equals("constant") || domain.equals("varying") || domain.equals("cumulative"))) return null;
        if (!(system.equals("k") || system.equals("d") || system.equals("t") || system.equals("s4") || system.equals("s5"))) return null;
        if (!(constants.equals("rigid"))) return null;
        if (!(consequence.equals("local"))) return null;

        String[][] matrix = new String[5][3];
        boolean rating_found = false;
        int systems = 0;
        String[] lines = problem.split("\n");
        for (String line : lines){
            if (systems == 5) break;
            if (rating_found){
                String[] split = line.split("\\s+");
                List<String> entries = Arrays.asList(split).subList(2, 5);
                matrix[systems] = entries.toArray(new String[entries.size()]);
                systems++;
            }
            if (line.contains("Rating")) rating_found = true;
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

        for (String[] line : matrix){
            String out = "";
            for (String e:line){
                out = out + " " + e;
            }
            System.out.println(out);
        }

        return convertQmltpStatus(matrix[sys_index][dom_index]);
    }

    private static String getStatusFromComments(String problem, String system, String domain, String constants, String consequence){
        if (!(domain.equals("constant") || domain.equals("varying") || domain.equals("cumulative"))) return null;
        if (!(system.equals("k") || system.equals("d") || system.equals("t") || system.equals("s4") || system.equals("s5"))) return null;
        if (!(constants.equals("rigid"))) return null;
        if (!(consequence.equals("local"))) return null; // TODO is it global or local for QMLTP?

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
        return "???";
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


