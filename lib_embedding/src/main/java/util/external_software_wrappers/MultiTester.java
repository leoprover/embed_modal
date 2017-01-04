package util.external_software_wrappers;

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


public class MultiTester {

    private static final Logger log = Logger.getLogger( "default" );
    private static final String errorPrefix = "Error_";

    public List<ThfProblem> allProblems;
    private List<String> filterList;

    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit, Path filterFile) throws IOException {

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
                        System.out.println("Processing " + String.valueOf(problems.get()) + " " + f.toString());
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
                    });
        }

        // Write results to file
        // subdirectory/filename,satallax_status,satallax_duration,leo_status,leo_duration,nitpick_status,nitpick_duration
        try {
            Files.write(Paths.get(outPath.toString(),"all"),this.allProblems.stream()
                    .map(p->p.name + "," +
                            p.satallax.getAbbrevStatus() + "," + p.satallax.duration + "," +
                            p.leo.getAbbrevStatus() + "," + p.leo.duration + "," +
                            p.nitpick.getAbbrevStatus() + "," + p.nitpick.duration
                    )
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write all file");
            e.printStackTrace();
        }

        // here should come the same stuff as in Problem tester satallax
    }


}

