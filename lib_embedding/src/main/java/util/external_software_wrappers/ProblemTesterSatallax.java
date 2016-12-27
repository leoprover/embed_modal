package util.external_software_wrappers;

import exceptions.WrapperException;
import util.ThfProblem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProblemTesterSatallax {

    public List<ThfProblem> all;
    private final String errorPrefix = "Error_";

    private static final Logger log = Logger.getLogger( "default" );

    public ProblemTesterSatallax(){
        this.all = new ArrayList<>();
    }
    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit) throws IOException {

        // remove all old error files
        try(Stream<Path> paths = Files.walk(outPath)){
            paths.filter(f->f.getFileName().toString().contains(errorPrefix)).forEach((path) -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // do nothing
                }
            });
        }

        // convert send all problems to satallax
        AtomicInteger problems = new AtomicInteger();
        try(Stream<Path> paths = Files.walk(inPath)){
            paths.filter(Files::isRegularFile).filter(f->f.toString().contains(".p") && !f.toString().contains(".ps") && !f.toString().contains(".dot")).forEach(f->{
                problems.incrementAndGet();
                System.out.println("Processing " + String.valueOf(problems.get()) + " " + f.toString());
                SatallaxWrapper s = new SatallaxWrapper();
                try {
                    s.call(f,timoutPerProblem,timeUnit);
                    this.all.add(new ThfProblem(f,s));
                } catch (WrapperException e) {
                    //System.err.println("Wrapper Exception");
                    //System.err.println(e.getMessage());
                    //System.err.println(e.getCause());
                    //e.printStackTrace();
                } catch (InterruptedException e) {
                    //System.err.println("InterruptedException");
                    //System.err.println(e.getMessage());
                    //System.err.println(e.getCause());
                    //e.printStackTrace();
                }
            });

            // write results to files
            try {
                Files.write(Paths.get(outPath.toString(),"ParserError"),this.all.stream()
                        .filter(p->p.s.hasParserError())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write ParseError file");
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get(outPath.toString(),"TypeError"),this.all.stream()
                        .filter(p->p.s.hasTypeError())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write TypeError file");
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get(outPath.toString(),"CounterSatisfiable"),this.all.stream()
                        .filter(p->p.s.isCounterSatisfiable())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write CounterSatisfiable file");
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get(outPath.toString(),"Theorem"),this.all.stream()
                        .filter(p->p.s.isTheorem())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Theorem file");
                e.printStackTrace();
            }
            try {
                Files.write(Paths.get(outPath.toString(),"Satisfiable"),this.all.stream()
                        .filter(p->p.s.isSatisfiable())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Satisfiable file");
                e.printStackTrace();
            }
            /*
            try {
                Files.write(Paths.get(outPath.toString(),"NotSatisfiable"),this.all.stream()
                        .filter(p->!p.s.isSatisfiable())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write NotSatisfiable file");
                e.printStackTrace();
            }
            */
            // save all files of unknown to one file
            try {
                Files.write(Paths.get(outPath.toString(),"UnknownStatus"),this.all.stream()
                        .filter(p->p.s.hasUnknownStatus())
                        .map(p->p.s.status + "," + p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write UnknownStatus file");
                e.printStackTrace();
            }
            // save all failed files to one file
            try {
                Files.write(Paths.get(outPath.toString(),"Failed"),this.all.stream()
                        .filter(p->p.s.hasUnknownStatus()||p.s.hasTypeError()||p.s.hasParserError())
                        .map(p->p.s.status + "," + p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Failed file");
                e.printStackTrace();
            }

            try {
                Files.write(Paths.get(outPath.toString(),"Total"),this.all.stream()
                        .map(p->p.s.status + "," + p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Total file");
                e.printStackTrace();
            }
            // save output of failed files separately
            this.all.stream()
                   .filter(p->p.s.hasUnknownStatus()||p.s.hasParserError()||p.s.hasTypeError())
                   .forEach(p->{
                       try {
                           Files.write(Paths.get(outPath.toString(), errorPrefix + p.path.getFileName().toString()),p.s.stdout.getBytes());
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   });

        }
    }

    public static String testProblem(Path inPath, long timoutPerProblem, TimeUnit timeUnit) throws WrapperException, InterruptedException {
        StringBuilder res = new StringBuilder();
        SatallaxWrapper s = new SatallaxWrapper();
        s.call(inPath,timoutPerProblem,timeUnit);
        // csv which contains whether the following properties apply (1 for true, 0 for false)
        // stdout , stderr , parseerror ,
        res.append(s.hasStdout() ? 1 : 0);
        res.append(",");
        res.append(s.hasStderr()  ? 1 : 0);
        res.append(",");
        res.append(s.hasParserError() ? 1 : 0);
        res.append(",");
        res.append(s.hasTypeError() ? 1 : 0);
        res.append(",");
        res.append(s.isTheorem() ? 1 : 0);
        res.append(",");
        res.append(s.isCounterSatisfiable() ? 1 : 0);
        return res.toString();
    }

    /*
    public static String testProblemPrettyPrint(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit) throws WrapperException, InterruptedException, IOException {
        String ret = testProblem(inPath,outPath,timoutPerProblem,timeUnit);
        String[] values = ret.split(",");

    }
    */
}
