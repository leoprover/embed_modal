import sys
import common
import table_single_provers
from functools import reduce

def timing_csa(embedding_prover_list,prover_dict,timekind):
    timing = {"nitpick":{},"mleancop":{}}
    for prover in ["nitpick"]:
        timing[prover]["semsem"] = []
        timing[prover]["synsem"] = []
    timing["mleancop"]["all"] = []
    for system,qlist in sorted(prover_dict["mleancop"].items(),key=table_single_provers.sortSystems):
        for quantification,statlist in sorted(qlist.items(), key=table_single_provers.sortQuantification):
            systemprefix = system[:len(system)-3]
            systemsuffix = system[len(system)-3:]
            quantificationprefix = quantification[:len(quantification)-3]
            quantificationsuffix = quantification[len(quantification)-3:]

            # filter
            if quantificationprefix == "vary":
                continue
            if systemprefix == "S5":
                continue
            if systemprefix != "S5U" and quantificationprefix == "cumul":
                continue

            numbers = []
            numbers.append(prover_dict["nitpick"][systemprefix+"sem"][quantificationprefix+"sem"]["avg_"+timekind+"_single_csa"])
            if systemprefix != "S5U":
                numbers.append(prover_dict["nitpick"][systemprefix+"syn"][quantificationprefix+"sem"]["avg_"+timekind+"_single_csa"])
            else:
                numbers.append(-1)

            timing["nitpick"]["semsem"].append(numbers[0])
            timing["nitpick"]["synsem"].append(numbers[1])
            timing["mleancop"]["all"].append(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_csa"])
    return timing

def timing(embedding_prover_list,prover_dict,timekind):
    timing = {"leo":{},"satallax":{},"mleancop":{}}
    for prover in ["leo","satallax"]:
        timing[prover]["semsem"] = []
        timing[prover]["semsyn"] = []
        timing[prover]["synsem"] = []
        timing[prover]["synsyn"] = []
    timing["mleancop"]["all"] = []

    for system,qlist in sorted(prover_dict["mleancop"].items(),key=table_single_provers.sortSystems):
        for quantification,statlist in sorted(qlist.items(), key=table_single_provers.sortQuantification):
            sb = []
            systemprefix = system[:len(system)-3]
            systemsuffix = system[len(system)-3:]
            quantificationprefix = quantification[:len(quantification)-3]
            quantificationsuffix = quantification[len(quantification)-3:]

            # filter
            if systemprefix == "S5U" and quantificationprefix == "vary":
                continue
            if systemprefix == "S5" and (quantificationprefix == "const" or quantificationprefix == "cumul"):
                continue
            #print(system,quantification)

            numbers = []
            for prover in embedding_prover_list:
                if quantificationprefix == "vary":
                    numbers.append(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"])
                elif systemprefix == "S5U":
                    numbers.append(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"sem"]["avg_"+timekind+"_single_thm"])
                else:
                    numbers.append(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"sem"]["avg_"+timekind+"_single_thm"])

                if quantificationprefix == "vary":
                    # vary can only be sem
                    numbers.append(0)
                elif systemprefix == "S5U":
                    numbers.append(0)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"syn"]["avg_"+timekind+"_single_thm"])


                if quantificationprefix == "vary":
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"])
                elif systemprefix == "S5U":
                    numbers.append(0)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"sem"]["avg_"+timekind+"_single_thm"])


                if quantificationprefix == "vary":
                    # vary can only be sem
                    numbers.append(0)
                elif systemprefix == "S5U":
                    numbers.append(0)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"syn"]["avg_"+timekind+"_single_thm"])

            #minimum = "{0:.1f}".format(round(min(map(lambda n: 9999 if n==-1 else n,numbers)),1))
            #numbers = map(lambda n: "{0:.1f}".format(round(n,1)), numbers)
            #numbers = (map(lambda n: "\\textbf{"+str(n)+"}" if n==minimum else str(n), numbers))
            #numbers = (map(lambda n: str(n), numbers))
            #numbers = list(map(lambda n: table_single_provers.SEMANTICS_NON_EXISTENT if n=="-1.0" else n, numbers))


            timing["leo"]["semsem"].append(numbers[0])
            timing["leo"]["semsyn"].append(numbers[1])
            timing["leo"]["synsem"].append(numbers[2])
            timing["leo"]["synsyn"].append(numbers[3])
            timing["satallax"]["semsem"].append(numbers[4])
            timing["satallax"]["semsyn"].append(numbers[5])
            timing["satallax"]["synsem"].append(numbers[6])
            timing["satallax"]["synsyn"].append(numbers[7])
            timing["mleancop"]["all"].append(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"])
    return timing

def calc_perc_of_mleancop(timing,provers):
    perc_of_mleancop = {}
    average = {}
    for prover in provers:
        perc_of_mleancop[prover] = {}
        average[prover] = {}
        for i,enc in enumerate(timing[prover]):
            perc_of_mleancop[prover][enc] = list(map(lambda en: round(100.0/en[1]*(en[0]-en[1]),1),list(zip(timing[prover][enc],timing["mleancop"]["all"]))))
            #newl = reduce(lambda output, current: output if current == 0 else output + [current], timing[prover][enc], [])
            average[prover][enc] = round(sum(list(map(lambda en: 100.0/en[1]*(en[0]-en[1]),list(zip(timing[prover][enc],timing["mleancop"]["all"]))))),1)/len(timing[prover][enc])
    return perc_of_mleancop,average

def calc_average_better_than_other_encodings(timing,timekind):
    for prover in timing.keys():
        for enc in timing[prover]:
            if enc == "semsem":
                continue
        if prover in ["leo"]:
            pass
def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)

    t_wc = timing(["leo","satallax"],prover_dict,"wc")
    t_cpu = timing(["leo","satallax"],prover_dict,"cpu")
    #for p,encs in t_wc.items():
    #    print("---------------------------")
    #    print(p)
    #    for e,l in encs.items():
    #        print(e,l)

    print("#######################################")
    perc_of_mleancop,average = calc_perc_of_mleancop(t_wc,["leo","satallax"])
    for prover in perc_of_mleancop.keys():
        print("---------------------------")
        print(prover)
        print("WC usage percent more than mleancop: encoding / average / [semantics]")
        for enc in perc_of_mleancop[prover]:
            print(enc,average[prover][enc],perc_of_mleancop[prover][enc])
        print("#######################################")
    perc_of_mleancop,average = calc_perc_of_mleancop(t_cpu,["leo","satallax"])
    for prover in perc_of_mleancop.keys():
        print("---------------------------")
        print(prover)
        print("CPU usage percent more than mleancop: encoding / average / [semantics]")
        for enc in perc_of_mleancop[prover]:
            print(enc,average[prover][enc],perc_of_mleancop[prover][enc])

    t_wc = timing_csa(["nitpick"],prover_dict,"wc")
    t_cpu = timing_csa(["nitpick"],prover_dict,"cpu")
    print("#######################################")
    perc_of_mleancop,average = calc_perc_of_mleancop(t_wc,["nitpick"])
    for prover in perc_of_mleancop.keys():
        print("---------------------------")
        print(prover)
        print("WC usage percent more than mleancop: encoding / average / [semantics]")
        for enc in perc_of_mleancop[prover]:
            print(enc,average[prover][enc],perc_of_mleancop[prover][enc])
        print("#######################################")
    perc_of_mleancop,average = calc_perc_of_mleancop(t_cpu,["nitpick"])
    for prover in perc_of_mleancop.keys():
        print("---------------------------")
        print(prover)
        print("CPU usage percent more than mleancop: encoding / average / [semantics]")
        for enc in perc_of_mleancop[prover]:
            print(enc,average[prover][enc],perc_of_mleancop[prover][enc])

if __name__ == "__main__":
    main(sys.argv[1:])