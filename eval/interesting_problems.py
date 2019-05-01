from extract_qmltp_info import extract_qmltp_info_from_problem_file_list_to_dicts
from common import problem_directory,get_problem_file_list

file_list = get_problem_file_list(problem_directory)
qmltp_info = extract_qmltp_info_from_problem_file_list_to_dicts(file_list)

def get_different_cumul_status():
    ret = {}
    for problem_name,system_dict in qmltp_info.items():
        for system, quantification_dict in system_dict.items():
            if problem_name in ret:
                continue
            if (quantification_dict["$cumulative"] != "Unsolved"):#and system != "$modal_system_S5":
                if (quantification_dict["$cumulative"] != quantification_dict["$constant"] and quantification_dict["$constant"] != "Unsolved") or \
                    (quantification_dict["$cumulative"] != quantification_dict["$varying"] and quantification_dict["$varying"] != "Unsolved" ):
                    ret[problem_name] = system_dict
    return ret
cumul_interesting_problems = get_different_cumul_status()



