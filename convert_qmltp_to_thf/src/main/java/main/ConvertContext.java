package main;

import util.tree.Node;

public class ConvertContext {
    public Node original;
    public Node converted;
    public String name;
    public String definitions;

    public ConvertContext (Node original, Node converted, String name, String definitions){
        this.original = original;
        this.converted = converted;
        this.name = name;
        this.definitions = definitions;
    }

    public String getNewProblem(){
        return definitions + "\n% converted problem\n" + converted.toStringWithLinebreaks();
    }

    public String getNewProblemWithOriginalProblem(){
        return this.getNewProblem() + "\n\n% original problem\n" + this.original.toStringWithLinebreaksCommented();
    }
}
