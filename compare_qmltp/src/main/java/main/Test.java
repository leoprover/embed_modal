package main;

import java.util.ArrayList;
import java.util.List;

public class Test {
    private List<Problem> problems;
    public String test_name;
    public String system;
    public String domain;
    public String constants;
    public String consequence;

    public Test(){
        this.problems = new ArrayList<>();
    }
    public void addProblem(Problem p){
        this.problems.add(p);
        //System.out.println("added " + p.name);
    }
    public List<Problem> getProblems(){
        return this.problems;
    }
}
