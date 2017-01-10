package util.external_software_wrappers;

import util.LexicalOrderComparator;
import util.QmlProblem;
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


public class NativeMultiTester {

    private static final Logger log = Logger.getLogger( "default" );
    private static final String errorPrefix = "Error_";

    public List<QmlProblem> allProblems;
    private List<String> filterList;

    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit, Path filterFile, Path progress,
                                     String axiom, String domains) throws IOException {

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

        // Test problems for concrete semantic setting
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
                        QmlProblem qmlProblem = new QmlProblem(f);
                        qmlProblem.name = name;
                        allProblems.add(qmlProblem);

                        // Leo 2
                        MLeanCopWrapper mleancop = new MLeanCopWrapper();
                        qmlProblem.mleancop = mleancop;
                        mleancop.call(f,timoutPerProblem,timeUnit,axiom, domains);
                        System.out.println(name + ": mleancop: " + mleancop.getAbbrevStatus());

                        // save progress
                        try {
                            Files.write(progress,info.getBytes());
                        } catch (IOException e) {
                            System.err.println("Could not write progress file " + progress.toString());
                            e.printStackTrace();
                        }
                    });
        }

        // Write results to file
        // subdirectory/filename,satallax_status,satallax_duration,leo_status,leo_duration,nitpick_status,nitpick_duration
        try {
            Files.write(Paths.get(outPath.toString(),"all"),this.allProblems.stream()
                    .sorted((e1,e2)->new LexicalOrderComparator().compare(e1.name,e2.name))
                    .map(p->p.name + "," +
                            p.mleancop.getAbbrevStatus() + "," + p.mleancop.duration
                    )
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write all file");
            e.printStackTrace();
        }

        // here should come the same stuff as in Problem tester satallax
    }


}

