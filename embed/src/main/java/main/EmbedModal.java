package main;

import exceptions.*;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import transformation.SemanticsGenerator;
import transformation.Wrappers;
import util.SingleLineFormatter;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.*;


public class EmbedModal {

    private static final Logger log = Logger.getLogger( "default" );

    private static CommandLine argsParse(String[] args){
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
        options.addOption( Option.builder("diroutput")
                .desc( "directory output structure" )
                .hasArg()
                .argName( "DIROUTPUT" )
                .build()
        );
        options.addOption( Option.builder("log")
                .desc( "log file" )
                .hasArg()
                .argName( "LOG" )
                .build()
        );
        options.addOption( Option.builder("loglevel")
                .desc( "warning, info, finest" )
                .hasArg()
                .argName( "LOGLEVEL" )
                .build()
        );
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse( options, args);
            // setup logging
            Level level = Level.INFO;
            if (line.hasOption("loglevel")) {
                String loglevel = line.getOptionValue("loglevel");
                switch (loglevel) {
                    case "warning":
                        level = Level.WARNING;
                        break;
                    case "info":
                        level = Level.INFO;
                        break;
                    case "finest":
                        level = Level.FINEST;
                        break;
                    default:
                        throw new CliException("Invalid loglevel. Valid levels are warning, info, finest");
                }
            }
            ConsoleHandler cHandler = new ConsoleHandler();
            cHandler.setLevel(level);
            cHandler.setFormatter(new SingleLineFormatter());
            log.addHandler(cHandler);
            log.setUseParentHandlers(false);
            log.setLevel(level);

            String format = line.getOptionValue("f");
            if (!Arrays.stream(validFormats).anyMatch(format::contains))
                throw new CliException("Invalid format. Valid formats: " + String.join(",",validFormats));
            try {
                // setup log file
                if (line.hasOption("log")){
                    Path logDirectory = Paths.get(line.getOptionValue("log")).getParent();
                    if (Files.exists(logDirectory)){
                        if (!Files.isDirectory(logDirectory)){
                            log.warning("log file directory path exists but is not a valid directory: " + logDirectory.toString());
                        }
                    } else {
                        Files.createDirectory(logDirectory);
                        log.info("Created directory for logging: " + logDirectory.toString());
                    }
                    Handler handler = new FileHandler( line.getOptionValue("log") );
                    handler.setFormatter(new SingleLineFormatter());
                    handler.setLevel(level);
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


    // SemanticsCube : [modal_system][domains][constants][consequence]
    private static String[] resolveSemantics(String s){
        if (s == null) return null;
        String[] ret = null;
        switch (s) {
            case "standard_s5":
                ret = new String[1];
                ret[0] = SemanticsGenerator.semanticsCube[4][0][0][0];
                break;
            case "constant_rigid_global":
                ret = SemanticsGenerator.constant_rigid_global;
                break;
            case "rigid_local":
                ret = SemanticsGenerator.rigid_local;
                break;
            case "rigid":
                ret = SemanticsGenerator.rigid;
                break;
            case "all":
            case "all_supported":
                ret = SemanticsGenerator.rigid;
                break;
            default:
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
                log.info("Input is directory " + inPath.toString());
                Path outPath = Paths.get(cl.getOptionValue("o"));
                if (Files.exists(outPath)){
                    if (!Files.isDirectory(outPath)){
                        log.severe("Outpath already exists and is not a valid directory: " + outPath.toString());
                        System.exit(1);
                    }
                    log.info("Output directory already exists. Files may be overwritten.");
                } else {
                    Files.createDirectory(outPath);
                    log.info("Created output directory " + outPath.toString());
                }
                String semantics = null;
                boolean inDot = false;
                if (cl.hasOption("dotin")){
                    inDot = true;
                    if (!cl.getOptionValue("dotin").equals("yes")){
                        log.severe("For directory input dotin has to be either yes or left out completely");
                    }
                }
                boolean outDot = false;
                if (cl.hasOption("dotout")){
                    outDot = true;
                    if (!cl.getOptionValue("dotout").equals("yes")){
                        log.severe("For directory input dotout has to be either yes or left out completely");
                    }
                }
                String dot = null;
                if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");
                // semantics have to be provided
                if (cl.hasOption("semantics")){
                    semantics = cl.getOptionValue("semantics");
                    if (!cl.hasOption("diroutput")){
                        log.severe("Please specify a directory output structure using -diroutput <structure>. Valid values are: joint,splitted");
                        System.exit(1);
                    }
                    if (!(cl.getOptionValue("diroutput").equals("joint") || cl.getOptionValue("diroutput").equals("splitted"))){
                        log.severe("This is not a valid value for diroutput: " + cl.getOptionValue("diroutput") + ". Valid values are: joint,splitted");
                        System.exit(1);
                    }
                    if (cl.getOptionValue("diroutput").equals("splitted")) Wrappers.convertModalMultipleSemanticsOnMultipleDirectoriesTraverseDirectory(inPath,cl.getOptionValue("o"),inDot,outDot,dot,resolveSemantics(semantics));
                    if (cl.getOptionValue("diroutput").equals("joint")) Wrappers.convertModalMultipleSemanticsTraverseDirectory(inPath,cl.getOptionValue("o"),inDot,outDot,dot,resolveSemantics(semantics));
                // no semantics will be provided
                } else {
                    Wrappers.convertModalMultipleSemanticsOnMultipleDirectoriesTraverseDirectory(inPath, cl.getOptionValue("o"), inDot, outDot, dot, resolveSemantics(semantics));
                }
            // input is file
            }else{
                log.info("Input is file " + inPath.toString());
                Path outPath = Paths.get(cl.getOptionValue("o"));
                Path inDot = null;
                if (cl.hasOption("dotin")) inDot = Paths.get(cl.getOptionValue("dotin"));
                Path outDot = null;
                if (cl.hasOption("dotout")) outDot = Paths.get(cl.getOptionValue("dotout"));
                String dot = null;
                if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");
                String semantics = null;
                // semantics have to be provided
                if (cl.hasOption("semantics")) {
                    semantics = cl.getOptionValue("semantics");
                    String[] semanticsList = resolveSemantics(semantics);
                    if (semanticsList.length == 1) {
                        Wrappers.convertModal(inPath, outPath, inDot, outDot, dot, semanticsList[0]);
                    } else {
                        Wrappers.convertModalMultipleSemantics(inPath, outPath, inDot, outDot, dot, semanticsList);
                    }
                // no semantics will be provided
                } else {
                    Wrappers.convertModalMultipleSemantics(inPath, outPath, inDot, outDot, dot, null);
                }
            }
        }

        // free logic embedding
        else if (cl.getOptionValue("f").equals("free")){
            System.out.println("Free logic embedding has not been implemented yet.");
        }

        System.exit(0);
    }

}
