package util.external_software_wrappers;

import util.LexicalOrderComparator;
import util.ProcessKiller;
import util.ThfProblem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MultiTester {

    private static final Logger log = Logger.getLogger( "default" );
    private static final String errorPrefix = "Error_";

    public List<ThfProblem> allProblems;
    private List<String> filterList;

    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit, Path filterFile, Path progress) throws IOException {

        /*
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
        */

        // init white filter list
        filterList = null;
        if (filterFile != null){
            try (Stream<String> lines = Files.lines(filterFile)) {
                filterList = lines.collect(Collectors.toList());
            } catch (IOException e) {
                filterList = null;
                log.warning("Could not load filter file=" + filterFile+toString());
            }
        }

        // Test problems
        allProblems = new ArrayList<>();
        AtomicInteger problems = new AtomicInteger();
        try(Stream<Path> paths = Files.walk(inPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(f -> f.toString().contains(".p") && !f.toString().contains(".ps") && !f.toString().contains(".dot"))
                    .filter(f -> {
                        if (this.filterList == null) return true;
                        return filterList.contains(f.toString());
                    })
                    .forEach(f -> {
                        problems.incrementAndGet();
                        //String name = f.getParent().getFileName().toString() + "/" + f.getFileName().toString();
                        String name = f.getFileName().toString();
                        String info = "Processing " + String.valueOf(problems.get()) + " " + f.toString();
                        System.out.println(info);
                        ThfProblem thfProblem = new ThfProblem(f);
                        thfProblem.name = name;
                        allProblems.add(thfProblem);

                        // Satallax
                        SatallaxWrapper satallax = new SatallaxWrapper();
                        satallax.call(f, timoutPerProblem, timeUnit);
                        thfProblem.satallax = satallax;
                        System.out.println(name + ": satallax: " + satallax.getAbbrevStatus());

                        // Leo 2
                        Leo2Wrapper leo = new Leo2Wrapper();
                        leo.call(f,timoutPerProblem,timeUnit);
                        thfProblem.leo = leo;
                        System.out.println(name + ": leo2: " + leo.getAbbrevStatus());

                        // Nitpick
                        NitpickWrapper nitpick = new NitpickWrapper();
                        nitpick.call(f,timoutPerProblem,timeUnit);
                        thfProblem.nitpick = nitpick;
                        System.out.println(name + ": nitpick: " + nitpick.getAbbrevStatus());

                        info = info + " " + thfProblem.satallax.getAbbrevStatus() + " " +
                                thfProblem.leo.getAbbrevStatus() + " " + thfProblem.nitpick.getAbbrevStatus()+ "\n";
                        // save progress
                        try {
                            Files.write(progress,info.getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            System.err.println("Could not write progress file " + progress.toString());
                            e.printStackTrace();
                        }

                        // kill all atp processes on machine older than 82 seconds
                        ProcessKiller.killAllOlderThan(82,"leo");
                        ProcessKiller.killAllOlderThan(82,"satallax");
                        ProcessKiller.killAllOlderThan(82,"nitpick");
                        ProcessKiller.killAllOlderThan(82,"isabelle");
                    });
        }

        // Write results to file
        // subdirectory/filename,satallax_status,satallax_duration,leo_status,leo_duration,nitpick_status,nitpick_duration
        try {
            Files.write(Paths.get(outPath.toString()),this.allProblems.stream()
                    .sorted((e1,e2)->new LexicalOrderComparator().compare(e1.name,e2.name))
                    .map(p->p.name + "," +
                            p.satallax.getAbbrevStatus() + "," + p.satallax.duration + "," +
                            p.leo.getAbbrevStatus() + "," + p.leo.duration + "," +
                            p.nitpick.getAbbrevStatus() + "," + p.nitpick.getNitpickDuration()
                    )
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write all file");
            e.printStackTrace();
        }

        // here should come the same stuff as in Problem tester satallax
    }


}

