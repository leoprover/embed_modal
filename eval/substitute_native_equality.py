from antlr4 import *
from HmfLexer import HmfLexer
from HmfListener import HmfListener
from HmfParser import HmfParser
import common
import sys
import filters_for_the_qmltp
from pathlib import Path

# ctx methods
"""
accept
addChild
addErrorNode
addTokenNode
children
copyFrom
depth
enterRule
exception
exitRule
getAltNumber
getChild
getChildCount
getChildren
getPayload
getRuleContext
getRuleIndex
getSourceInterval
getText
getToken
getTokens
getTypedRuleContext
getTypedRuleContexts
invokingState
isEmpty
parentCtx
parser
removeLastChild
setAltNumber
start
stop
thf_pair_connective
thf_unitary_formula
toString
toStringTree
"""


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



class Node:
    def __init__(self,rule,content):
        self.rule = rule
        self.content = content
        self.children = []
    def getRule(self):
        return self.rule
    def getContent(self):
        return self.content
    def setContent(self,newContent):
        self.content = newContent
    def setParent(self,parent):
        self.parent = parent
    def getParent(self):
        return self.parent
    def hasChildren(self):
        return len(self.children) != 0
    def getChild(self,n):
        return self.children[n]
    def addChildBack(self,childNode):
        self.children.append(childNode)
        childNode.setParent(self)
    def addChildFront(self,childNode):
        self.children = [childNode] + self.children
    def childCount(self):
        return len(self.children)
    def removeChild(self,n):
        if self.childCount() <= n:
            self.children = self.children[:n]
        else:
            self.children = self.children[:n] + self.children[n+1:]
    def replaceChild(self,n,newChild):
        self.children[n] = newChild
        newChild.setParent(self)
    def getFirstTerminal(self):
        current = self
        while current.hasChildren():
            current = current.getChild(0)
        return current
    def __str__(self):
        self.strret = ""
        self.dfs(Node.string_helper,self)
        ret = self.strret
        #del self.strret
        return ret
    @staticmethod
    def string_helper(node,root_node):
        if not node.hasChildren():
            root_node.strret += node.getContent()
    def __repr__(self):
        return self.__str__()
    # callbacks have the form mycallback(node,args)
    def dfs(self,callback,*callback_args):
        stack = [self]
        while len(stack) != 0:
            current = stack.pop()
            for child in current.children[::-1]:
                stack.append(child)
            callback(current,*callback_args)

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
    problem_file_list = common.get_problem_file_list(qmltp_path)
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality_with_axiomatization
    #problem_white_filter = ["SYM052+1.p"]
    problem_black_filter = None
    problems_with_equalities = []
    problems_unprocessed = []

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
            #content = "thf(1,conjecture,(a=b))."
            #content = "thf(1,conjecture,(a!=b))."
            #content = "thf(con,conjecture,((($dia@(?[X:$i]:($dia@(?[Y:$i]:(X!=Y)))))&(a&($box@(![X:$i]:($box@(a=>(e@X)))))))=>(?[X:$i]:(?[Y:$i]:(X!=Y)))))."
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

        #if listener.containsEquality():
        #    problems_with_equalities.append(f.name)
        #    print("contains equality")
        #break
    print("=========================================================")
    print("Problems containing a binary pair with the equality sign:")
    print("[\"" + "\",\n\"".join(sorted(problems_with_equalities)) + "\"]")
    print("Problems that could not be processed due to recurions depth exceeded error:")
    print("[\"" + "\",\n\"".join(sorted(problems_unprocessed)) + "\"]")

if __name__ == '__main__':
    main()