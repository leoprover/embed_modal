import sys
import common

def check_consistency_iteration_callback(filename, system, quantification, problem_list, *callback_args):
    ret_dict = callback_args[0]
    szs_dict = common.create_szs_dict_of_configuration(problem_list)
    if ("InputError" in szs_dict):
        if not filename in ret_dict:
            ret_dict[filename] = []
        ret_dict[filename].append({'system':system,
                                   'quantification':quantification,
                                   'problem_list':problem_list})

def main(problem_file_list):
    problem_list = common.accumulate_csv(problem_file_list)
    problem_dict = common.create_dict_from_problems(problem_list)
    filename_to_issue = {}
    common.iterate_dict(problem_dict, check_consistency_iteration_callback, filename_to_issue)
    print("files with issues:",len(filename_to_issue))
    for filename in sorted(filename_to_issue):
        issue_list = filename_to_issue[filename]
        print("=================================================================================================================================")
        print(filename)
        for issue_dict in issue_list:
            print("")
            print(common.representation_of_configuration(issue_dict['system'],issue_dict['quantification'],issue_dict['problem_list']))
    print("###############")
    print("###############")
    print("###############")
    print("[\"" + "\",\n\"".join(sorted(filename_to_issue)) + "\"]")

if __name__ == "__main__":
    main(sys.argv[1:])