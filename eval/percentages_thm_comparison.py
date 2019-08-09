import sys
import common
import table_single_provers

def sumTotalStatus(prover_dict,stat):
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
                if prover == "nitpick":
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
                print(system,quantification,prover,stat)
                ret[prover] += statlist[stat]
    return list(map(lambda kv: (kv[0],len(set(kv[1]))),ret.items()))

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)

    total_THM = sumTotalStatus(prover_dict,"thm_single")
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


if __name__ == "__main__":
    main(sys.argv[1:])