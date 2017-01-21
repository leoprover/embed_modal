package main;

public class Problem {
    // status can be THM, CSA, UNK
    // qmltp can also have status ??? which means the status was something else than Theorem, Non-Theorem, Unsolved
    public String name = null;
    public String problem = null;
    public String category = null;
    public String system = null;
    public String domains = null;
    public String constants = null;
    public String consequence = null;
    public String rating = null;
    public String status_qmltp = null;
    public String status_satallax = null;
    public String status_leo = null;
    public String status_nitpick = null;
    public String status_mleancop = null;
    public double time_satallax;
    public double time_leo;
    public double time_nitpick;
    public double time_mleancop;

    @Override
    public String toString(){
        return "name:" + name + " system:" + system + " domains:" + domains + " constants: " + constants + " consequence:" + consequence +
                " status_qmltp:" + status_qmltp + " status_satallax:" + status_satallax +
                " status_leo:" + status_leo + " status_nitpick:" + status_nitpick+
                " status_mleancop:" + status_mleancop;
    }
}
