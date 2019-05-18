from antlr4 import *
from HmfLexer import HmfLexer
from HmfListener import HmfListener
from HmfParser import HmfParser
import common
import sys
import filters_for_the_qmltp

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
    def __str__(self):
        self.strret = ""
        self.dfs(sb)
        ret = self.strret
        #del self.strret
        return ret
    def stringBuilder(self,node):
        print(current.content)

        if not node.hasChildren():
            print(current.content)
        self.strret += node.content
    def dfs(self,callback,*callback_args):
        stack = [self]
        while len(stack) != 0:
            current = stack.pop()
            for child in current.children[::-1]:
                stack.append(child)
            if not current.hasChildren():
                print(current.content)

def exchangeEqualities(node):
    exchangeEqualities.equalityFound = False
    exchangeEqualities.inequalityFound = False
    if node.getRule() == "thf_binary_pair":
        operatorNode = node.getChild(1)
        if operatorNode.getContent() == "=":
            exchangeEqualities.equalityFound = True
            operatorNode.setContent("@")
            node.addChildFront("terminal","@")
            node.addChildFront("terminal","customqmltpeq")
            node.addChildFront("terminal","(")
            node.addChildBack("terminal",")")
        if operatorNode.getContent == "!=":
            exchangeEqualities.inequalityFound = True
            operatorNode.setContent("@")
            node.addChildFront("terminal","@")
            node.addChildFront("terminal","customqmltpineq")
            node.addChildFront("terminal","(")
            node.addChildBack("terminal",")")
            node.addChildFront("terminal","~")
            node.addChildFront("terminal","(")
            node.addChildBack("terminal",")")

def getOOOTypeDeclaration(identifier):
    return "thf(typedecl_" + identifier + ",type," + identifier + ": ($o > $o > $o))."

def main():
    sys.setrecursionlimit(1500)
    qmltp_path = sys.argv[1]
    out_path = sys.argv[2]
    problem_file_list = common.get_problem_file_list(qmltp_path)
    #problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality_with_axiomatization
    problem_white_filter = ["SYM052+1.p"]
    problem_black_filter = None
    problems_with_equalities = []
    problems_unprocessed = []

    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        print("now processing",f)
        #lexer = HmfLexer(FileStream(f))
        with open(f,"r") as fh:
            content = fh.read()
            content = "thf(1,conjecture,(a=b))."
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
            root.dfs(exchangeEqualities)
            if exchangeEqualities.equalityFound:
                print("eq found")
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