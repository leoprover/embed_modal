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
    private List<String> filterList;
    public ProblemTesterSatallax(){
        this.all = new ArrayList<>();
    }
    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit, Path filterFile) throws IOException {

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

        filterList = null;
        if (filterFile != null){
            try (Stream<String> lines = Files.lines(filterFile)) {
                filterList = lines.collect(Collectors.toList());
            } catch (IOException e) {
                filterList = null;
                log.warning("Could not load filter file=" + filterFile+toString());
            }
        }

        // convert send all problems to satallax
        AtomicInteger problems = new AtomicInteger();
        try(Stream<Path> paths = Files.walk(inPath)){
            paths.filter(Files::isRegularFile)
                    .filter(f->f.toString().contains(".p") && !f.toString().contains(".ps") && !f.toString().contains(".dot"))
                    .filter(f->{
                        if (this.filterList == null) return true;
                        return filterList.contains(f.toString());
                    })
                    .forEach(f->{
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
            // save all files of unknown status to one file
            try {
                Files.write(Paths.get(outPath.toString(),"UnknownStatus"),this.all.stream()
                        .filter(p->p.s.hasUnknownStatus())
                        .map(p->p.s.status + "," + p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write UnknownStatus file");
                e.printStackTrace();
            }
            // save all timeouts when testing for theorem
            try {
                Files.write(Paths.get(outPath.toString(),"TimeoutTheorem"),this.all.stream()
                        .filter(p->p.s.status.contains("Timeout"))
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write UnknownStatus file");
                e.printStackTrace();
            }
            // save all timeouts when testing for SAT
            try {
                Files.write(Paths.get(outPath.toString(),"TimeoutSAT"),this.all.stream()
                        .filter(p->p.s.sat.contains("Timeout"))
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write UnknownStatus file");
                e.printStackTrace();
            }
            // save all files with status error to one file
            try {
                Files.write(Paths.get(outPath.toString(),"Error"),this.all.stream()
                        .filter(p->p.s.hasError())
                        .map(p->p.path.toString())
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Failed file");
                e.printStackTrace();
            }
            // save all files with status error to one file and keep stdout
            try {
                Files.write(Paths.get(outPath.toString(),"ErrorReason"),this.all.stream()
                        .filter(p->p.s.hasError())
                        .map(p->p.path.toString() + " ::: " + p.s.allout.replaceAll("\\t","").replaceAll("\\n"," :: "))
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write Failed file");
                e.printStackTrace();
            }
            // save status all files
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
                           Files.write(Paths.get(outPath.toString(), errorPrefix + p.path.getFileName().toString()), p.s.allout.getBytes());
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   });

        }
    }

}
