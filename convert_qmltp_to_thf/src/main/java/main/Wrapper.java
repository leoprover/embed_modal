package main;

import exceptions.ParseException;
import fofParser.QmfAstGen;
import javafx.util.Pair;
import org.antlr.v4.runtime.ANTLRInputStream;
import parser.ParseContext;
import util.tree.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Wrapper {

    private static final Logger log = Logger.getLogger( "default" );

    public static void convertQmfTraverseDirectories(Path inPath, String oPath, boolean dotin, boolean dotout, String dotBin ){
        log.info("traversing directories.");
        AtomicInteger totalProblems = new AtomicInteger();
        AtomicInteger parseErrors = new AtomicInteger();
        AtomicInteger otherErrors = new AtomicInteger();
        List<Pair<String,String>> missedProblems = new ArrayList<>(); // other than parse Errors
        List<String> missedProblemsParseError = new ArrayList<>();
        if (Files.isDirectory(inPath)){
            log.info("Input is a directory " + inPath.toString());
            log.info("Creating subdirectories.");
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
                    pathsNew.filter(Files::isRegularFile).forEach(f->{
                        totalProblems.getAndIncrement();
                        log.info("Processing " +totalProblems.get() + ": " + f.toString());
                        String subdir = f.toString().substring(inPath.getParent().toString().length());
                        Path outPath = Paths.get(oPath,subdir);
                        Path inDot = Paths.get(outPath.toString()+".in.dot");
                        Path outDot = Paths.get(outPath.toString()+".out.dot");
                        if (!dotin) inDot = null;
                        if (!dotout) outDot = null;
                        try {
                            boolean success = convertQmfToThf(f,outPath,inDot,outDot,dotBin );
                            if (!success){
                                log.warning("Parse error in problem " + f.toString());
                                missedProblemsParseError.add(f.toString());
                                parseErrors.getAndIncrement();
                            }
                        } catch (ParseException e) {
                            String error = "ParseException: Could not convert \"" + f.toString() + "\" ::: \"" + e.toString() + "\" ::: \"" + e.getMessage();
                            log.warning(error);
                            missedProblems.add(new Pair<>(f.toString(),error));
                            otherErrors.getAndIncrement();
                            //e.printStackTrace();
                            //System.exit(1);
                        } catch (IOException e) {
                            String error = "IOException: Could not convert " + f.toString() + " ::: " + e.toString() + " ::: " + e.getMessage();
                            log.warning(error);
                            missedProblems.add(new Pair<>(f.toString(),error));
                            otherErrors.getAndIncrement();

                        } catch (ConversionException e) {
                            String error = "ConversionException: Could not convert " + f.toString() + " ::: " + e.toString() + " ::: " + e.getMessage();
                            log.warning(error);
                            missedProblems.add(new Pair<>(f.toString(),error));
                            otherErrors.getAndIncrement();
                        }
                    });
                    System.out.println();
                    // write errors to file
                    try {
                        Files.write(Paths.get(oPath,"OtherErrors"),missedProblems.stream()
                                .map(p->p.getKey() + " ::: " + p.getValue())
                                .collect(Collectors.joining("\n")).getBytes());
                    } catch (IOException e) {
                        System.err.println("Could not write OtherErrors file");
                        e.printStackTrace();
                    }
                    try {
                        Files.write(Paths.get(oPath,"ParseErrors"),missedProblemsParseError.stream()
                                .collect(Collectors.joining("\n")).getBytes());
                    } catch (IOException e) {
                        System.err.println("Could not write ParseErrors file");
                        e.printStackTrace();
                    }
                    System.out.println("Problems total:" + totalProblems.get() + " parseErrors:" + parseErrors.get() + " otherErrors:" + otherErrors.get());
                    System.exit(0);
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

    public static boolean convertQmfToThf(Path inPath, Path oPath, Path dotin, Path dotout, String dotBin) throws IOException, ParseException, ConversionException {
        if (!Files.isRegularFile(inPath)){
            log.info("Not a regular file:" + inPath.toString());
            return false;
        }

        String problem = null;
        try {
            problem = new String(Files.readAllBytes(inPath));
        } catch (IOException e) {
            throw new IOException("Could not read file " + inPath + " ::: " + e.getMessage());
        }

        ANTLRInputStream inputStream = new ANTLRInputStream(problem);
        parser.ParseContext parseContext = QmfAstGen.parse( inputStream,"tPTP_file",inPath.toString());
        Node root = parseContext.getRoot();

        // create input dot
        if (dotin != null){
            String dotInContent = root.toDot();
            Files.write(dotin, dotInContent.getBytes());
            if (dotBin != null){
                String cmd = dotBin + " -Tps " + dotin + " -o " + dotin + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }

        // check for parse error
        if ( parseContext.hasParseError()) return false;

        // convert
        Converter c = new Converter(root,inPath.toString());
        ConvertContext context = c.convert();
        //System.out.println(context.getNewProblem());

        // create output dot
        if (dotout != null){
            String dotOutContent = context.converted.toDot();
            Files.write(dotout, dotOutContent.getBytes());
            if (dotBin != null){
                String cmd = dotBin + " -Tps " + dotout + " -o " + dotout + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }


        // output
        String newProblem = context.getNewProblem();
        //System.out.println(newProblem);
        Files.write(oPath,newProblem.getBytes());

        return true;
    }
}
