package main;

import java.util.ArrayList;
import java.util.List;

public class Results {
    private List<Problem> problems;
    public Results(){
        this.problems = new ArrayList<>();
    }
    public void addProblem(Problem p){
        this.problems.add(p);
    }
}
