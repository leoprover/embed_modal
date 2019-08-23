import common
import sys
import common
from pathlib import *

def find_all_of_qmltp(problem_list, white_filter,status):
    ret = []
    for p in problem_list:
        if p.prover != "QMLTP":
            continue
        if status != None and p.szs != status:
            continue
        if white_filter != None and white_filter(p):
            ret.append(p)
        elif white_filter == None:
            ret.append(p)
    return ret

def filter_supported_csa(p):
    if p.quantification == "$constant":
        return True
    if p.quantification == "$cumulative" and p.system == "$modal_system_S5":
        return True
    return False

def filter_unknown_status(p):
    if p.szs in ["Theorem","CounterSatisfiable","Unsolved"]:
        return False
    print(p)
    return True

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    supported_csa = find_all_of_qmltp(problem_list,filter_supported_csa,"CounterSatisfiable")
    supported_thm = find_all_of_qmltp(problem_list,None,"Theorem")
    unsolved = find_all_of_qmltp(problem_list,None,"Unsolved")
    other = find_all_of_qmltp(problem_list,filter_unknown_status,None)
    print("supported thm",len(supported_thm))
    print("supported csa",len(supported_csa))
    print("unsolved",len(unsolved))
    print("other",len(other))

if __name__ == "__main__":
    main(sys.argv[1:])