package main;

import util.external_software_wrappers.NativeMultiTester;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMultiNative {

    private static final Logger log = Logger.getLogger("default");

    public static void main(String[] args) {
        //log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);

        if (args.length!=5){
            System.err.println("Unmatched argument size\nThree arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/file\n" +
                    "/path/to/progress/file\n" +
                    "śystem\n" +
                    "domain\n" +
                    "timeout\n");
            System.exit(1);
        }

        String inPathString = args[0]; // problem input directory
        String outputPathString = args[1]; // output file for results
        String progress = args[2]; // output file which stores progress
        String system = args[3]; // semantics: axiom system
        String domain = args[4]; // semantics: domain
        String timeout_string = args[5]; // timeout for prover
        long timeout = Long.valueOf(timeout_string);

        if (!Files.isDirectory(Paths.get(inPathString))){
            System.err.println("Unmatched argument size\nFive arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/file\n" +
                    "/path/to/progress/file\n" +
                    "śystem\n" +
                    "domain\n" +
                    "timeout\n");
            System.err.println(inPathString + " is not a valid directory");
            System.exit(1);
        }

        if (Files.exists(Paths.get(outputPathString))){
            System.err.println("Five arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/file\n" +
                    "/path/to/progress/file\n" +
                    "śystem\n" +
                    "domain\n" +
                    "timeout\n");
            System.err.println(outputPathString + " already exists!");
            System.exit(1);
        }


        String[] semantics = {"d", "t", "s4", "s5"};
        String[] domains = {"const", "cumul", "vary"};

        if (!Arrays.asList(semantics).contains(system)){
            System.err.println("Five arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/file\n" +
                    "/path/to/progress/file\n" +
                    "śystem\n" +
                    "domain\n" +
                    "timeout\n");
            System.err.println(semantics + " is not a valid axiom system");
            System.exit(1);
        }

        if (!Arrays.asList(domains).contains(domain)){
            System.err.println("Five arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/file\n" +
                    "/path/to/progress/file\n" +
                    "śystem\n" +
                    "domain\n" +
                    "timeout\n");
            System.err.println(semantics + " is not a quantification semantic");
            System.exit(1);
        }

        NativeMultiTester tester = new NativeMultiTester();
        try {
            tester.testProblemDirectory(Paths.get(inPathString),Paths.get(outputPathString),timeout,TimeUnit.SECONDS,Paths.get(progress),system,domain);
        } catch (IOException e) {
            System.err.println("Could not walk path " + inPathString);
            e.printStackTrace();
        }


/*
        for (String curSemantics : semantics) {
            for (String curDomains : domains) {
                try {
                    tester.testProblemDirectory(Paths.get(inPathString),Paths.get(outputPathString),timeout,TimeUnit.SECONDS,filterList,Paths.get(progress), curSemantics, curDomains);
                } catch (IOException e) {
                    System.err.println("Could not traverse files");
                    e.printStackTrace();
                }
            }
        }*/


    }
}


