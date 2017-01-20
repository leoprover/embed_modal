package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
    // concerning qmltp and status
    /*
    public List<Problem> confirmedProblems; // confirm result of qmltp without disagreement of the atps
    public List<Problem> unconfirmedProblems; // qmltp yields a result but atps do not or atps disagree
    public List<Problem> newProblemResultTotal; // qmltp yields unsolved but atps have a result and they agree
    public List<Problem> newProblemResultTHM; // qmltp yields unsolved but atps have a result and they agree
    public List<Problem> newProblemResultCSA; // qmltp yields unsolved but atps have a result and they agree
    */
    public List<Problem> disagreementQmltpAtp; // if qmltp and atp results are different and all atp yield the same result
    public List<Problem> disagreementQmltpMleancop; // if qmltp and atp results are different and all atp yield the same result

    public List<Problem> totalProblems; // all problems

    // atp specific
    public List<Problem> errorAtp; // at least one atp system has an error status
    public List<Problem> disagreementAtpAtp; // if at least two atp systems have different results
    public List<Problem> disagreementAtpMleancop; // if at least two atp systems have different results
    public HashMap<String,List<Problem>> satallax; // STATUS -> List of Problems
    public HashMap<String,List<Problem>> leo;
    public HashMap<String,List<Problem>> nitpick;
    public HashMap<String,List<Problem>> mleancop;

    // table
    public List<String> table;

    public void evaluate(String outputPath){

        /*
        confirmedProblems = new ArrayList<>(); // y
        unconfirmedProblems = new ArrayList<>(); // y
        newProblemResultTotal = new ArrayList<>(); // y
        newProblemResultTHM = new ArrayList<>(); // y
        newProblemResultCSA = new ArrayList<>(); // y
        newProblems = new ArrayList<>();
        newProblemsSolved = new ArrayList<>(); // n
        */
        disagreementQmltpAtp = new ArrayList<>(); // y
        disagreementQmltpMleancop = new ArrayList<>();
        totalProblems = new ArrayList<>();

        /*
        rating_1_00_THM = new ArrayList<>();
        rating_1_00_CSA = new ArrayList<>();
        rating_1_00_UNK = new ArrayList<>();
        rating_1_00_Total = new ArrayList<>();
        */

        errorAtp = new ArrayList<>();
        disagreementAtpAtp = new ArrayList<>(); // y + n
        disagreementAtpMleancop = new ArrayList<>();
        satallax = new HashMap<>();
        satallax.put("ERR",new ArrayList<>());
        satallax.put("UNK",new ArrayList<>());
        satallax.put("THM",new ArrayList<>());
        satallax.put("CSA",new ArrayList<>());
        leo = new HashMap<>();
        leo.put("ERR",new ArrayList<>());
        leo.put("UNK",new ArrayList<>());
        leo.put("THM",new ArrayList<>());
        leo.put("CSA",new ArrayList<>());
        nitpick = new HashMap<>();
        nitpick.put("ERR",new ArrayList<>());
        nitpick.put("UNK",new ArrayList<>());
        nitpick.put("THM",new ArrayList<>());
        nitpick.put("CSA",new ArrayList<>());
        mleancop = new HashMap<>();
        mleancop.put("ERR",new ArrayList<>());
        mleancop.put("UNK",new ArrayList<>());
        mleancop.put("THM",new ArrayList<>());
        mleancop.put("CSA",new ArrayList<>());

        table = new ArrayList<>();
        table.add("Semantics satSUM satTHM satCSA leoSUM leoTHM leoCSA nitSUM nitTHM nitCSA");

        for (Test test : this.tests){
            System.out.println("Evaluate: " + test.test_name + " " + test.system + " " + test.domains);

            HashMap<String,List<Problem>> csatallax = new HashMap<>();
            csatallax.put("ERR",new ArrayList<>());
            csatallax.put("UNK",new ArrayList<>());
            csatallax.put("THM",new ArrayList<>());
            csatallax.put("CSA",new ArrayList<>());
            HashMap<String,List<Problem>> cleo = new HashMap<>();
            cleo.put("ERR",new ArrayList<>());
            cleo.put("UNK",new ArrayList<>());
            cleo.put("THM",new ArrayList<>());
            cleo.put("CSA",new ArrayList<>());
            HashMap<String,List<Problem>> cnitpick = new HashMap<>();
            cnitpick.put("ERR",new ArrayList<>());
            cnitpick.put("UNK",new ArrayList<>());
            cnitpick.put("THM",new ArrayList<>());
            cnitpick.put("CSA",new ArrayList<>());
            HashMap<String,List<Problem>> cmleancop = new HashMap<>();
            cmleancop.put("ERR",new ArrayList<>());
            cmleancop.put("UNK",new ArrayList<>());
            cmleancop.put("THM",new ArrayList<>());
            cmleancop.put("CSA",new ArrayList<>());

            for (Problem problem : test.getProblems()){
                //System.out.println(problem.toString());

                // put all problems int a list
                totalProblems.add(problem);

                // save result for every atp in a list for all tests together
                List<Problem> l = satallax.get(problem.status_satallax);
                l.add(problem);
                l = leo.get(problem.status_leo);
                l.add(problem);
                l = nitpick.get(problem.status_nitpick);
                l.add(problem);
                if (problem.status_mleancop != null) {
                    //System.err.println("MLEANCOP HAS NO ERROR");
                    l = mleancop.get(problem.status_mleancop);
                    l.add(problem);
                }

                // save result for every atp in a list only for current test
                // used for filling up table
                l = csatallax.get(problem.status_satallax);
                l.add(problem);
                l = cleo.get(problem.status_leo);
                l.add(problem);
                l = cnitpick.get(problem.status_nitpick);
                l.add(problem);
                if (problem.status_mleancop != null) {
                    l = cmleancop.get(problem.status_mleancop);
                    l.add(problem);
                }
                // compare qmltp status with atp status: atp status is not qmltp status
                //
                // qmltp entry exists &&
                // qmltp has a solution = 'not unsolved'
                // atps have no error
                // atps agree on solution
                // atps have a solution = solution is not UNK
                // atps solution is NOT the same as qmltp solution
                if (
                        problem.status_qmltp != null &&
                        !problem.status_qmltp.equals("UNK") &&
                        !hasError(problem) &&
                        atpsAgree(problem) &&
                        !getAgreedStatus(problem).equals("UNK") &&
                        !getAgreedStatus(problem).equals(problem.status_qmltp)
                        ){
                    disagreementQmltpAtp.add(problem);
                }

                // compare qmltp status with mleancop status: mleancop status is not qmltp status
                //
                // qmltp entry exists
                // qmltp has a solution
                // mleancop entry exists
                // mleancop has a solution = solution is not UNK
                // mleancop solution is NOT the same as qmltp solution
                if (
                        problem.status_qmltp != null &&
                        !problem.status_qmltp.equals("UNK") &&
                        problem.status_mleancop != null &&
                        !problem.status_mleancop.equals("UNK") &&
                        !problem.status_mleancop.equals(problem.status_qmltp)
                        ){
                    disagreementQmltpMleancop.add(problem);
                }

                // compare mleancop status with atp status: mleancop status is not agreed atp status
                // compare qmltp status with mleancop status: mleancop status is not qmltp status
                //
                // mleancop entry exists
                // mleancop has a solution = solution is not UNK
                // atps have no error
                // atps agree on solution
                // atps have a solution = solution is not UNK
                // atps solution is NOT the same as mleancop solution
                if (
                        problem.status_mleancop != null &&
                        !problem.status_mleancop.equals("UNK") &&
                        !hasError(problem) &&
                        atpsAgree(problem) &&
                        !getAgreedStatus(problem).equals("UNK") &&
                        !getAgreedStatus(problem).equals(problem.status_mleancop)
                        ){
                    disagreementAtpMleancop.add(problem);
                }

                // atps disagree
                if (
                        !atpsAgree(problem)
                        ){
                    disagreementAtpAtp.add(problem);
                }

                // 1.0 problems
                // TODO

            }


            // create and add table entry for this test
            String delimiter = " ";
            StringBuilder entry = new StringBuilder();
            entry.append(test.test_name);
            entry.append(delimiter);
            entry.append(csatallax.get("THM").size() + csatallax.get("CSA").size());
            entry.append(delimiter);
            entry.append(csatallax.get("THM").size());
            entry.append(delimiter);
            entry.append(csatallax.get("CSA").size());
            entry.append(delimiter);
            entry.append(cleo.get("THM").size() + cleo.get("CSA").size());
            entry.append(delimiter);
            entry.append(cleo.get("THM").size());
            entry.append(delimiter);
            entry.append(cleo.get("CSA").size());
            entry.append(delimiter);
            entry.append(cnitpick.get("THM").size() + cnitpick.get("CSA").size());
            entry.append(delimiter);
            entry.append(cnitpick.get("THM").size());
            entry.append(delimiter);
            entry.append(cnitpick.get("CSA").size());
            entry.append(delimiter);
            entry.append(cmleancop.get("THM").size() + cmleancop.get("CSA").size());
            entry.append(delimiter);
            entry.append(cmleancop.get("THM").size());
            entry.append(delimiter);
            entry.append(cmleancop.get("CSA").size());
            entry.append(delimiter);
            table.add(entry.toString());
        }

        System.out.println("");
        System.out.println("==============================================");
        System.out.println("Results:");
        this.outputToStdout();
        this.outputToFiles(outputPath);
        System.out.println("FINISH");
    }

    private void outputToStdout(){
        System.out.println("totalProblems:             " + totalProblems.size());
        /*
        System.out.println("ConfirmedProblems:       " + confirmedProblems.size());
        System.out.println("unconfirmedProblems:     " + unconfirmedProblems.size());
        System.out.println("newProblemResultTotal:     " + newProblemResultTotal.size());
        System.out.println("newProblemResultTHM:     " + newProblemResultTHM.size());
        System.out.println("newProblemResultCSA:     " + newProblemResultCSA.size());
        */
        System.out.println("disagreementQmltpAtp:      " + disagreementQmltpAtp.size());
        System.out.println("disagreementQmltpMleancop: " + disagreementQmltpMleancop.size());
        /*
        System.out.println("newProblems:             " + newProblems.size());
        System.out.println("newProblemsSolved:       " + newProblemsSolved.size());
        System.out.println("totallyUnsolvedProblems: " + totallyUnsolvedProblems.size());
        */
        System.out.println("disagreementAtpAtp:        " + disagreementAtpAtp.size());
        System.out.println("disagreementAtpMleancop:   " + disagreementAtpMleancop.size());
        System.out.println();
        System.out.println("ERR satallax               " + satallax.get("ERR").size());
        System.out.println("UNK satallax               " + satallax.get("UNK").size());
        System.out.println("THM satallax               " + satallax.get("THM").size());
        System.out.println("CSA satallax               " + satallax.get("CSA").size());
        System.out.println("ERR leo                    " + leo.get("ERR").size());
        System.out.println("UNK leo                    " + leo.get("UNK").size());
        System.out.println("THM leo                    " + leo.get("THM").size());
        System.out.println("CSA leo                    " + leo.get("CSA").size());
        System.out.println("ERR nitpick                " + nitpick.get("ERR").size());
        System.out.println("UNK nitpick                " + nitpick.get("UNK").size());
        System.out.println("THM nitpick                " + nitpick.get("THM").size());
        System.out.println("CSA nitpick                " + nitpick.get("CSA").size());
        System.out.println("ERR mleancop               " + mleancop.get("ERR").size());
        System.out.println("UNK mleancop               " + mleancop.get("UNK").size());
        System.out.println("THM mleancop               " + mleancop.get("THM").size());
        System.out.println("CSA mleancop               " + mleancop.get("CSA").size());
        System.out.println();
        table.forEach(System.out::println);
    }

    private String statusMleanCopNoNull(Problem p){
        if (p == null) return "NUL";
        return p.status_mleancop;
    }

    private String statusQmltpNoNull(Problem p){
        if (p == null) return "NUL";
        return p.status_qmltp;
    }

    private String combineProblem(Problem p){
          return p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name + ","
                + statusQmltpNoNull(p) + "," + getAgreedStatus(p) + "," + statusMleanCopNoNull(p);
    }
    private void outputToFiles(String outputPath){
        System.out.println(Paths.get(outputPath.toString(),"totalProblems"));
        try {
            Files.write(Paths.get(outputPath.toString(),"totalProblems"),this.totalProblems.stream()
                    .map(p->combineProblem(p))
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write totalProblems file");
            e.printStackTrace();
        }
        /*
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
            Files.write(Paths.get(outputPath.toString(),"newProblemResultTotal"),this.newProblemResultTotal.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblemResultTotal file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"newProblemResultTHM"),this.newProblemResultTHM.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblemResultTHM file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"newProblemResultCSA"),this.newProblemResultCSA.stream()
                    .map(p->p.system + "," + p.domains + "," + p.constants + "," + p.consequence + "," + p.name)
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write newProblemResultCSA file");
            e.printStackTrace();
        }
        */
        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementQmltpAtp"),this.disagreementQmltpAtp.stream()
                    .map(p->combineProblem(p))
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementQmltpAtp file");
            e.printStackTrace();
        }
        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementQmltpMleancop"),this.disagreementQmltpMleancop.stream()
                    .map(p->combineProblem(p))
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementQmltpMleancop file");
            e.printStackTrace();
        }
        /*
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
        */

        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementAtpAtp"),this.disagreementAtpAtp.stream()
                    .map(p->combineProblem(p))
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementAtpAtp file");
            e.printStackTrace();
        }

        try {
            Files.write(Paths.get(outputPath.toString(),"disagreementAtpMleancop"),this.disagreementAtpMleancop.stream()
                    .map(p->combineProblem(p))
                    .collect(Collectors.joining("\n")).getBytes());
        } catch (IOException e) {
            System.err.println("Could not write disagreementAtpMleancop file");
            e.printStackTrace();
        }
        for (String status : satallax.keySet()){
            String filename = "satallax_" + status;
            try {
                Files.write(Paths.get(outputPath.toString(), filename ),this.satallax.get(status).stream()
                        .map(p->combineProblem(p))
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write " + filename + " file");
                e.printStackTrace();
            }
        }
        for (String status : leo.keySet()){
            String filename = "leo_" + status;
            try {
                Files.write(Paths.get(outputPath.toString(), filename ),this.leo.get(status).stream()
                        .map(p->combineProblem(p))
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write " + filename + " file");
                e.printStackTrace();
            }
        }
        for (String status : nitpick.keySet()){
            String filename = "nitpick_" + status;
            try {
                Files.write(Paths.get(outputPath.toString(), filename ),this.nitpick.get(status).stream()
                        .map(p->combineProblem(p))
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write " + filename + " file");
                e.printStackTrace();
            }
        }
        for (String status : mleancop.keySet()){
            String filename = "mleancop" + status;
            try {
                Files.write(Paths.get(outputPath.toString(), filename ),this.mleancop.get(status).stream()
                        .map(p->combineProblem(p))
                        .collect(Collectors.joining("\n")).getBytes());
            } catch (IOException e) {
                System.err.println("Could not write " + filename + " file");
                e.printStackTrace();
            }
        }
    }

    private boolean hasError(Problem p){
        return p.status_satallax.equals("ERR") || p.status_leo.equals("ERR") || p.status_nitpick.equals("ERR");
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
