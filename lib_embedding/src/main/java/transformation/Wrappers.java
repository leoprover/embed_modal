package transformation;

import exceptions.AnalysisException;
import exceptions.ParseException;
import exceptions.TransformationException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import parser.ParseContext;
import parser.ThfAstGen;
import util.Pair;
import util.tree.Node;
import transformation.ModalTransformator.TransformationParameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wrappers {

    private static final Logger log = Logger.getLogger( "default" );

    /*
     * Converts a directory and its sub*directories to modal logical thf problems.
     * Output is mapped to a similar subdirectory structure
     * @param inPath input directory containing problems/subdirectories
     * @param oPath output directory
     * One directory structures for every single semantics
     */
    public static void convertModalMultipleSemanticsOnMultipleDirectoriesTraverseDirectory(Path inPath, String oPath, boolean dotin, boolean dotout, String dotBin, String[] semantics, TransformationParameter... params){
        if (Files.isDirectory(inPath)){
            log.info("Input is a directory " + inPath.toString());
            AtomicInteger problems = new AtomicInteger();
            AtomicInteger problemsParseErrors = new AtomicInteger();
            AtomicInteger problemsOtherErrors = new AtomicInteger();
            List<String> parseErrors = new ArrayList<>();
            List<Pair<String,String>> otherErrors = new ArrayList<>();

            if (semantics != null) {
                log.fine("Semantics: " + semantics.length);
                for (String sem : semantics) {
                    log.finer("Semantics in list: " + sem);
                }
            } else {
                semantics = new String[1];
                semantics[0] = "";
            }
            log.info("Embedding.");
            for (String sem : semantics) {
                log.info("Current semantics: " + sem);
                Path out_s = Paths.get(oPath,SemanticsGenerator.thfName(sem));
                try (Stream<Path> paths = Files.walk(inPath)) {

                    // create subdirectories
                    log.info("Creating subdirectories.");
                    paths.filter(Files::isDirectory).forEach(d -> {
                        Path newDir = Paths.get(
                                out_s.toString(),
                                d.toAbsolutePath().toString().replace(inPath.toAbsolutePath().getParent().toString(), ""));
                        try {
                            Files.createDirectories(newDir);
                            log.info("Created directory " + newDir.toString());
                        } catch (IOException e) {
                            log.severe("Could not create directory " + newDir.toString() + " ::: " + e.getMessage());
                        }
                    });

                    // embed problems
                    try (Stream<Path> pathsNew = Files.walk(inPath)) {
                        log.info("Converting problems.");
                        pathsNew.filter(Files::isRegularFile).filter(f -> f.toString().endsWith(".p")).forEach(f -> {
                            problems.getAndIncrement();
                            log.info("Processing " + String.valueOf(problems.get()) + ": " + f.toString());
                            String subdir = f.toString().substring(inPath.getParent().toString().length());
                            Path outPath = Paths.get(out_s.toString(), subdir);
                            Path inDot = Paths.get(outPath.toString() + "-in.dot");
                            Path outDot = Paths.get(outPath.toString() + "-out.dot");
                            //System.out.println(outPath.toString());
                            if (!dotin) inDot = null;
                            if (!dotout) outDot = null;


                            // call embedding wrapper
                            try {
                                boolean success = convertModal(f, outPath, inDot, outDot, dotBin, sem, params);
                                if (!success) {
                                    log.warning("ParseError: Could not convert " + f.toString());
                                    parseErrors.add(f.toString());
                                    problemsParseErrors.getAndIncrement();
                                }
                            } catch (IOException | ParseException | AnalysisException | TransformationException e) {
                                String error = "Could not convert " + f.toString() + " ::: " + e.toString() + " ::: " + e.getMessage();
                                log.warning(error);
                                otherErrors.add(new Pair<String, String>(f.toString(), error));
                                problemsOtherErrors.getAndIncrement();
                                //e.printStackTrace();
                            }
                        });

                        // write errors to file
                        try {
                            Files.write(Paths.get(out_s.toString(), "OtherErrors"), otherErrors.stream()
                                    .map(p -> p.getKey() + " ::: " + p.getValue())
                                    .collect(Collectors.joining("\n")).getBytes());
                        } catch (IOException e) {
                            log.warning("Could not write OtherErrors file");
                            e.printStackTrace();
                        }
                        try {
                            Files.write(Paths.get(out_s.toString(), "ParseErrors"), parseErrors.stream()
                                    .collect(Collectors.joining("\n")).getBytes());
                        } catch (IOException e) {
                            log.warning("Could not write ParseErrors file");
                            e.printStackTrace();
                        }
                        log.info("Problems total:" + problems.get() + " parseErrors:" + problemsParseErrors.get() + " otherErrors:" + problemsOtherErrors.get());

                    } catch (IOException e) {
                        log.severe("Could not traverse directory " + inPath.toString() + " ::: " + e.getMessage());
                        log.severe("Exit.");
                        System.exit(1);
                    }
                } catch (IOException e) {
                    log.severe("Could not traverse directory " + inPath.toString() + " ::: " + e.getMessage());
                    log.severe("Exit.");
                    System.exit(1);
                }
            }
        }
    }

    /*
     * Converts a directory and its sub*directories to modal logical thf problems.
     * Output is mapped to a similar subdirectory structure
     * @param inPath input directory containing problems/subdirectories
     * @param oPath output directory
     * One directory structure for all semantics
     */
    public static void convertModalMultipleSemanticsTraverseDirectory(Path inPath, String oPath, boolean dotin, boolean dotout, String dotBin, String[] semantics, TransformationParameter... params) {
        if (Files.isDirectory(inPath)){
            log.info("Input is a directory " + inPath.toString());
            log.info("Creating subdirectories.");
            AtomicInteger problems = new AtomicInteger();
            AtomicInteger problemsParseErrors = new AtomicInteger();
            AtomicInteger problemsOtherErrors = new AtomicInteger();
            List<String> parseErrors = new ArrayList<>();
            List<Pair<String,String>> otherErrors = new ArrayList<>();
            try(Stream<Path> paths = Files.walk(inPath)){

                // create subdirectories
                paths.filter(Files::isDirectory).forEach(d -> {
                    Path newDir = Paths.get(
                            oPath,
                            d.toAbsolutePath().toString().replace(inPath.toAbsolutePath().getParent().toString(), ""));
                    try {
                        Files.createDirectories(newDir);
                        log.info("Created directory " + newDir.toString());
                    } catch (IOException e) {
                        log.warning("Could not create directory " + newDir.toString() + " ::: " + e.getMessage());
                    }
                });

                // embed problems
                try(Stream<Path> pathsNew = Files.walk(inPath)){
                    log.info("Converting problems.");
                    pathsNew.filter(Files::isRegularFile).filter(f->f.toString().endsWith(".p")).forEach(f->{
                        problems.getAndIncrement();
                        log.info("Processing " + String.valueOf(problems.get()) + ": " + f.toString());
                        String subdir = f.toString().substring(inPath.getParent().toString().length());
                        Path outPath = Paths.get(oPath,subdir);
                        Path inDot = Paths.get(outPath.toString());
                        Path outDot = Paths.get(outPath.toString());
                        if (!dotin) inDot = null;
                        if (!dotout) outDot = null;

                        // call to embedding wrapper
                        try {
                            boolean success = convertOneFileToModalMultipleSemantics(f,outPath,inDot,outDot,dotBin,semantics, params);
                            if (!success){
                                log.warning("ParseError: Could not convert " + f.toString());
                                parseErrors.add(f.toString());
                                problemsParseErrors.getAndIncrement();
                            }
                        } catch (TransformationException | ParseException | IOException | AnalysisException e) {
                            String error = "Could not convert " + f.toString() + " ::: " + e.toString() + " ::: " + e.getMessage();
                            log.warning(error);
                            otherErrors.add(new Pair<>(f.toString(), error));
                            problemsOtherErrors.getAndIncrement();
                        }
                    });

                    // write errors to file
                    try {
                        Files.write(Paths.get(oPath,"OtherErrors"),otherErrors.stream()
                                .map(p->p.getKey() + " ::: " + p.getValue())
                                .collect(Collectors.joining("\n")).getBytes());
                    } catch (IOException e) {
                        System.err.println("Could not write OtherErrors file");
                        e.printStackTrace();
                    }
                    try {
                        Files.write(Paths.get(oPath,"ParseErrors"),parseErrors.stream()
                                .collect(Collectors.joining("\n")).getBytes());
                    } catch (IOException e) {
                        System.err.println("Could not write ParseErrors file");
                        e.printStackTrace();
                    }
                    log.info("Problems total:" + problems.get() + " parseErrors:" + problemsParseErrors.get() + " otherErrors:" + problemsOtherErrors.get());

                } catch (IOException e){
                    log.severe("Could not traverse directory " + inPath.toString() + " ::: " + e.getMessage());
                    log.severe("Exit.");
                    System.exit(1);
                }
            } catch (IOException e){
                log.severe("Could not traverse directory " + inPath.toString() + " ::: " + e.getMessage());
                log.severe("Exit.");
                System.exit(1);
            }

        }
    }

    /*
     * Wrapper for convertModel which creates multiple problems from one problem and an array containing semantics
     * The problem must not include semantics since multiple semantics declaration results in undefined behavior
     * experimental output
     */
    public static boolean convertOneFileToModalMultipleSemantics(Path inPath, Path outPath, Path inDot, Path outDot, String dotBin, String[] semantics, TransformationParameter... params) throws IOException, ParseException, AnalysisException, TransformationException {
        if (semantics == null) return convertModal(inPath,outPath,inDot,outDot,dotBin,null, params);
        else{
            boolean success = true;
            int semNameNumber = 0;
            Set<String> semNames = new HashSet<>();
            for (String sem : semantics){

                // ensure every semantics name is unique
                semNameNumber++;
                String semName = SemanticsGenerator.thfName(sem);
                if (semName.equals("") || semNames.contains(semName)) semName += semNameNumber;
                semNames.add(semName);

                log.info("Converting with semantics " + semName + " file " + inPath.toString());
                String outproblem = outPath.toString();
                if (outproblem.endsWith(".p")) outproblem = outproblem.substring(0,outproblem.lastIndexOf(".p"));

                // only one semantic - leave out semantics name
                Path t_outPath = Paths.get(outproblem + ".p");
                Path t_inDot = null;
                Path t_outDot = null;
                if (inDot != null) t_inDot = Paths.get(outproblem + ".in.dot");
                if (outDot != null) t_outDot = Paths.get(outproblem + ".out.dot");

                // multiple semantics - add semantics name
                if (semantics.length != 1) {
                    t_outPath = Paths.get(outproblem + "-" + semName + ".p");
                    t_inDot = null;
                    t_outDot = null;
                    if (inDot != null) t_inDot = Paths.get(outproblem + "-" + semName + ".in.dot");
                    if (outDot != null) t_outDot = Paths.get(outproblem + "-" + semName + ".out.dot");
                }
                success &= convertModal(inPath,t_outPath,t_inDot,t_outDot,dotBin,sem, params);
            }
            return success;
        }
    }

    public static String convertModalToString(Path inPath, TransformationParameter... params) throws IOException, ParseException, AnalysisException, TransformationException {
        return convertModalToString(inPath, null, null, null, null, params);
    }

    public static String convertModalToString(Path inPath, Path inDot,
                                              Path outDot, String dotBin,
                                              String semantics,
                                              TransformationParameter... params)
            throws IOException, ParseException, AnalysisException, TransformationException {
        // read file
        if (!Files.isRegularFile(inPath)){
            throw new IOException("Could not read file " + inPath + " ::: " + "Not a regular file or does not exist");
        }
        String problem = null;
        try {
            problem = new String(Files.readAllBytes(inPath));
        } catch (IOException e) {
            throw new IOException("Could not read file " + inPath + " ::: " + e.getMessage());
        }
        if (semantics != null && problem.contains("$modal")) log.warning("Problem may already contain semantical definitions. These may be partly overwritten by the semantics additionally specified.");

        // add optional semantics
        if (semantics != null) problem = problem + "\n\n" + semantics;

        // parse input
        String rule = "tPTP_file";
        CodePointCharStream inputStream = CharStreams.fromString(problem);
        ParseContext parseContext = ThfAstGen.parse(inputStream, rule, inPath.getFileName().toString());
        Node root = parseContext.getRoot();

        // create input dot
        if (inDot != null){
            log.info("Creating input dot file " + inDot.toString());
            String dotIn = root.toDot();
            Files.write(inDot, dotIn.getBytes());
            if (dotBin != null){
                log.info("Creating input ps file " + inDot.toString() + ".ps");
                String cmd = dotBin + " -Tps " + inDot + " -o " + inDot + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }

        // check for parse error
        if (parseContext.hasParseError()){
            throw new ParseException("Parse Error " + parseContext.getParseError() + " in file " + inPath.toString());
        }

        // pack parameters in immutable set for easier handling
        Set<TransformationParameter> paramSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(params)));
        if ((paramSet.contains(TransformationParameter.SEMANTICAL) && paramSet.contains(TransformationParameter.SYNTACTICAL))) {
            throw new TransformationException("Cannot use SEMANTICAL and SYNTACTICAL parameters at the same time.");
        }
        if (params.length == 0) log.info("No transformation parameters in use, using default embedding.");
        else log.info("Transformation parameters: " + String.join(",", Arrays.stream(params).map(Enum::name).collect(Collectors.toList())));

        // embed
        TransformContext transformContext = null;
        ModalTransformator transformator = new ModalTransformator(root);
        transformContext = transformator.transform(paramSet);
        log.info("Transformed problem.");

        // create output dot
        if (outDot != null){
            log.info("Creating output dot file " + outDot.toString());
            String dotIn = transformContext.transformedRoot.toDot();
            Files.write(outDot, dotIn.getBytes());
            if (dotBin != null){
                log.info("Creating output ps file " + outDot.toString() + ".ps");
                String cmd = dotBin + " -Tps " + outDot + " -o " + outDot + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }

        // output
        return transformContext.getProblemIncludingOld();
    }

    /*
     * converts one problem
     * inDot outDot dotBin can be null
     * semantics can be null ( semantics is already in the problem file )
     */
    public static boolean convertModal(Path inPath, Path outPath, Path inDot, Path outDot, String dotBin, String semantics, TransformationParameter... params) throws IOException, ParseException, AnalysisException, TransformationException {
        String newProblem = convertModalToString(inPath, inDot, outDot, dotBin, semantics, params);
        Files.write(outPath,newProblem.getBytes());
        log.info("Transformed problem was written to " + outPath.toString());
        return true;
    }
}
