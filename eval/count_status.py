import sys
import common

def count_iteration_callback(filename, system, quantification, problem_list, *callback_args, **callback_kwargs):
    res = callback_args[0]
    if not "whitefilter" in callback_kwargs:
        callback_kwargs["whitefilter"] = None
    whitefilter = callback_kwargs["whitefilter"]
    if not "blackfilter" in callback_kwargs:
        callback_kwargs["blackfilter"] = None
    blackfilter = callback_kwargs["blackfilter"]
    for p in problem_list:
        if whitefilter and not whitefilter(p):
            continue
        if blackfilter and blackfilter(p):
            continue
        if not quantification in res:
            res[quantification] = {}
        if not system in res[quantification]:
            res[quantification][system] = {}
        if not p.prover in res[quantification][system]:
            res[quantification][system][p.prover] = {}
        if p.szs not in res[quantification][system][p.prover]:
            res[quantification][system][p.prover][p.szs] = 0
        res[quantification][system][p.prover][p.szs] += 1

def isS5orS5U(p):
    if p.system in ["$modal_system_S5U","$modal_system_S5"]:
        return True

def main(csv_file_list):
    print("Counting all SZS status per quantification and per prover.")
    print("")
    problem_list = common.accumulate_csv(csv_file_list)
    problem_dict = common.create_dict_from_problems(problem_list)
    #quant_dict = common.getQuantificationToProverToProblemListDict(problem_list)

    result_dict = {} # quantification -> system -> prover -> SZS -> (sum)

    common.iterate_dict(problem_dict, count_iteration_callback, result_dict,whitefilter=isS5orS5U)
    for quant,vq in result_dict.items():
        for system,vs in vq.items():
            for prover,vszs in vs.items():
                for szs,numberofitems in vszs.items():
                 print(quant,system,prover,szs,numberofitems)


if __name__ == "__main__":
    main(sys.argv[1:])