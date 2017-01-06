package main;

import java.util.ArrayList;
import java.util.List;

public class Results {
    private List<Test> tests;
    public Results(){
        this.tests = new ArrayList<>();
    }
    public void addTest(Test t){
        this.tests.add(t);
    }

    public void evaluate(){
        for (Test test : this.tests){
            System.out.println("Evaluate: " + test.test_name + " " + test.system + " " + test.domain);
            for (Problem problem : test.getProblems()){
                System.out.println(problem.toString());
            }
        }
    }
}
