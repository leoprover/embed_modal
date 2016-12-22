package transformation;


import exceptions.AnalysisException;
import exceptions.TransformationException;
import org.antlr.v4.runtime.ANTLRInputStream;
import parser.ParseContext;
import parser.ThfAstGen;
import util.tree.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Wrappers {

    private static final Logger log = Logger.getLogger( "default" );

    /*
     * Converts a directory and its sub*directories to modal logical thf problems.
     * Output is mapped to a similar subdirectory structure
     * @param inPath input directory containing problems/subdirectories
     * @param oPath output directory
     */
    public static void convertModalMultipleSemanticsTraverseDirectory(Path inPath, String oPath, boolean dotin, boolean dotout, String dotBin, String[] semantics){
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
                    pathsNew.filter(Files::isRegularFile).filter(f->f.toString().endsWith(".p")).forEach(f->{
                        log.info("processing " + f.toString());
                        String subdir = f.toString().substring(inPath.getParent().toString().length());
                        Path outPath = Paths.get(oPath,subdir);
                        Path inDot = Paths.get(outPath.toString()+".in.dot");
                        Path outDot = Paths.get(outPath.toString()+".out.dot");
                        if (!dotin) inDot = null;
                        if (!dotout) outDot = null;
                        try {
                            boolean success = convertModalMultipleSemantics(f,outPath,inDot,outDot,dotBin,semantics);
                            if (!success) log.warning("ParseError: Could not convert " + f.toString());
                        } catch (Exception e) {
                            log.warning("Could not convert " + f.toString() + " ::: " + e.toString() + " ::: " + e.getMessage());
                            //e.printStackTrace();
                            //System.exit(1);
                        }
                    });
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

    /*
     * Wrapper for convertModel which creates multiple problems from one problem and an array containing semantics
     * The problem must not include semantics since multiple semantics declaration results in undefined behavior
     */
    public static boolean convertModalMultipleSemantics(Path inPath, Path outPath, Path inDot, Path outDot, String dotBin, String[] semantics) throws IOException, exceptions.ParseException, AnalysisException, TransformationException {
        if (semantics == null) return convertModal(inPath,outPath,inDot,outDot,dotBin,null);
        else{
            if (semantics.length == 1){
                return convertModal(inPath,outPath,inDot,outDot,dotBin,semantics[0]);
            }
            boolean success = false;
            for (int i = 0; i < semantics.length ; i++){
                Path t_outPath = Paths.get(outPath.toString() + "." + i);
                Path t_inDot = null;
                Path t_outDot = null;
                if (inDot != null) t_inDot = Paths.get(inDot.toString() + "." + i);
                if (outDot != null) t_outDot = Paths.get(outDot.toString() + "." + i);
                //System.out.println(t_inDot);
                //System.out.println(t_outDot);
                success |= convertModal(inPath,t_outPath,t_inDot,t_outDot,dotBin,semantics[i]);
            }
            return success;
        }
    }

    /*
     * converts one problem
     * inDot outDot dotBin can be null
     * semantics can be null ( semantics is already in the problem file )
     */
    public static boolean convertModal(Path inPath, Path outPath, Path inDot, Path outDot, String dotBin, String semantics) throws IOException, exceptions.ParseException, AnalysisException, TransformationException {
        log.info("Processing " + inPath.toString());
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

        // add optional semantics
        if (semantics == null) semantics = "";
        problem = semantics + "\n\n" + problem;

        // parse input
        String rule = "tPTP_file";
        ANTLRInputStream inputStream = new ANTLRInputStream(problem);
        ParseContext parseContext = ThfAstGen.parse(inputStream, rule, inPath.getFileName().toString());
        Node root = parseContext.getRoot();

        // create input dot
        if (inDot != null){
            String dotIn = root.toDot();
            Files.write(inDot, dotIn.getBytes());
            if (dotBin != null){
                String cmd = dotBin + " -Tps " + inDot + " -o " + inDot + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }

        // check for parse error
        if (parseContext.hasParseError()){
            log.warning("Parse Error " + parseContext.getParseError() + " in file " + inPath.toString());
            return false;
        }

        // embed
        TransformContext transformContext = null;
        ModalTransformator transformator = new ModalTransformator(root);
        transformContext = transformator.transform();
        //System.out.println(EmbeddingDefinitions.getAllDefinitions());
        //System.out.println(transformContext.getProblem());

        // create output dot
        if (outDot != null){
            String dotIn = transformContext.transformedRoot.toDot();
            Files.write(outDot, dotIn.getBytes());
            if (dotBin != null){
                String cmd = dotBin + " -Tps " + outDot + " -o " + outDot + ".ps";
                Runtime.getRuntime().exec(cmd);
            }
        }

        // output
        String newProblem = transformContext.getProblemIncludingOld();
        //System.out.println(newProblem);
        Files.write(outPath,newProblem.getBytes());

        return true;
    }
}
