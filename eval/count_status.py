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
        if not system in res:
            res[system] = {}
        if not quantification in res[system]:
            res[system][quantification] = {}
        if not p.prover in res[system][quantification]:
            res[system][quantification][p.prover] = {}
        if p.szs not in res[system][quantification][p.prover]:
            res[system][quantification][p.prover][p.szs] = []
        res[system][quantification][p.prover][p.szs].append(p)

def isS5orS5U(p):
    if p.system in ["$modal_system_S5U","$modal_system_S5"]:
        return True

def main(csv_file_list):
    print("Counting all SZS status per quantification and per prover.")
    print("")
    problem_list = common.accumulate_csv(csv_file_list)
    problem_dict = common.create_dict_from_problems(problem_list)
    #quant_dict = common.getQuantificationToProverToProblemListDict(problem_list)

    result_dict = {} # system -> quantification -> prover -> SZS -> (sum)

    common.iterate_dict(problem_dict, count_iteration_callback, result_dict)
    for system,vs in result_dict.items():
        for quant,vq in vs.items():
            for prover,vszs in vq.items():
                for szs,itemlist in vszs.items():
                 print(system,quant,prover,szs,len(itemlist))


if __name__ == "__main__":
    main(sys.argv[1:])