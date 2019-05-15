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

class HmfEqualityListener(HmfListener):
    def __init__(self):
        self.containsEqualityInBinaryPair = False
    def enterTPTP_input(self, ctx):
        print("TPTP_input found!")
    def containsEquality(self):
        return self.containsEqualityInBinaryPair
    def enterThf_binary_pair(self, ctx):
        if ctx.getChild(1).getText() == "=":
            self.containsEqualityInBinaryPair = True

def main():
    qmltp_path = sys.argv[1]
    problem_file_list = common.get_problem_file_list(qmltp_path)
    problem_white_filter = filters_for_the_qmltp.qmltp_problems_containing_equality
    problem_black_filter = []
    problems_with_equalities = []

    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        print("now processing",f)
        lexer = HmfLexer(FileStream(f))
        stream = CommonTokenStream(lexer)
        parser = HmfParser(stream)
        tree = parser.tPTP_file()
        listener = HmfEqualityListener()
        walker = ParseTreeWalker()
        walker.walk(listener, tree)
        if listener.containsEquality():
            problems_with_equalities.append(f.name)
            print("contains equality")

    print("=========================================================")
    print("Problems containing a binary pair with the equality sign:")
    print("[\"" + "\",\n\"".join(sorted(problems_with_equalities)) + "\"]")

if __name__ == '__main__':
    main()