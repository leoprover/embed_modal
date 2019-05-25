import common
import sys

def main():
    qmltp_path = sys.argv[1]
    print(qmltp_path)
    problem_file_list = common.get_problem_file_list(qmltp_path)
    problem_white_filter = None
    problem_black_filter = None
    problems_containing_symbol_qmltpeq = []

    for f in problem_file_list:
        if problem_white_filter != None and not f.name in problem_white_filter:
            continue
        if problem_black_filter != None and f.name in problem_black_filter:
            continue
        print("now processing",f)
        with open(f,"r") as fh:
            content = fh.read()
            if "qmltpeq" in content:
                problems_containing_symbol_qmltpeq.append(f.name)

    print("=========================================================")
    print(str(len(problems_containing_symbol_qmltpeq)) + " Problems containing the symbol qmltpeq:")
    print("[\"" + "\",\n\"".join(sorted(problems_containing_symbol_qmltpeq)) + "\"]")

if __name__ == '__main__':
    main()