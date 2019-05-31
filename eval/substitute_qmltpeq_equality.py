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

# first apply formula has children:  "qmltpeq@op1","@","op2"
# second apply formula has children: "qmltpeq","@","op1"
def exchangeQmltpEqualities(node):
    if node.getRule() == "thf_apply_formula":
        #print(node.children)
        qmltpeq_at_op1_node = node.getChild(0)
        if node.childCount() == 1: # "qmltpeq" and "@" were already removed
            return
        at_operator_node = node.getChild(1)
        op2_node = node.getChild(2)
        #print("0",qmltpeq_at_op1_node)
        #print("1",at_operator_node)
        #print("2",op2_node)
        if "qmltpeq" != qmltpeq_at_op1_node.getContent() and "qmltpeq" in qmltpeq_at_op1_node.getContent():
            at_operator_node.getFirstTerminal().setContent("=")
            qmltpeq_at_op1_node.removeChild(1) # @
            qmltpeq_at_op1_node.removeChild(0) # qmltpeq

def removeQmltpeqTypeDeclaration(node):
    if node.rule == "name" and "qmltpeq" in node.getContent():
        tptp_input_node = node.parent.parent.parent
        tptp_input_node.removeChildren()
        tptp_input_node.setContent("")

def main(qmltp_dir,out_dir):
    sys.setrecursionlimit(1500)
    qmltp_path = Path(qmltp_dir)
    out_path = Path(out_dir)
    problem_file_list = get_problem_file_list(qmltp_path)
    # problems that have the symbol "=" replaced by "customqmltpeq" or "customqmltpeqfromineq" +
    # problems that contain the symbol "qmltpeq"
    # all problems have an adequate axiomatization
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_qmltpeq_symbol_with_axiomatization + \
                           filters_for_the_qmltp.qmltp_problems_containing_native_equality_with_axiomatization
    #problem_white_filter = ["GSV107+1.p"]
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
            # replace symbol "customqmltpeq" with symbol "qmltpeq" for conformity
            content = content.replace("customqmltpeq","qmltpeq")
            # replace symbol "customqmltpeqfromineq" with "qmltpeq" for conformity
            content = content.replace("customqmltpeqfromineq","qmltpeq")
            #content = "\nthf(typedecl_qmltpeq,type,qmltpeq: ($i > $i > $o)).\nthf(defuse,axiom,($box@(~(($box@((qmltpeq@def@use)))))))."
            #content = "thf(defuse,axiom,(qmltpeq@def@use))." + "\nthf(typedecl_qmltpeq,type,qmltpeq: ($i > $i > $o))." + "\nthf(qmltpeq_type,type,(qmltpeq : ($i>$i>$o)))."
            lexer = HmfLexer(InputStream(content))
            stream = CommonTokenStream(lexer)
            parser = HmfParser(stream)
            tree = parser.tPTP_file()
            listener = DefaultTreeListener(parser)
            walker = ParseTreeWalker()
            walker.walk(listener, tree)
            root = listener.root

            # replace all (qmltpeq @ a @ b) by (a = b)
            root.dfs(exchangeQmltpEqualities)
            # replace all type sentences for the symbol "qmltpeq"
            root.dfs(removeQmltpeqTypeDeclaration)

            newProblem = str(root)
            # write to file
            outFileDir.mkdir(exist_ok=True)
            with open(outFilePath,"w+") as fhw:
                fhw.write(newProblem)

if __name__ == '__main__':
    main(sys.argv[1],sys.argv[2])