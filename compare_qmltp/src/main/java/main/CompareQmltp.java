package main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompareQmltp {

    private static final Logger log = Logger.getLogger("default");

    public static void main(String[] args) {
        //log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);

        if (args.length != 2){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/all/embedded/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n");
            System.exit(1);
        }

        String embedded_qmltp = args[0];
        String test_results = args[1];

        if (!Files.isDirectory(Paths.get(embedded_qmltp))){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/all/embedded/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n");
            System.err.println(embedded_qmltp + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(test_results))){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/all/embedded/qmltp/Problems/directory\n" +
                    "/path/to/test/results\n");
            System.err.println(test_results + " is not a valid directory");
            System.exit(1);
        }


    }
}


