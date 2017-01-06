package main;

public class Problem {
    // status can be THM, CSA, UNK
    // qmltp can also have status ??? which means the status was something else than Theorem, Non-Theorem, Unsolved
    public String name;
    public String category;
    public String status_qmltp;
    public String status_satallax;
    public String status_leo;
    public String status_nitpick;
    public double time_satallax;
    public double time_leo;
    public double time_nitpick;

    @Override
    public String toString(){
        return "name:" + name + " status_qmltp:" + status_qmltp + " status_satallax:" + status_satallax +
                " status_leo:" + status_leo + " status_nitpick:" + status_nitpick;
    }
}
