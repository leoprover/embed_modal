import common
from pathlib import *



def extract_qmltp_info_from_problem(problem):
    start = problem.find("% Status") + len("% Status")
    end = problem.find("% Rating")
    info = problem[start:end]\
        .replace(":","")\
        .replace("varying","")\
        .replace("cumulative","")\
        .replace("constant","")\
        .replace("v1.1","")\
        .replace("K ","  ")\
        .replace("D ","  ")\
        .replace("T ","  ")\
        .replace("S4 ","   ")\
        .replace("S5 ","   ")\
        .replace("%","")\
        .strip()\
        .split("\n")
    info = list(map(lambda e: e.strip(),info))
    info = list(map(lambda e:
                    list(filter(lambda x: len(x)!=0,e.split(" "))),info))
    return info

# filename, prover, status, wc_time, cpu_time, system, quantification, consequence, constants, transformation_parameter_list
def extract_qmltp_info_from_problem_file_list(problem_file_list):
    res = []
    for p in problem_file_list:
        with open(p,"r") as fh:
            content = fh.read()
            info = extract_qmltp_info_from_problem(content)
            for system, s_val in zip(("$modal_system_K","$modal_system_D","$modal_system_T","$modal_system_S4","$modal_system_S5"),info): # s_val is an inner list
                for quantification, q_val in zip(("$varying","$cumulative","$constant"),s_val):
                    line = [p.name, "QMLTP", q_val, "0", "0",
                              system, quantification, "$local", "$rigid",
                              ""]
                    res.append(line)
    return res

def extract_qmltp_info_from_problem_file_list_to_problem_list(problem_file_list):
    res = []
    qmltp_info = extract_qmltp_info_from_problem_file_list(problem_file_list)
    for p in qmltp_info:
        newp = common.Problem(p[0],p[1],p[2],p[3],p[4],p[5],p[6],p[7],p[8],p[9])
        res.append(newp)
    return res

def extract_qmltp_info_from_problem_to_dict(problem):
    res = {}
    info = extract_qmltp_info_from_problem(problem)
    for system, s_val in zip(("$modal_system_K","$modal_system_D","$modal_system_T","$modal_system_S4","$modal_system_S5"),info):
        #print("X",system,s_val)
        if not system in res:
            res[system] = {}
        for quantification, q_val in zip(("$varying","$cumulative","$constant"),s_val):
            if not quantification in res[system]:
                res[system][quantification] = {}
            res[system][quantification] = q_val
    return res

def extract_qmltp_info_from_problem_file_list_to_dicts(problem_file_list):
    res = {}
    for p in problem_file_list:
        with open(p,"r") as fh:
            content = fh.read()
            res[p.name] = extract_qmltp_info_from_problem_to_dict(content)
    return res

