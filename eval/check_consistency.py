import sys
import common

def check_consistency_iteration_callback(filename, system, quantification, problem_list, *callback_args):
    ret_dict = callback_args[0]
    szs_dict = common.create_szs_dict_of_configuration(problem_list)
    if      ("Theorem" in szs_dict and "Non-Theorem" in szs_dict) or \
            ("Theorem" in szs_dict and "CounterSatisfiable" in szs_dict) or \
            ("Theorem" in szs_dict and "ContradictoryAxioms" in szs_dict) or \
            ("ContradictoryAxioms" in szs_dict and "Satisfiable" in szs_dict):
        if not filename in ret_dict:
            ret_dict[filename] = []
        ret_dict[filename].append({'system':system,
                                     'quantification':quantification,
                                     'problem_list':problem_list})

def main(csv_file_list):
    print("Checking for consistency of the results.")
    print("Each run (prover,transformationparameters) of a modal configuration (consequence,constants,quantification,system) "
          "is cross-verified with any other run of the modal configuration. "
          "This includes")
    print("Configurations with SZS status Theorem and Non-Theorem on different runs.")
    print("Configurations with SZS status Theorem and CounterSatisfiable on different runs.")
    print("Configurations with SZS status Theorem and ContradictoryAxioms on different runs.")
    print("Configurations with SZS status ContradictoryAxioms and Satisfiable on different runs.")
    print("")
    problem_list = common.accumulate_csv(csv_file_list)
    problem_dict = common.create_dict_from_problems(problem_list,replaceS5U=True)
    filename_to_issue = {}
    common.iterate_dict(problem_dict, check_consistency_iteration_callback, filename_to_issue)
    print("files with issues:",len(filename_to_issue))
    varying_files = set()
    cumulative_files = set()
    constant_files = set()
    for filename in sorted(filename_to_issue):
        issue_list = filename_to_issue[filename]
        print("=================================================================================================================================")
        print(filename)
        for issue_dict in issue_list:
            if issue_dict['system'] == "$modal_system_K":
                continue
            if issue_dict['quantification'] == "$cumulative":
                cumulative_files.add(filename)
            if issue_dict['quantification'] == "$varying":
                varying_files.add(filename)
            if issue_dict['quantification'] == "$constant":
                constant_files.add(filename)
            print("")
            print(common.representation_of_configuration(issue_dict['system'],issue_dict['quantification'],issue_dict['problem_list']))
    print("")
    print("python representation of all " + str(len(filename_to_issue)) + " files with issues:")
    print("[\"" + "\",\n\"".join(sorted(filename_to_issue)) + "\"]")
    print("")
    print("python representation of all " + str(len(varying_files)) + " vary files with issues:")
    print("[\"" + "\",\n\"".join(sorted(varying_files)) + "\"]")
    print("")
    print("python representation of all " + str(len(cumulative_files)) + " cumul files with issues:")
    print("[\"" + "\",\n\"".join(sorted(cumulative_files)) + "\"]")
    print("")
    print("python representation of all " + str(len(constant_files)) + " const files with issues:")
    print("[\"" + "\",\n\"".join(sorted(constant_files)) + "\"]")

if __name__ == "__main__":
    main(sys.argv[1:])