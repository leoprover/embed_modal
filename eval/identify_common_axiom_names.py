from common import get_problem_file_list, create_tree
import sys
import filters_for_the_qmltp
from pathlib import Path

class SearchResult:
    def __init__(self):
        self.names = []
        self.nodes = []
    def add(self,name,node):
        self.names.append(name)
        self.nodes.append(node)

def getAxiomNames(node,res):
    if node.rule == "formula_role":
        role = node.getContent()
        if role == "axiom":
            tptp_input_node = node.parent.parent.parent
            tptp_name_node = node.parent.getChild(2)
            res.add(str(tptp_name_node),tptp_input_node)

def main(qmltp_dir):
    sys.setrecursionlimit(1500)
    qmltp_path = Path(qmltp_dir)
    problem_file_list = get_problem_file_list(qmltp_path)
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_qmltpeq_symbol_with_axiomatization + \
    #                       filters_for_the_qmltp.qmltp_problems_containing_native_equality_with_axiomatization
    problem_black_filter = None
    commonAxiomNames = None

    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        print("now processing",f)
        with open(f,"r") as fh:
            content = fh.read()
            root = create_tree(content)

            # identify axiom names
            res = SearchResult()
            root.dfs(getAxiomNames,res)
            currentAxiomNames = set(res.names)
            if commonAxiomNames == None:
                commonAxiomNames = set(currentAxiomNames)
            commonAxiomNames = commonAxiomNames.intersection(currentAxiomNames)
            print("intersection of names with previous problems", commonAxiomNames)

    print("================================")
    print("there are " + str(len(commonAxiomNames)) + " common axiom names among the problems in " + str(qmltp_path))
    print("common axiom names are:")
    print("[\n\"" + "\",\n\"".join(sorted(list(commonAxiomNames))) + "\"\n]")

if __name__ == '__main__':
    main(sys.argv[1])
