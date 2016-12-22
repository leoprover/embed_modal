package main;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ConvertQmltpToThf {

    private static final Logger log = Logger.getLogger( "default" );

    public static void main(String[] args) {

        log.setLevel(Level.ALL);

        if (args.length != 2){
            System.err.println("Unmatched argument size\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n");
            System.exit(1);
        }

        String qmltpPath = args[0]; // "/home/tg/university/bachelor_thesis/QMLTP-v1.1/";
        String resultPath = args[1]; // "/home/tg/university/bachelor_thesis/software/output/convert_qmltp_to_thf/";

        if (!Files.isDirectory(Paths.get(qmltpPath))){
            System.err.println("path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(qmltpPath + " is not a valid directory");
            System.exit(1);
        }

        if (!Files.isDirectory(Paths.get(resultPath))){
            System.err.println("result path is not a directory\nTwo arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result/");
            System.err.println(resultPath + " is not a valid directory");
            System.exit(1);
        }

        Path problemsPath = Paths.get(qmltpPath);
        Wrapper.convertQmfTraverseDirectories(problemsPath,resultPath,true,true,"dot");

    }

}
