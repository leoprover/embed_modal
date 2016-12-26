package main;

import exceptions.*;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import transformation.EmbeddingDefinitions;
import transformation.SemanticsGenerator;
import transformation.Wrappers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.*;


public class EmbedModal {

    private static final Logger log = Logger.getLogger( "default" );

    private static CommandLine argsParse(String[] args){
        /*
        -f
        modal
        -i
        /home/tg/university/bachelor_thesis/software/src/test/tptp_files/modal1.p
        -o
        /home/tg/university/bachelor_thesis/software/output.p
        -dotin
        /home/tg/university/bachelor_thesis/software/output.in.dot
        -dotout
        /home/tg/university/bachelor_thesis/software/output.out.dot
        -log
        /home/tg/university/bachelor_thesis/software/log.log
         */
        Options options = new Options();
        String[] validFormats = new String[]{"modal", "free"};
        options.addOption( Option.builder("h")
                .longOpt( "help" )
                .desc( "Print help"  )
                .build()
        );
        options.addOption( Option.builder("f")
                .longOpt( "format" )
                .desc( "Input format. Valid values: modal,free"  )
                .hasArg()
                .argName( "FORMAT" )
                .required()
                .build()
        );
        options.addOption( Option.builder("i")
                .longOpt( "input" )
                .desc( "Input file"  )
                .hasArg()
                .argName( "INPUT_FILE" )
                .required()
                .build()
        );
        options.addOption( Option.builder("o")
                .longOpt( "output" )
                .desc( "Output file"  )
                .hasArg()
                .argName( "OUTPUT_FILE" )
                .required()
                .build()
        );
        options.addOption( Option.builder("dotin")
                .desc( "Output file"  )
                .hasArg()
                .argName( "INPUT_DOT_FILE" )
                .build()
        );
        options.addOption( Option.builder("dotout")
                .desc( "Output file"  )
                .hasArg()
                .argName( "OUTPUT_DOT_FILE" )
                .build()
        );
        options.addOption( Option.builder("dotbin")
                .desc( "Dot binary from graphviz"  )
                .hasArg()
                .argName( "DOT_BIN" )
                .build()
        );
        options.addOption( Option.builder("semantics")
                //.desc( "Entry from semantics cube [systems][domains][constants][consequences] or all for creating all available semantical options"  )
                .desc("standard_s5 or all")
                .hasArg()
                .argName( "SEMANTICS" )
                .build()
        );
        options.addOption( Option.builder("log")
                .desc( "log file" )
                .hasArg()
                .argName( "LOG" )
                .build()
        );
        CommandLineParser parser = new DefaultParser();
        //log.setUseParentHandlers(false);
        //log.setLevel(Level.ALL);
        ConsoleHandler cHandler = new ConsoleHandler();
        log.addHandler(cHandler);
        cHandler.setLevel(Level.ALL);
        try {
            CommandLine line = parser.parse( options, args);
            String format = line.getOptionValue("f");
            if (!Arrays.stream(validFormats).anyMatch(format::contains))
                throw new CliException("Invalid format. Valid formats: " + String.join(",",validFormats));
            try {
                if (line.hasOption("log")){
                    Handler handler = new FileHandler( line.getOptionValue("log") );
                    handler.setLevel(Level.ALL);
                    log.addHandler(handler);
                }
            } catch (IOException e) {
                log.warning("Could not open log file " + line.getOptionValue("LOG") + ": " + e.getMessage());
            }


            return line;
        }
        catch( ParseException | CliException e){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "embedlogic", options );
            if (args.length == 1 && args[0].equals("-h") || args[0].equals("--help")){
                System.exit(0);
            }
            System.err.println("Invalid arguments. Reason: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }




    private static String[] resolveSemantics(String s){
        if (s == null) return null;
        String[] ret = null;
        if (s.equals("standard_s5")){
            ret = new String[1];
            ret[0] = SemanticsGenerator.semanticsCube[4][0][0][0];
        }else if (s.equals("all")){
            ret = SemanticsGenerator.rigid_constant_global;
        }else{
            log.severe("Unsupported semantics " + s);
            System.exit(1);
        }
        return ret;
    }

    public static void main(String[] args) throws Exception {

        CommandLine cl = argsParse(args);

        // modal logic embedding
        if (cl.getOptionValue("f").equals("modal")) {
            Path inPath = Paths.get(cl.getOptionValue("i"));

            // input is directory
            if (Files.isDirectory(inPath)){
                String semantics = null;
                boolean inDot = false;
                if (cl.hasOption("dotin")) inDot = true;
                boolean outDot = false;
                if (cl.hasOption("dotout")) outDot = true;
                String dot = null;
                if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");
                if (cl.hasOption("semantics")) semantics = cl.getOptionValue("semantics");

                Wrappers.convertModalMultipleSemanticsTraverseDirectory(inPath,cl.getOptionValue("o"),inDot,outDot,dot,resolveSemantics(semantics));

            // input is file
            }else{
                Path outPath = Paths.get(cl.getOptionValue("o"));
                Path inDot = null;
                if (cl.hasOption("dotin")) inDot = Paths.get(cl.getOptionValue("dotin"));
                Path outDot = null;
                if (cl.hasOption("dotout")) outDot = Paths.get(cl.getOptionValue("dotout"));
                String dot = null;
                if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");
                String semantics = null;
                if (cl.hasOption("semantics")) semantics = cl.getOptionValue("semantics");
                Wrappers.convertModalMultipleSemantics(inPath,outPath,inDot,outDot,dot,resolveSemantics(semantics));
            }
        }

        // free logic embedding
        else if (cl.getOptionValue("f").equals("free")){
            System.out.println("Free logic embedding not implemented yet.");
        }

        //System.out.println(EmbeddingDefinitions.getAllDefinitions());
        System.exit(0);
    }

}
