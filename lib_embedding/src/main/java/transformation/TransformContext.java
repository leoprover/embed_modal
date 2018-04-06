package transformation;


import util.tree.Node;

import java.util.List;
import java.util.logging.Logger;

public class TransformContext {

    private static final Logger log = Logger.getLogger( "default" );

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
        log.fine("Creating new problem.");
        StringBuilder problem = new StringBuilder();
        log.finest("Problem has " + userTypes.size() + " userTypes.");
        if (!this.userTypes.isEmpty()) {
            //userTypes.stream().map(ut->ut.toStringLeafs()).forEach(ut-> log.finest("Usertype: " + ut));
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("% user type definitions \n");
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("\n");
            for (Node userType : this.userTypes) {
                problem.append(userType.toStringLeafs());
                problem.append("\n");
            }
            problem.append("\n");
        }
        if (!this.modalDefintions.equals("")) {
            log.finest("Problem has meta-modal definitions.");
            //log.finest("Problem has modal definitions:\n" + this.modalDefintions);
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("% modal definitions \n");
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("\n");
            problem.append(this.modalDefintions);
        } else {
            log.finest("Problem has no meta-modal definitions.");
        }
        //log.finest("Transformed problem:\n" + this.transformedRoot.toStringWithLinebreaks());
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("% transformed problem\n");
        problem.append("% -------------------------------------------------------------------------\n");
        problem.append("\n");
        problem.append(this.transformedRoot.toStringWithLinebreaks().trim());
        problem.append("\n\n");
        if (!this.auxiliaryDefinitions.equals("")) {
            log.finest("Problem has auxiliary definitions.");
            //log.finest("Problem has auxiliary definitions:\n" + this.auxiliaryDefinitions);
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("% auxiliary definitions \n");
            problem.append("% -------------------------------------------------------------------------\n");
            problem.append("\n");
            problem.append(this.auxiliaryDefinitions);
        } else {
            log.finest("Problem has no auxiliary definitions.");
        }
        problem.append("\n");
        log.finest("Completely transformed problem:\n" + problem.toString().trim());
        return problem.toString();
    }

    public String getProblemIncludingOld(){
        return this.getProblem() +
                "% -------------------------------------------------------------------------\n" +
                "% old problem\n" +
                "% -------------------------------------------------------------------------\n" +
                "\n" +
                this.originalRoot.toStringWithLinebreaksCommented().trim() +
                "\n\n";
    }

}
