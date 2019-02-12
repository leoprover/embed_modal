package main;

import exceptions.AnalysisException;
import exceptions.CliException;
import exceptions.TransformationException;
import org.apache.commons.cli.*;
import transformation.ModalTransformator;
import transformation.SemanticsGenerator;
import transformation.Wrappers;
import util.SingleLineFormatter;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

import static transformation.ModalTransformator.completeToDefaultParameterSet;
import static transformation.ModalTransformator.transformationParameterSetIsNotContradictory;


public class EmbedModal {

    private static final Logger log = Logger.getLogger( "default" );

    private static final String defaultCliSystemName = "K";
    private static final String defaultCliConstantsName = "rigid";
    private static final String defaultCliDomainsName = "constant";
    private static final String defaultCliConsequenceName = "local";
    private static List<String> cliSystemNames = new ArrayList<>();
    private static List<String> cliConstantNames = new ArrayList<>();
    private static List<String> cliDomainNames = new ArrayList<>();
    private static List<String> cliConsequenceNames = new ArrayList<>();
    static{
        cliSystemNames.addAll(Arrays.stream(SemanticsGenerator.systems).map(s->s.substring(14)).collect(Collectors.toList()));
        cliConstantNames.addAll(Arrays.stream(SemanticsGenerator.constants).map(s->s.substring(1)).collect(Collectors.toList()));
        cliDomainNames.addAll(Arrays.stream(SemanticsGenerator.domains).map(s->s.substring(1)).collect(Collectors.toList()));
        cliConsequenceNames.addAll(Arrays.stream(SemanticsGenerator.consequences).map(s->s.substring(1)).collect(Collectors.toList()));
    }

    private static Set<ModalTransformator.TransformationParameter> defaultCliTransformationParameters = new HashSet<>();
    private static Map<String, ModalTransformator.TransformationParameter> cliTransformationParameterNames = new HashMap<>();
    static {
        defaultCliTransformationParameters.add(ModalTransformator.TransformationParameter.SEMANTIC_MODALITY_AXIOMATIZATION);
        defaultCliTransformationParameters.add(ModalTransformator.TransformationParameter.SEMANTIC_MONOTONIC_QUANTIFICATION);
        defaultCliTransformationParameters.add(ModalTransformator.TransformationParameter.SEMANTIC_ANTIMONOTONIC_QUANTIFICATION);
        for (ModalTransformator.TransformationParameter p : ModalTransformator.TransformationParameter.values()) {
            cliTransformationParameterNames.put(p.name().toLowerCase(),p);
        }
    }

    private static CommandLine argsParse(String[] args){
        Options options = new Options();
        options.addOption( Option.builder("h")
                .longOpt( "help" )
                .desc( "Print help"  )
                .build()
        );
        options.addOption( Option.builder("i")
                .longOpt( "input" )
                .desc( "Input file in THF format"  )
                .hasArg()
                //.argName( "INPUT_FILE" )
                .required()
                .build()
        );
        options.addOption( Option.builder("o")
                .longOpt( "output" )
                .desc( "Output file for embedding in THF"  )
                .hasArg()
                //.argName( "OUTPUT_FILE" )
                .required()
                .build()
        );
        options.addOption( Option.builder("dotin")
                .desc( "Output file of graphviz parse tree representation before embedding"  )
                .hasArg()
                //.argName( "INPUT_DOT_FILE" )
                .build()
        );
        options.addOption( Option.builder("dotout")
                .desc( "Output file of graphviz parse tree representation after embedding"  )
                .hasArg()
                //.argName( "OUTPUT_DOT_FILE" )
                .build()
        );
        options.addOption( Option.builder("dotbin")
                .desc( "Dot binary for applying graphviz to dotin and dotout"  )
                .hasArg()
                //.argName( "DOT_BIN" )
                .build()
        );
        options.addOption( Option.builder("systems")
                .desc("Modality semantics. Choices are: " + String.join(",", cliSystemNames))
                .hasArg()
                //.argName( "SYSTEM" )
                .build()
        );
        options.addOption( Option.builder("constants")
                .desc("Constant semantics. Choices are: " + String.join(",", cliConstantNames))
                .hasArg()
                //.argName( "CONSTANTS" )
                .build()
        );
        options.addOption( Option.builder("domains")
                .desc("Domain semantics. Choices are: " + String.join(",", cliDomainNames))
                .hasArg()
                //.argName( "DOMAIN" )
                .build()
        );
        options.addOption( Option.builder("consequences")
                .desc("Consequence semantics. Choices are: " + String.join(",", cliConsequenceNames))
                .hasArg()
                //.argName( "CONSEQUENCE" )
                .build()
        );
        options.addOption( Option.builder("t")
                .desc("Special transformation Parameters. Choices are: " + String.join(",", cliTransformationParameterNames.keySet()))
                .hasArg()
                //.argName( "CONSEQUENCE" )
                .build()
        );
        options.addOption( Option.builder("diroutput")
                .desc( "Directory output structure for directory input. Choices are \n" +
                        "joint: exactly one duplicate directory structure and a semantics description is appended to filenames containing the embedded problems\n" +
                        "splitted: one duplicate directory structure for every semantic" )
                .hasArg()
                //.argName( "DIROUTPUT" )
                .build()
        );
        options.addOption( Option.builder("log")
                .desc( "Log file" )
                .hasArg()
                //.argName( "LOG" )
                .build()
        );
        options.addOption( Option.builder("loglevel")
                .desc( "Choices are: warning, info, finest" )
                .hasArg()
                //.argName( "LOGLEVEL" )
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

            try {
                // setup log file
                if (line.hasOption("log")){
                    Path logDirectory = Paths.get(line.getOptionValue("log")).toAbsolutePath().getParent();
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
        catch( org.apache.commons.cli.ParseException | CliException e){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "embedlogic", options );
            if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))){
                System.exit(0);
            }
            System.err.println("Invalid arguments. Reason: " + e.getMessage());
            System.exit(1);
        }
        return null; // does not happen since either returns before or exits with status 1
    }

    private static List<String> resolveCliSemantics(String cliSystems, String cliConstants, String cliDomains, String cliConsequence) throws CliException {
        List<String> systems = Arrays.stream(cliSystems.split(",")).map(String::trim).filter(x->!x.equals("")).collect(Collectors.toList());
        List<String> constants = Arrays.stream(cliConstants.split(",")).map(String::trim).filter(x->!x.equals("")).collect(Collectors.toList());
        List<String> domains = Arrays.stream(cliDomains.split(",")).map(String::trim).filter(x->!x.equals("")).collect(Collectors.toList());
        List<String> consequences = Arrays.stream(cliConsequence.split(",")).map(String::trim).filter(x->!x.equals("")).collect(Collectors.toList());

        if (systems.isEmpty()) {
            systems.add(defaultCliSystemName);
            log.info("No system semantic specified. Using default: " + defaultCliSystemName);
        }
        if (constants.isEmpty()) {
            constants.add(defaultCliConstantsName);
            log.info("No constants semantic specified. Using default: " + defaultCliConstantsName);
        }
        if (domains.isEmpty()) {
            domains.add(defaultCliDomainsName);
            log.info("No domain semantic specified. Using default: " + defaultCliDomainsName);
        }
        if (consequences.isEmpty()) {
            consequences.add(defaultCliConsequenceName);
            log.info("No consequence semantic specified. Using default: " + defaultCliConsequenceName);
        }

        for (String x : systems) if (!cliSystemNames.contains(x)) throw new CliException(x + " is not a valid system semantic.");
        for (String x : constants) if (!cliConstantNames.contains(x)) throw new CliException(x + " is not a valid constant semantic.");
        for (String x : domains) if (!cliDomainNames.contains(x)) throw new CliException(x + " is not a valid domain semantic.");
        for (String x : consequences) if (!cliConsequenceNames.contains(x)) throw new CliException(x + " is not a valid consequence semantic.");

        List<String> semantics = new ArrayList<>();
        for (String cons : constants){
            for (String consequence : consequences){
                for (String dom : domains){
                    for (String sys : systems){
                        semantics.add(SemanticsGenerator.semanticsToTPTPSpecification(
                                "$modal_system_" + sys,"$" + dom,"$" + cons, "$" + consequence
                        ));
                    }
                }
            }
        }

        log.info("Considered systems: " + systems);
        log.info("Considered constants: " + constants);
        log.info("Considered domains: " + domains);
        log.info("Considered consequences: " + consequences);
        log.info("Resulting in " + semantics.size() + " different semantics.");

        return semantics;
    }

    private static List<String> getSemantics(CommandLine cl) throws CliException {
        String systems = "";
        String constants = "";
        String domains = "";
        String consequences = "";
        if (cl.hasOption("systems")) systems = cl.getOptionValue("systems");
        if (cl.hasOption("constants")) constants = cl.getOptionValue("constants");
        if (cl.hasOption("domains")) domains = cl.getOptionValue("domains");
        if (cl.hasOption("consequences")) consequences = cl.getOptionValue("consequences");
        return resolveCliSemantics(systems,constants,domains,consequences);
    }

    private static boolean cliContainsSemantics(CommandLine cl){
        return cl.hasOption("systems") || cl.hasOption("constants") || cl.hasOption("domains") || cl.hasOption("consequences");
    }

    private static Set<ModalTransformator.TransformationParameter> resolveTransformationParameters(String cliTransParams) throws CliException {
        Set<String> paramCliNames = Arrays.stream(cliTransParams.split(",")).map(String::trim).filter(x->!x.equals("")).collect(Collectors.toSet());
        for (String x : paramCliNames) if (!cliTransformationParameterNames.keySet().contains(x)) throw new CliException(x + " is not a valid transformation parameter.");
        Set<ModalTransformator.TransformationParameter> params = paramCliNames.stream().map(x->cliTransformationParameterNames.get(x)).collect(Collectors.toSet());
        params = completeToDefaultParameterSet(params);
        String validParams = transformationParameterSetIsNotContradictory(params);
        if (validParams != null) throw new CliException(validParams);
        return  params;
    }

    public static void main(String[] args) {
        CommandLine cl = argsParse(args);

        ModalTransformator.TransformationParameter[] transformationParameters = defaultCliTransformationParameters.toArray(new ModalTransformator.TransformationParameter[defaultCliTransformationParameters.size()]);
        if (cl.hasOption("t")){
            Set<ModalTransformator.TransformationParameter> tp = null;
            try {
                tp = resolveTransformationParameters(cl.getOptionValue("t"));
            } catch (CliException e) {
                log.severe("Could not resolve transformation parameters: " + e.getMessage());
                System.exit(1);
            }
            transformationParameters = tp.toArray(new ModalTransformator.TransformationParameter[tp.size()]);
        }

        Path inPath = Paths.get(cl.getOptionValue("i")).toAbsolutePath();

        // input is a directory
        if (Files.isDirectory(inPath)){
            log.info("Input is directory " + inPath.toString());
            Path outPath = Paths.get(cl.getOptionValue("o")).toAbsolutePath();
            if (Files.exists(outPath)){
                if (!Files.isDirectory(outPath)){
                    log.severe("Outpath already exists and is not a valid directory: " + outPath.toString());
                    System.exit(1);
                }
                log.info("Output directory already exists. Files may be overwritten.");
            } else {
                try {
                    Files.createDirectory(outPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.info("Created output directory " + outPath.toString());
            }

            boolean inDot = false;
            if (cl.hasOption("dotin")) inDot = true;
            boolean outDot = false;
            if (cl.hasOption("dotout")) outDot = true;
            String dot = null;
            if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");

            // semantics have to be provided
            if (cliContainsSemantics(cl)) {
                List<String> semantics = null;
                try {
                    semantics = getSemantics(cl);
                } catch (CliException e) {
                    log.severe("Could not convert: Semantics are invalid: " + e.getMessage());
                    System.exit(1);
                }
                if (semantics.size() == 1) {
                    Wrappers.convertModalMultipleSemanticsTraverseDirectory(
                            inPath, cl.getOptionValue("o"), inDot, outDot, dot, semantics.toArray(new String[semantics.size()]), transformationParameters
                    );
                } else {
                    if (!cl.hasOption("diroutput")) {
                        log.severe("Please specify a directory output structure using -diroutput <structure> if you use more than one semantic for converting a directory. Valid values are: joint,splitted");
                        System.exit(1);
                    }
                    String dirOutput = cl.getOptionValue("diroutput");
                    switch (dirOutput) {
                        case "splitted":
                            Wrappers.convertModalMultipleSemanticsOnMultipleDirectoriesTraverseDirectory(
                                    inPath, cl.getOptionValue("o"), inDot, outDot, dot, semantics.toArray(new String[semantics.size()]), transformationParameters
                            );
                            break;
                        case "joint":
                            Wrappers.convertModalMultipleSemanticsTraverseDirectory(
                                    inPath, cl.getOptionValue("o"), inDot, outDot, dot, semantics.toArray(new String[semantics.size()]), transformationParameters
                            );
                            break;
                        default:
                            log.severe("This is not a valid value for diroutput: " + cl.getOptionValue("diroutput") + ". Valid values are: joint,splitted");
                            System.exit(1);
                    }
                }
            }

            // no semantics will be provided
            else {
                Wrappers.convertModalMultipleSemanticsOnMultipleDirectoriesTraverseDirectory(
                        inPath, cl.getOptionValue("o"), inDot, outDot, dot, null, transformationParameters
                );
            }

        // input is a file
        }else{
            log.info("Input is file " + inPath.toString());
            Path outPath = Paths.get(cl.getOptionValue("o")).toAbsolutePath();
            Path inDot = null;
            if (cl.hasOption("dotin")) inDot = Paths.get(cl.getOptionValue("dotin")).toAbsolutePath();
            Path outDot = null;
            if (cl.hasOption("dotout")) outDot = Paths.get(cl.getOptionValue("dotout")).toAbsolutePath();
            String dot = null;
            if (cl.hasOption("dotbin")) dot = cl.getOptionValue("dotbin");

            // semantics have to be provided
            if (cliContainsSemantics(cl)) {
                List<String> semantics = null;
                try {
                    semantics = getSemantics(cl);
                } catch (CliException e) {
                    log.severe("Could not convert: Semantics are invalid: " + e.getMessage());
                    System.exit(1);
                }
                try {
                    if (semantics.size() == 1) {
                        Wrappers.convertModal(inPath, outPath, inDot, outDot, dot, semantics.get(0), transformationParameters);
                    } else {
                        Wrappers.convertOneFileToModalMultipleSemantics(
                                inPath, outPath, inDot, outDot, dot, semantics.toArray(new String[semantics.size()]), transformationParameters
                        );
                    }
                } catch (TransformationException | exceptions.ParseException | IOException | AnalysisException e) {
                    log.severe("Could not convert: " + e.getMessage());
                    System.exit(1);
                }
                // no semantics will be provided
            } else {
                try {
                    Wrappers.convertModal(inPath, outPath, inDot, outDot, dot, null, transformationParameters);
                } catch (TransformationException | exceptions.ParseException | IOException | AnalysisException e) {
                    log.severe("Could not convert: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
        System.exit(0);
    }

}
