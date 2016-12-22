package main;

import util.external_software_wrappers.ProblemTesterSatallax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestProblems {

    private static final Logger log = Logger.getLogger("default");

    public static String resultPath;
    /*
    public static void test(Path inPath){
        System.out.println("processing " + inPath);
        Path directory = Paths.get(resultPath,inPath.getParent().getFileName().toString());
        if (!Files.exists(directory)){
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                System.err.println("Could not create directory "+ directory.toString());
                e.printStackTrace();
            }
        }
        Path outPath = Paths.get(directory.toString(),inPath.getFileName().toString());
        try {
            String problem = new String(Files.readAllBytes(inPath));
            try{
                Files.write(outPath,problem.getBytes());
            }catch (IOException f){
                System.err.println("Could not write to file " + outPath.toString());
                f.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Could not open file " + inPath.toString());
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) {
        if (args.length != 2){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n");
            System.exit(1);
        }

        String inPathString = args[0]; // "/home/tg/university/bachelor_thesis/QMLTP-v1.1/";
        String outputPathString = args[1]; // "/home/tg/university/bachelor_thesis/software/output/convert_qmltp_to_thf/";
        TestProblems.resultPath = outputPathString;

        if (!Files.isDirectory(Paths.get(inPathString))){
            System.err.println("embedded qmltp path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(inPathString + " is not a valid directory");
        }

        if (!Files.isDirectory(Paths.get(outputPathString))){
            System.err.println("result path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(outputPathString + " is not a valid directory");
        }

        ProblemTesterSatallax tester = new ProblemTesterSatallax();
        try {
            tester.testProblemDirectory(Paths.get(inPathString),Paths.get(outputPathString),20,TimeUnit.SECONDS);
        } catch (IOException e) {
            System.err.println("Could not traverse files");
            e.printStackTrace();
        }
        /*
        Path problemsPath = Paths.get(qmltpPath,"Problems");
        Path outputPath = Paths.get(outputPathString);
        Path inDot = Paths.get(outputPath.toString(),"ouput.in.dot");
        Path outDot = Paths.get(outputPath.toString(),"ouput.out.dot");
        String dotBin = "dot";
        String semantics = SemanticsGenerator.standard_s5;
        Wrappers.convertModal(problemsPath,outputPath,inDot,outDot,dotBin,semantics);
        try (Stream<Path> paths = Files.walk(problemsPath)) {
            paths.filter(Files::isRegularFile).filter(p->!p.toString().contains("/MML/")).forEach(TestProblems::test);
        } catch (IOException e) {
            System.err.println("Could not scan directory properly");
            e.printStackTrace();
        }
        */

    }
}


