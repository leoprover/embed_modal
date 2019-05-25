from antlr4 import *
from HmfLexer import HmfLexer
from HmfListener import HmfListener
from HmfParser import HmfParser
from common import Node, get_problem_file_list
import sys
import filters_for_the_qmltp
from pathlib import Path


class DefaultTreeListener(HmfListener):
    def __init__(self,parser):
        self.root = Node("root",None)
        self.nodeptr = self.root
        self.ruleNames = parser.ruleNames
        self.currentRole = None
        self.axiomNames = set()
    def enterFormula_role(self, ctx:HmfParser.Formula_roleContext):
        role = ctx.getText()
        self.currentRole = role
    def enterName(self, ctx:HmfParser.NameContext):
        name = ctx.getText()
        if self.currentRole == "axiom":
            self.axiomNames.add(name)

def main():
    sys.setrecursionlimit(1500)
    qmltp_path = Path(sys.argv[1])
    problem_file_list = get_problem_file_list(qmltp_path)
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality_with_axiomatization
    #problem_white_filter = ["SYM052+1.p"]
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
            lexer = HmfLexer(InputStream(content))
            stream = CommonTokenStream(lexer)
            parser = HmfParser(stream)
            tree = parser.tPTP_file()
            listener = DefaultTreeListener(parser)
            walker = ParseTreeWalker()
            walker.walk(listener, tree)
            if commonAxiomNames == None:
                commonAxiomNames = set()
                commonAxiomNames = commonAxiomNames.union(listener.axiomNames)
            else:
                commonAxiomNames = commonAxiomNames.intersection(listener.axiomNames)
            print(commonAxiomNames)
    print("================================")
    print("common axiom names are:")
    print(commonAxiomNames)


if __name__ == '__main__':
    main()