package main;

import util.external_software_wrappers.MultiTester;
import util.external_software_wrappers.ProblemTesterSatallax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMulti {

    private static final Logger log = Logger.getLogger("default");

    public static void main(String[] args) {
        //log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);

        if (!(args.length == 2 || args.length == 3)){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n");
            System.exit(1);
        }

        String inPathString = args[0]; // "/home/tg/university/bachelor_thesis/QMLTP-v1.1/";
        String outputPathString = args[1]; // "/home/tg/university/bachelor_thesis/software/output/convert_qmltp_to_thf/";

        if (!Files.isDirectory(Paths.get(inPathString))){
            System.err.println("input path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(inPathString + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(outputPathString))){
            System.err.println("result path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(outputPathString + " is not a valid directory");
            System.exit(1);
        }

        MultiTester tester = new MultiTester();
        Path filterList = null;
        if (args.length == 3) filterList = Paths.get(args[2]);
        try {
            tester.testProblemDirectory(Paths.get(inPathString),Paths.get(outputPathString),3,TimeUnit.SECONDS,filterList);
        } catch (IOException e) {
            System.err.println("Could not traverse files");
            e.printStackTrace();
        }

    }
}


