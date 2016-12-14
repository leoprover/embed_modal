package main;

import exceptions.ParseException;
import parser.ParseContext;
import parser.ThfAstGen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * Takes the tptp Problems directory and filters for thf formulas from meta data from the tptp website.
 * Parses thf problems and measures time, afterwards writes results into csv file
 */
public class ThfTest {

    public static void main(String[] args) {

        if (args.length != 3){
            System.err.println("Three arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/path/to/result\n" +
                    "/path/to/problemmeta");
            System.exit(1);
        }

        String directoryPath = args[0]; //"/home/tg/university/bachelor_thesis/TPTP-v6.4.0/Problems";
        String resultPath = args[1];
        String problemMetaPath = args[2];

        if (!Files.isDirectory(Paths.get(directoryPath))){
            System.err.println("Two arguments needed: \n" +
                    "/path/to/tptp/Problems/directory\n" +
                    "/pat/to/result/csv");
            System.err.println(directoryPath + " is not a valid directory");
        }

        try { // open meta file try block
            List<TptpProblem> thfProblems = new ArrayList<>();
            List<String> problemMeta = Files.readAllLines(Paths.get(problemMetaPath));
            int i = 0;
            int missedProblems = 0;
            int successProblems = 0;
            int totalProblems = problemMeta.size();
            // preload all meta information and the problem itself
            for (String line : problemMeta){
                try {
                    TptpProblem tptpProblem = new TptpProblem();
                    String name = line.substring(0, line.indexOf(' '));
                    String prefix = name.substring(0, 3);
                    String filename = directoryPath + "/" + prefix + "/" + name + ".p";
                    String problem = new String(Files.readAllBytes(Paths.get(filename)));
                    tptpProblem.name = name;
                    tptpProblem.prefix = prefix;
                    tptpProblem.filename = filename;
                    tptpProblem.problem = problem;
                    tptpProblem.meta = line.split("[ ]+");
                    thfProblems.add(tptpProblem);
                    successProblems += 1;
                }
                catch (IOException e) {
                    missedProblems += 1;
                }
            }
            System.out.println( "Successfully opened " + successProblems + " of " + totalProblems + " with " + missedProblems + " missed problems");

            int parseErrors = 0;
            int parseSuccess = 0;
            List<String> parseErrorList = new ArrayList<>();
            // parse and create asts for all problems, measure time
            for (TptpProblem current : thfProblems) {
                try {
                    Instant pStart = Instant.now();
                    ParseContext parseContext = ThfAstGen.parse(current.problem, "tPTP_file", current.name);
                    current.parseError = parseContext.getParseError();
                    current.root = parseContext.getRoot();
                    Instant pEnd = Instant.now();
                    current.parseTime = Duration.between(pStart,pEnd);
                    current.parsed = true;
                    if (current.parseError != null){
                        throw new ParseException(current.parseError);
                    }
                    parseSuccess += 1;
                    System.out.println(parseSuccess + " of " + totalProblems + " name:" + current.name + " parsetime:" + current.getParseTimeInSeconds());
                } catch (ParseException e) {
                    parseErrors += 1;
                    if (current.parseError == null) current.parseError = e.getMessage();
                    parseErrorList.add(current.name + " " + parseErrors + "error: " + e.getMessage());
                    System.out.println("ERROR " + current.name + " " + parseErrors + "error: " + e.getMessage());
                    //e.printStackTrace();
                }
                //if (parseSuccess == 4) break; // for debugging
                if (parseSuccess % 50 == 0) // give garbage collector a hint to collect every 50 problems and pause test meanwhile
                    try {
                        System.gc();
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
            }
            System.out.println("Successfully parsed " + (totalProblems - parseErrors) + " of " + totalProblems);

            // create general results
            TptpTestResult generalResult = new TptpTestResult(thfProblems);

            // save individual results as csv
            List<String> resultList = new ArrayList<>();
            for (TptpProblem current : thfProblems) {
                resultList.add(current.getMetaWithTimeCsv());
            }
            String resultCsv = String.join("\n", resultList);
            Files.write(Paths.get(resultPath + "_individual_results.csv"),resultCsv.getBytes());

            // export average parsingTime dependending on the input size to python script creating a plotly plot
            // input size may be Rtng Forms Type Units Atoms EqAts VarAts Symbls Vars ^ ! ?
            String script1 = generalResult.getAverageTimePerMetaIndexPythonScript();
            Files.write(Paths.get(resultPath + "_mean_time_per_meta_index_plot_script.py"),script1.getBytes());

            // export non-average parsingTime dependending on the input size to python script creating a plotly plot
            // input size may be Rtng Forms Type Units Atoms EqAts VarAts Symbls Vars ^ ! ?
            String script2 = generalResult.getTimePerMetaIndexPythonScript();
            Files.write(Paths.get(resultPath + "_time_per_meta_index_plot_script.py"),script2.getBytes());

            // export parsingTime depending on the problem category to latex tabular
            String categoryLatex = generalResult.getAverageTimePerCategoryLatexCode();
            Files.write(Paths.get(resultPath + "_mean_time_per_category.txt"), categoryLatex.getBytes());

            // save average and median parsing time as plaintext
            String meanParsingTime = String.valueOf(generalResult.getMeanParsingTime());
            String medianParsingTime = String.valueOf(generalResult.getMedianParsingTime());
            Files.write(Paths.get(resultPath + "_mean_time.txt"), meanParsingTime.getBytes());
            Files.write(Paths.get(resultPath + "_median_time.txt"), medianParsingTime.getBytes());

            // save errors
            String errorFileContent = String.join("\n", parseErrorList);
            Files.write(Paths.get(resultPath + "_errors.txt"), errorFileContent.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
