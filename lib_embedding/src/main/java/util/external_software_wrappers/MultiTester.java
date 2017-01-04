package util.external_software_wrappers;

import util.ThfProblem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MultiTester {

    private static final Logger log = Logger.getLogger( "default" );
    private static final String errorPrefix = "Error_";

    public List<ThfProblem> all;
    private List<String> filterList;

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
                        System.out.println("Processing " + String.valueOf(problems.get()) + " " + f.toString());
                        ThfProblem thfProblem = new ThfProblem(f);

                        // Satallax
                        SatallaxWrapper satallax = new SatallaxWrapper();
                        satallax.call(f, timoutPerProblem, timeUnit);
                        thfProblem.satallax = satallax;

                        // Leo 2
                        Leo2Wrapper leo = new Leo2Wrapper();
                        leo.call(f,timoutPerProblem,timeUnit);
                        thfProblem.leo = leo;

                        // Nitpick
                        NitpickWrapper nitpick = new NitpickWrapper();
                        nitpick.call(f,timoutPerProblem,timeUnit);
                        thfProblem.nitpick = nitpick;
                    });
        }

    }


}

