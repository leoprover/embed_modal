package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    private List<Problem> problems;
    private Map<String,Problem> problemMap;
    public String test_name;
    public String system;
    public String domains;
    public String constants;
    public String consequence;

    public Test(){
        this.problems = new ArrayList<>();
        this.problemMap = new HashMap<>();
    }
    public void addProblem(Problem p){
        this.problems.add(p);
        this.problemMap.put(p.name,p);
        //System.out.println("added " + p.name);
    }
    public List<Problem> getProblems(){
        return this.problems;
    }
}
