from common import get_problem_file_list,create_tree
import sys
import filters_for_the_qmltp
from pathlib import Path
import re

def remove_equality_axioms(node):
    if node.rule == "name":
        name = node.getContent()
        subst = re.findall("_substitution_[0-9]+",name)
        if name in ["reflexivity","symmetry","transitivity"] or len(subst) == 1:
            tptp_input_node = node.parent.parent.parent
            tptp_input_node.removeChildren()
            tptp_input_node.setContent("% removed axiom " + name)

def main(qmltp_dir,out_dir):
    sys.setrecursionlimit(1500)
    qmltp_path = Path(qmltp_dir)
    out_path = Path(out_dir)
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
            root.dfs(remove_equality_axioms)

            newProblem = str(root)
            # write to file
            outFileDir.mkdir(exist_ok=True)
            with open(outFilePath,"w+") as fhw:
                fhw.write(newProblem)

if __name__ == '__main__':
    main(sys.argv[1],sys.argv[2])