package transformation;


import util.tree.Node;

public class TransformContext {
    public final String modalDefintions;
    public final Node originalRoot;
    public final Node transformedRoot;
    public final ThfAnalyzer thfAnalyzer;
    public final SemanticsAnalyzer semanticsAnalyzer;

    protected TransformContext(String modalDefintions, Node originalRoot, Node transformedRoot, ThfAnalyzer thfAnalyzer, SemanticsAnalyzer semanticsAnalyzer){
        this.modalDefintions = modalDefintions;
        this.originalRoot = originalRoot;
        this.transformedRoot = transformedRoot;
        this.thfAnalyzer = thfAnalyzer;
        this.semanticsAnalyzer = semanticsAnalyzer;
    }

    public String getProblem(){
        StringBuilder problem = new StringBuilder();
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("% modal definitions \n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("\n");
        problem.append(this.modalDefintions);
        problem.append("\n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("% transformed problem\n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("\n");
        problem.append(this.transformedRoot.toStringWithLinebreaksFormatted());
        problem.append("\n\n");
        return problem.toString();
    }

    public String getProblemIncludingOld(){
        return this.getProblem() +
                "\n\n" +
                "% -------------------------------------------------------------------------\n" +
                "% old problem\n" +
                "% -------------------------------------------------------------------------\n" +
                "\n" +
                this.originalRoot.toStringWithLinebreaksCommented() +
                "\n\n";
    }

}
