package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Results {
    public List<Test> tests;
    public Results(){
        this.tests = new ArrayList<>();
    }
    public void addTest(Test t){
        this.tests.add(t);
    }

    // results
    // concerning qmltp
    public List<Problem> confirmedProblems; // confirm result of qmltp without disagreement of the atps
    public List<Problem> unconfirmedProblems; // qmltp yields a result but atps do not or atps disagree
    public List<Problem> newProblemResult; // qmltp yields unsolved but atps have a result and they agree
    public List<Problem> disagreementQmltpAtp; // if qmltp and atp results are different and all atp yield the same result

    // not concerning qmltp
    public List<Problem> newProblems; // qmltp does not sopport this semantical setting
    public List<Problem> newProblemsSolved; // qmltp does not support this semantical setting and atps solve this problem (agree)

    // both
    public List<Problem> totallyUnsolvedProblems; // neither qmltp nor atps have results in which atps agree
    public List<Problem> disagreementAtpAtp; // if at least two atp systems have different results
    public List<Problem> totalProblems; // all problems


    public void evaluate(String outputPath){

        confirmedProblems = new ArrayList<>(); // y
        unconfirmedProblems = new ArrayList<>(); // y
        newProblemResult = new ArrayList<>(); // y
        newProblems = new ArrayList<>();
        newProblemsSolved = new ArrayList<>(); // n
        disagreementAtpAtp = new ArrayList<>(); // y + n
        disagreementQmltpAtp = new ArrayList<>(); // y
        totallyUnsolvedProblems = new ArrayList<>(); // y + n
        totalProblems = new ArrayList<>();

        for (Test test : this.tests){
            System.out.println("Evaluate: " + test.test_name + " " + test.system + " " + test.domains);
            for (Problem problem : test.getProblems()){
                //System.out.println(problem.toString());

                totalProblems.add(problem);

                // qmltp entry exists
                // this is the case for systems k,d,t,s4,s5 and domains const,cumul,vary and constants rigid and consequence ???
                if (problem.status_qmltp != null){

                    // no solution available in qmltp
                    if (problem.status_qmltp.equals("UNK")){
                        // atps agree on a solution
                        String agreedStatus = getAgreedStatus(problem);
                        if (atpsAgree(problem)){
                            // atps have a solution
                            if (!agreedStatus.equals("UNK")) newProblemResult.add(problem);
                            // atps do not have a solution
                            else totallyUnsolvedProblems.add(problem);
                        }
                        // atps do not agree on a solution
                        else {
                            disagreementAtpAtp.add(problem);
                            totallyUnsolvedProblems.add(problem);
                        }
                    }
                    // solution in available in qmltp
                    else {
                        // atps agree on a solution
                        if (atpsAgree(problem)){
                            String agreedStatus = getAgreedStatus(problem);
                            // atps have not solved problem
                            if (agreedStatus.equals("UNK")){
                                // qmltp has no solution
                                if (problem.status_qmltp.equals("UNK")) totallyUnsolvedProblems.add(problem);
                                // qmltp has solution
                                else unconfirmedProblems.add(problem);
                            }
                            // atps have solved problem
                            else {
                                // there is no solution in qmltp
                                if (problem.status_qmltp.equals("UNK")){
                                    newProblemResult.add(problem);
                                }
                                // there is a solution in qmltp
                                else {
                                    // qmltp solution and atp solution are equal
                                    if (problem.status_qmltp.equals(agreedStatus)) confirmedProblems.add(problem);
                                    // qmltp solution and atp solution disagree
                                    else disagreementQmltpAtp.add(problem);
                                }
                            }

                        }
                        // atps do not agree on a solution
                        else {
                            disagreementAtpAtp.add(problem);
                            unconfirmedProblems.add(problem);
                        }
                    }
                }
                // qmltp entry does not exist
                else {
                    newProblems.add(problem);
                    // atps agree on problem
                    if (atpsAgree(problem)){
                        String agreedStatus = getAgreedStatus(problem);
                        // atps have no solution
                        if (agreedStatus.equals("UNK")) totallyUnsolvedProblems.add(problem);
                        // atps have a solution
                        else newProblemsSolved.add(problem);
                    }
                    // atps do not agree on problem
                    else {
                        totallyUnsolvedProblems.add(problem);
                    }
                }
            }
        }

        System.out.println("");
        System.out.println("==============================================");
        System.out.println("Results:");
        this.outputToFiles(outputPath);
        this.outputToStdout();
    }

    private void outputToStdout(){
        System.out.println("totalProblems:           " + totalProblems.size());
        System.out.println("ConfirmedProblems:       " + confirmedProblems.size());
        System.out.println("unconfirmedProblems:     " + unconfirmedProblems.size());
        System.out.println("newProblemResult:        " + newProblemResult.size());
        System.out.println("disagreementQmltpAtp:    " + disagreementQmltpAtp.size());
        System.out.println("newProblems:             " + newProblems.size());
        System.out.println("newProblemsSolved:       " + newProblemsSolved.size());
        System.out.println("totallyUnsolvedProblems: " + totallyUnsolvedProblems.size());
        System.out.println("disagreementAtpAtp:      " + disagreementAtpAtp.size());
    }

    private void outputToFiles(String outputPath){
        try {
            Files.write(Paths.get(outputPath.toString(),"totalProblems"),this.totalProblems.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write totalProblems file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"confirmedProblems"),this.confirmedProblems.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write confirmedProblems file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"unconfirmedProblems"),this.unconfirmedProblems.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write unconfirmedProblems file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"newProblemResult"),this.newProblemResult.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblemResult file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementQmltpAtp"),this.disagreementQmltpAtp.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementQmltpAtp file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"newProblems"),this.newProblems.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblems file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"newProblemsSolved"),this.newProblemsSolved.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblemsSolved file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"totallyUnsolvedProblems"),this.totallyUnsolvedProblems.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write totallyUnsolvedProblems file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementAtpAtp"),this.disagreementAtpAtp.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementAtpAtp file");
            e.printStackTrace();
        }
    }

    private boolean atpsAgree(Problem p){
        // three agree
        if (p.status_satallax.equals(p.status_leo) && p.status_leo.equals(p.status_nitpick)) return true;
        // one UNK, two agree
        if (p.status_satallax.equals("UNK") && p.status_leo.equals(p.status_nitpick)) return true;
        if (p.status_leo.equals("UNK") && p.status_satallax.equals(p.status_nitpick)) return true;
        if (p.status_nitpick.equals("UNK") && p.status_satallax.equals(p.status_leo)) return true;
        // two UNK (one not UNK)
        if (p.status_satallax.equals("UNK") && p.status_leo.equals("UNK")) return true;
        if (p.status_satallax.equals("UNK") && p.status_nitpick.equals("UNK")) return true;
        if (p.status_nitpick.equals("UNK") && p.status_leo.equals("UNK")) return true;
        // all other cases disagreement
        return false;
    }

    private String getAgreedStatus(Problem p){
        if (p.status_satallax.equals(p.status_leo) && p.status_leo.equals(p.status_nitpick)) return p.status_satallax;
        // one UNK, two agree
        if (p.status_satallax.equals("UNK") && p.status_leo.equals(p.status_nitpick)) return p.status_leo;
        if (p.status_leo.equals("UNK") && p.status_satallax.equals(p.status_nitpick)) return p.status_satallax;
        if (p.status_nitpick.equals("UNK") && p.status_satallax.equals(p.status_leo)) return p.status_satallax;
        // two UNK (one not UNK)
        if (p.status_satallax.equals("UNK") && p.status_leo.equals("UNK")) return p.status_nitpick;
        if (p.status_satallax.equals("UNK") && p.status_nitpick.equals("UNK")) return p.status_leo;
        if (p.status_nitpick.equals("UNK") && p.status_leo.equals("UNK")) return p.status_satallax;
        // disagreement
        return null;
    }

}
