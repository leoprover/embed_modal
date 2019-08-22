import sys
import common
import table_single_provers

def sumTotalStatus(prover_dict,stat,excluded_semantics = [], included_semantics=[]):
    ret = {}
    ret["mleancopcsarestricted"] = []
    for prover in prover_dict.keys():
        if prover not in ret:
            ret[prover] = []
        for system,qlist in prover_dict[prover].items():
            for quantification,statlist in qlist.items():
                systemprefix = system[:len(system)-3]
                systemsuffix = system[len(system)-3:]
                quantificationprefix = quantification[:len(quantification)-3]
                quantificationsuffix = quantification[len(quantification)-3:]
                if prover in ["mleancop","qmltp","optho"] and "unique_compared_to_mleancop" in stat:
                    continue
                if prover in ["mleancop","qmltp","optho"] and "unique_compared_to_other_embedding_provers" in stat:
                    continue
                if prover != "mleancop" and stat == "thm_unique_compared_to_optho":
                    continue
                if prover in ["leo","satallax"]:
                    if quantification not in ["constsem","cumulsem","varyall"]:
                        continue
                    if systemprefix == "S5" and quantificationprefix != "vary":
                        continue
                    if systemsuffix != "sem" and systemprefix in ["D","T","S4","S5"]:
                        continue
                    if systemprefix == "S5U" and systemsuffix != "sem":
                        continue
                if prover == "nitpick" or (prover == "satallax" and stat == "csa_single"):
                    if quantification != "constsem" and systemprefix != "S5U":
                        continue
                    if systemprefix == "S5":
                        continue
                    if systemprefix == "S5U" and systemsuffix != "sem":
                        continue
                    if systemsuffix != "sem":
                        continue
                    if quantificationsuffix == "all":
                        continue
                if prover == "mleancop":
                    if systemprefix == "S5U":
                        continue
                    if quantificationprefix == "const" or (quantificationprefix == "cumul" and systemprefix == "S5"):
                        ret["mleancopcsarestricted"] += statlist[stat]
                if prover == "optho" :
                    pass # contains only the best encoding
                #print(system,quantification,prover,len(set(statlist[stat])))
                #print(system,quantification,prover,stat)
                #ex
                if len(excluded_semantics) != 0:
                    cont = False
                    for ex in excluded_semantics:
                        if systemprefix in ex and quantificationprefix in ex:
                            if True:
                                cont = True
                                #print("ex",systemprefix,quantificationprefix)
                                continue
                    if cont == True:
                        continue
                    else:
                        if len(excluded_semantics) != 0:
                            print(prover,"inc",systemprefix,quantificationprefix)
                #inc
                if len(included_semantics) != 0:
                    cont = True
                    for inc in included_semantics:
                        if systemprefix in inc and quantificationprefix in inc:
                            if not(systemprefix == "S5" and not "S5U" in inc):
                                cont = False
                                #print("inc",systemprefix,quantificationprefix)
                                continue
                    if cont == True:
                        continue
                    else:
                        if len(excluded_semantics) != 0 or len(included_semantics) != 0:
                            #print(prover,"ex",systemprefix,quantificationprefix)
                            pass
                ret[prover] += statlist[stat]

    return list(map(lambda kv: (kv[0],len(set(kv[1]))),ret.items()))

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)

    total_THM = sumTotalStatus(prover_dict,"thm_single")
    total_THM_S5U = sumTotalStatus(prover_dict,"thm_single",[],["S5U+const","S5U+cumul"])
    total_THM_other_than_S5U = sumTotalStatus(prover_dict,"thm_single",["S5U+const","S5U+cumul"],[])
    total_CSA = sumTotalStatus(prover_dict,"csa_single")
    total_U_THM_vs_Embedding = sumTotalStatus(prover_dict,"thm_unique_compared_to_other_embedding_provers")
    total_U_CSA_vs_Embedding = sumTotalStatus(prover_dict,"csa_unique_compared_to_other_embedding_provers")
    total_U_THM_vs_MLeanCop = sumTotalStatus(prover_dict,"thm_unique_compared_to_mleancop")
    total_U_CSA_vs_MleanCop = sumTotalStatus(prover_dict,"csa_unique_compared_to_mleancop")
    print("Total THM for best encoding:")
    print(total_THM)
    print()
    print("Total CSA for best encoding:")
    print(total_CSA)
    print()
    print("Total U THM vs. embedding for best encoding:")
    print(total_U_THM_vs_Embedding)
    print()
    print("Total U CSA vs. embedding for best encoding:")
    print(total_U_CSA_vs_Embedding)
    print()
    print("Total U THM vs. mleancop for best encoding:")
    print(total_U_THM_vs_MLeanCop)
    print()
    print("Total U CSA vs. mleancop for best encoding:")
    print(total_U_CSA_vs_MleanCop)
    print()
    print("Total THM other than S5U for best encoding:")
    print(total_THM_other_than_S5U)
    print()
    print("Total THM S5U for best encoding:")
    print(total_THM_S5U)
    print()

if __name__ == "__main__":
    main(sys.argv[1:])