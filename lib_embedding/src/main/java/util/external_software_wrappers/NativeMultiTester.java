package util.external_software_wrappers;

import util.LexicalOrderComparator;
import util.ProcessKiller;
import util.QmlProblem;
import util.ThfProblem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
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

    public void testProblemDirectory(Path inPath, Path outPath, long timoutPerProblem, TimeUnit timeUnit, Path progress,
                                     String axiom, String domains) throws IOException {


        // Test problems for concrete semantic setting
        allProblems = new ArrayList<>();
        AtomicInteger problems = new AtomicInteger();
        try(Stream<Path> paths = Files.walk(inPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(f -> f.toString().contains(".p") && !f.toString().contains(".ps") && !f.toString().contains(".dot"))
                    .forEach(f -> {
                        System.out.println(Instant.now());
                        problems.incrementAndGet();
                        //String name = f.getParent().getFileName().toString() + "/" + f.getFileName().toString();
                        String name = f.getFileName().toString();
                        String info = "Processing " + String.valueOf(problems.get()) + " " + f.toString();
                        System.out.println(info);
                        System.out.println("Semantics: " + axiom + "/"+domains);
                        QmlProblem qmlProblem = new QmlProblem(f);
                        qmlProblem.name = name;
                        allProblems.add(qmlProblem);

                        // MleanCoP
                        MLeanCopWrapper mleancop = new MLeanCopWrapper();
                        mleancop.call(f,timoutPerProblem,timeUnit,axiom, domains);
                        qmlProblem.mleancop = mleancop;
                        System.out.println(name + ": mleancop: " + mleancop.getAbbrevStatus());

                        // save progress
                        info = info + "," + qmlProblem.mleancop.getAbbrevStatus() + "," + qmlProblem.mleancop.duration;
                        try {
                            if (Files.exists(progress)) Files.write(progress,info.getBytes(), StandardOpenOption.APPEND);
                            else Files.write(progress,info.getBytes());
                        } catch (IOException e) {
                            System.err.println("Could not write progress file " + progress.toString());
                            e.printStackTrace();
                        }
                        System.out.println("::: progress saved");
                        // kill all atp processes on machine older than x+1 seconds
                        ProcessKiller.killAllOlderThan((int)timoutPerProblem+1,"mleancop");
                        System.out.println("::: killed all mleancop");
                        ProcessKiller.killAllOlderThan((int)timoutPerProblem+1,"swipl");
                        System.out.println("::: killed all swipl");
                    });
        }

        // Write results to file
        // subdirectory/filename,satallax_status,satallax_duration,leo_status,leo_duration,nitpick_status,nitpick_duration
        try {
            Files.write(Paths.get(outPath.toString()),this.allProblems.stream()
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

