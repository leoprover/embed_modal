from common import get_problem_file_list,create_tree,check_axiom_szs,Node
import sys
import filters_for_the_qmltp
from pathlib import Path
import re

def remove_equality_axioms(node:Node, bin_treelimitedrun,bin_prover):
    if node.rule == "name":
        name = node.getContent()
        subst = re.findall("_substitution_[0-9]+",name)
        if name in ["reflexivity","symmetry","transitivity"] or len(subst) == 1:
            tptp_input_node = node.parent.parent.parent
            szs = check_axiom_szs(bin_treelimitedrun,bin_prover,node.getRoot(),name,200,200)
            if szs == "Theorem":
                #print("removed axiom " + name)
                tptp_input_node.removeChildren()
                tptp_input_node.setContent("% removed axiom " + name)
            else:
                print("WARNING: could not proof axiom " + name + ". SZS: " + szs)

def main(qmltp_dir,out_dir):
    bin_prover    = "java -Xss128m -Xmx4g -Xms1g -jar /home/tg/apps/Leo-III/bin/leo3.jar %s -t %d " \
                     "--atp e=/home/tg/embed_modal/eval/provers/leo1.3/externals/eprover " \
                     "--atp cvc4=/home/tg/embed_modal/eval/provers/leo1.3/externals/cvc4-1.7-x86_64-linux-opt " \
                     "--atp-max-jobs 2 " \
                     "--atp-timeout cvc4=30 " \
                     "--atp-timeout e=30 " \
                     "--atp-debug "
    bin_treelimitedrun = "/home/tg/embed_modal/eval/TreeLimitedRun"

    sys.setrecursionlimit(1500)
    qmltp_path = Path(qmltp_dir)
    out_path = Path(out_dir)
    out_path.mkdir(exist_ok=True)
    problem_file_list = get_problem_file_list(qmltp_path)
    # problems that have the symbol "=" replaced by "customqmltpeq" or "customqmltpeqfromineq" +
    # problems that contain the symbol "qmltpeq"
    # all problems have an adequate axiomatization
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_qmltpeq_symbol_with_axiomatization + \
    #                       filters_for_the_qmltp.qmltp_problems_containing_native_equality_with_axiomatization
    problem_white_filter = None
    problem_black_filter = None
    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        outFileDir = out_path / f.name[:3]
        outFilePath = outFileDir / f.name
        if outFilePath.exists():
            print(f,"already exists.")
            continue
        print("now processing",f)
        with open(f,"r") as fh:
            content = fh.read()
            root = create_tree(content)

            # remove axioms
            root.dfs(remove_equality_axioms,bin_treelimitedrun,bin_prover)
            newProblem = str(root)

            # write to file
            outFileDir.mkdir(exist_ok=True)
            with open(outFilePath,"w+") as fhw:
                fhw.write(newProblem)

if __name__ == '__main__':
    main(sys.argv[1],sys.argv[2])