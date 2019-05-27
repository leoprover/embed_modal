from antlr4 import *
from HmfLexer import HmfLexer
from HmfListener import HmfListener
from HmfParser import HmfParser
import common
import sys
import filters_for_the_qmltp

class HmfEqualityListener(HmfListener):
    def __init__(self):
        self.containsEqualityInBinaryPair = False
    def containsEquality(self):
        return self.containsEqualityInBinaryPair
    def enterThf_binary_pair(self, ctx):
        if ctx.getChild(1).getText() == "=" or ctx.getChild(1).getText() == "!=":
            self.containsEqualityInBinaryPair = True

def main(qmltp_path):
    sys.setrecursionlimit(1500)
    problem_file_list = common.get_problem_file_list(qmltp_path)
    problem_white_filter = None
    problem_black_filter = None
    problems_with_equalities = []
    problems_unprocessed = []

    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        print("now processing",f)
        try:
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
        except:
            problems_unprocessed.append(f.name)

    print("=========================================================")
    print("Problems containing a binary pair with the equality sign:")
    print("[\"" + "\",\n\"".join(sorted(problems_with_equalities)) + "\"]")
    print("Problems that could not be processed due to recurions depth exceeded error:")
    print("[\"" + "\",\n\"".join(sorted(problems_unprocessed)) + "\"]")

if __name__ == '__main__':
    main(sys.argv[1])