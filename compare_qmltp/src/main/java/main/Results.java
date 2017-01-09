package main;

import java.util.ArrayList;
import java.util.List;

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


    public void evaluate(){

        confirmedProblems = new ArrayList<>(); // y
        unconfirmedProblems = new ArrayList<>(); // y
        newProblemResult = new ArrayList<>(); // y
        newProblems = new ArrayList<>();
        newProblemsSolved = new ArrayList<>(); // n
        disagreementAtpAtp = new ArrayList<>(); // y + n
        disagreementQmltpAtp = new ArrayList<>(); // y
        totallyUnsolvedProblems = new ArrayList<>(); // y + n

        for (Test test : this.tests){
            System.out.println("Evaluate: " + test.test_name + " " + test.system + " " + test.domain);
            for (Problem problem : test.getProblems()){
                System.out.println(problem.toString());

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
