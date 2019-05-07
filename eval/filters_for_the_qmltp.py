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

qmltp_problems_containing_equality = [
    "SYM052+1.p",
    "SYM055+1.p",
    "SYM056+1.p",
    "SYM057+1.p",
    "SYM064+1.p",
    "SYM068+1.p",
    "SYM085+1.p",
    "GSV060+1.p",
    "GSV061+1.p",
    "GSV062+1.p",
    "GSV063+1.p",
    "GSV064+1.p",
    "GSV065+1.p",
    "GSV066+1.p",
    "GSV067+1.p",
    "GSV068+1.p",
    "GSV069+1.p",
    "GSV070+1.p",
    "GSV071+1.p",
    "GSV072+1.p",
    "GSV073+1.p",
    "GSV074+1.p",
    "GSV075+1.p",
    "GSV076+1.p",
    "GSV077+1.p",
    "GSV078+1.p",
    "GSV079+1.p",
    "GSV080+1.p",
    "GSV081+1.p",
    "GSV082+1.P",
    "GSV083+1.p",
    "GSV084+1.p",
    "GSV085+1.p",
    "GSV086+1.p",
    "GSV087+1.p",
    "GSV088+1.p",
    "GSV089+1.p",
    "GSV090+1.p",
    "GSV091+1.p",
    "GSV092+1.p",
    "GSV093+1.p",
    "GSV094+1.p",
    "GSV095+1.p",
    "GSV096+1.p",
    "GSV097+1.p",
    "GSV098+1.p",
    "GSV099+1.p",
    "GSV100+1.p",
    "GSV101+1.p",
    "GSV102+1.p",
    "GSV103+1.p",
    "GSV104+1.p",
    "GSV105+1.p",
    "GSV106+1.p",
    "GSV107+1.p"]

problems_without_modal_operators = [
    "NLP001+1.p",
    "NLP002+1.p",
    "NLP003+1.p",
    "NLP004+1.p",
    "NLP005+1.p",
    "SET" # and many more
]
