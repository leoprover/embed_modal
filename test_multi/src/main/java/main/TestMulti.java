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

        if (!(args.length == 4 || args.length == 5)){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n" +
                    "timeout\n");
            System.exit(1);
        }

        String inPathString = args[0]; // "/home/tg/university/bachelor_thesis/QMLTP-v1.1/";
        String outputPathString = args[1]; // "/home/tg/university/bachelor_thesis/software/output/convert_qmltp_to_thf/";
        String progress = args[2];

        if (!Files.isDirectory(Paths.get(inPathString))){
            System.err.println("input path is not a directory\nThree arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n" +
                    "timeout\n");
            System.err.println(inPathString + " is not a valid directory");
            System.exit(1);
        }

        if (Files.exists(Paths.get(outputPathString))){
            System.err.println("result path is not a directory\nThree arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n" +
                    "timeout\n");
            System.err.println(outputPathString + " is not a valid directory");
            System.exit(1);
        }

        MultiTester tester = new MultiTester();
        Path filterList = null;
        if (args.length == 5) filterList = Paths.get(args[4]);
        long timeout = Long.valueOf(args[3]);
        try {
            tester.testProblemDirectory(Paths.get(inPathString),Paths.get(outputPathString),timeout,TimeUnit.SECONDS,filterList,Paths.get(progress));
        } catch (IOException e) {
            System.err.println("Could not traverse files");
            e.printStackTrace();
        }

    }
}


