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
    def visitTerminal(self, terminalNode):
        self.nodeptr.addChildBack(Node("terminal",terminalNode.getText()))
    def enterEveryRule(self,ctx):
        rule = self.ruleNames[ctx.getRuleIndex()]
        node = Node(rule,ctx.getText())
        self.nodeptr.addChildBack(node)
        self.nodeptr = node
    def exitEveryRule(self,ctx):
        self.nodeptr = self.nodeptr.getParent()


class EqualityReplacementResult:
    def __init__(self):
        self.equalityFound = False
        self.inequalityFound = False

def exchangeEqualities(node,eqresult):
    if node.getRule() == "thf_binary_pair":
        operatorNode = node.getChild(1)
        if operatorNode.getContent() == "=":
            eqresult.equalityFound = True
            operatorTerminal = operatorNode.getFirstTerminal()
            operatorTerminal.setContent("@")
            node.addChildFront(Node("terminal","@"))
            node.addChildFront(Node("terminal","customqmltpeq"))
            node.addChildFront(Node("terminal","("))
            node.addChildBack(Node("terminal",")"))
        if operatorNode.getContent() == "!=":
            eqresult.inequalityFound = True
            operatorTerminal = operatorNode.getFirstTerminal()
            operatorTerminal.setContent("@")
            node.addChildFront(Node("terminal","@"))
            node.addChildFront(Node("terminal","customqmltpeqfromineq"))
            node.addChildFront(Node("terminal","("))
            node.addChildBack(Node("terminal",")"))
            node.addChildFront(Node("terminal","~"))
            node.addChildFront(Node("terminal","("))
            node.addChildBack(Node("terminal",")"))

def getIIOTypeDeclaration(identifier):
    return "thf(typedecl_" + identifier + ",type," + identifier + ": ($i > $i > $o))."

def getOOOTypeDeclaration(identifier):
    return "thf(typedecl_" + identifier + ",type," + identifier + ": ($o > $o > $o))."

def main():
    sys.setrecursionlimit(1500)
    qmltp_path = Path(sys.argv[1])
    out_path = Path(sys.argv[2])
    problem_file_list = get_problem_file_list(qmltp_path)
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality_with_axiomatization
    #problem_white_filter = ["SYM052+1.p"]
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
            #continue
        print("now processing",f)
        with open(f,"r") as fh:
            content = fh.read()
            lexer = HmfLexer(InputStream(content))
            stream = CommonTokenStream(lexer)
            parser = HmfParser(stream)
            tree = parser.tPTP_file()
            #print(dir(tree))
            #print(tree.__class__)
            listener = DefaultTreeListener(parser)
            walker = ParseTreeWalker()
            walker.walk(listener, tree)
            root = listener.root
            eqresult = EqualityReplacementResult()
            root.dfs(exchangeEqualities,eqresult)
            newProblem = str(root)
            if eqresult.equalityFound:
                newProblem = getIIOTypeDeclaration("customqmltpeq") + "\n" + newProblem
            if eqresult.inequalityFound:
                newProblem = getIIOTypeDeclaration("customqmltpeqfromineq") + "\n" + newProblem
            outFileDir.mkdir(exist_ok=True)
            with open(outFilePath,"w+") as fhw:
                fhw.write(newProblem)


if __name__ == '__main__':
    main()