from antlr4 import *
from HmfLexer import HmfLexer
from HmfListener import HmfListener
from HmfParser import HmfParser
import sys

class HmfPrintListener(HmfListener):
    def enterThf_binary_pair(self, ctx):
        #for c in dir(ctx):
        #    print(c)
        for c in ctx.getChildren():
            print(c.getText())
def main():
    lexer = HmfLexer(InputStream("thf(1,conjecture,($true = $false))."))
    stream = CommonTokenStream(lexer)
    parser = HmfParser(stream)
    tree = parser.tPTP_file()
    printer = HmfPrintListener()
    walker = ParseTreeWalker()
    walker.walk(printer, tree)

if __name__ == '__main__':
    main()