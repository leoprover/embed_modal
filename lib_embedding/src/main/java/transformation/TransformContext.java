package transformation;


import util.tree.Node;

import java.util.List;
import java.util.Set;

public class TransformContext {
    public final String modalDefintions;
    public final String auxiliaryDefinitions;
    public final List<Node> userTypes;
    public final Node originalRoot;
    public final Node transformedRoot;
    public final ThfAnalyzer thfAnalyzer;
    public final SemanticsAnalyzer semanticsAnalyzer;

    protected TransformContext(String modalDefintions, String auxiliaryDefinitions, List<Node> userTypes, Node originalRoot, Node transformedRoot, ThfAnalyzer thfAnalyzer, SemanticsAnalyzer semanticsAnalyzer){
        this.modalDefintions = modalDefintions;
        this.auxiliaryDefinitions = auxiliaryDefinitions;
        this.userTypes = userTypes;
        this.originalRoot = originalRoot;
        this.transformedRoot = transformedRoot;
        this.thfAnalyzer = thfAnalyzer;
        this.semanticsAnalyzer = semanticsAnalyzer;
    }

    public String getProblem(){
        StringBuilder problem = new StringBuilder();
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("% user types definitions \n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("\n");
        for (Node userType : this.userTypes){
            problem.append(userType.toStringLeafs());
            problem.append("\n");
        }
        problem.append("\n");
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
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("% auxiliary definitions \n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("\n");
        problem.append(this.auxiliaryDefinitions);
        problem.append("\n");
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
