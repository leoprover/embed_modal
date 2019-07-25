import common
import sys
from pathlib import *
import table_single_provers

def createComparisonTable(embedding_prover_list,prover_dict,timekind):
    sb = []
    for system,qlist in sorted(prover_dict["mleancop"].items(),key=table_single_provers.sortSystems):
        for quantification,statlist in sorted(qlist.items(), key=table_single_provers.sortQuantification):
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

            if systemprefix == "S5U":
                sb.append("S5")
            else:
                sb.append(systemprefix)
            sb.append(" & \\multicolumn{1}{l|}{")
            sb.append(quantificationprefix)
            sb.append("} &")
            sb.append("\n")
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
                    numbers.append(-1)
                elif systemprefix == "S5U":
                    numbers.append(-1)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"syn"]["avg_"+timekind+"_single_thm"])


                if quantificationprefix == "vary":
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"])
                elif systemprefix == "S5U":
                    numbers.append(-1)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"sem"]["avg_"+timekind+"_single_thm"])


                if quantificationprefix == "vary":
                    # vary can only be sem
                    numbers.append(-1)
                elif systemprefix == "S5U":
                    numbers.append(-1)
                else:
                    numbers.append(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"syn"]["avg_"+timekind+"_single_thm"])

            minimum = "{0:.1f}".format(round(min(map(lambda n: 9999 if n==-1 else n,numbers)),1))
            numbers = map(lambda n: "{0:.1f}".format(round(n,1)), numbers)
            numbers = (map(lambda n: "\\textbf{"+str(n)+"}" if n==minimum else str(n), numbers))
            numbers = list(map(lambda n: table_single_provers.SEMANTICS_NON_EXISTENT if n=="-1.0" else n, numbers))

            sb.append(numbers[0])
            sb.append(" & ")
            sb.append(numbers[1])
            sb.append(" & ")
            sb.append(numbers[2])
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append(numbers[3])
            sb.append("}\n &")
            sb.append(numbers[4])
            sb.append(" & ")
            sb.append(numbers[5])
            sb.append(" & ")
            sb.append(numbers[6])
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append(numbers[7])
            sb.append("} & ")
            sb.append("\n")

            #optho
            #sb.append("\\multicolumn{1}{c|}{")
            #if systemprefix == "S5U":
            #    sb.append("{0:.1f}".format(round(prover_dict["optho"]["S5"+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"],1)))
            #else:
            #    sb.append("{0:.1f}".format(round(prover_dict["optho"][systemprefix+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"],1)))
            #sb.append("} & ")
            #sb.append("\n")

            #mlean
            sb.append("\\multicolumn{1}{c}{")
            sb.append("{0:.1f}".format(round(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_thm"],1)))
            sb.append("} \\\\")
            sb.append("\n\n")

    return "".join(map(lambda n:str(n),sb))


def createComparisonNitpickTable(prover_dict,timekind):
    sb = []
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


            sb.append(systemprefix)
            sb.append(" & \\multicolumn{1}{l|}{")
            sb.append(quantificationprefix)
            sb.append("} &")
            sb.append("\n")

            numbers = []
            numbers.append(prover_dict["nitpick"][systemprefix+"sem"][quantificationprefix+"sem"]["avg_"+timekind+"_single_csa"])
            if systemprefix != "S5U":
                numbers.append(prover_dict["nitpick"][systemprefix+"syn"][quantificationprefix+"sem"]["avg_"+timekind+"_single_csa"])
            else:
                numbers.append(-1)

            minimum = "{0:.1f}".format(round(min(map(lambda n: 9999 if n==-1 else n,numbers)),1))
            numbers = map(lambda n: "{0:.1f}".format(round(n,1)), numbers)
            numbers = (map(lambda n: "\\textbf{"+str(n)+"}" if n==minimum else str(n), numbers))
            numbers = list(map(lambda n: table_single_provers.SEMANTICS_NON_EXISTENT if n=="-1.0" else n, numbers))

            sb.append(numbers[0])
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append(numbers[1])
            sb.append("}\n &")

            sb.append("\\multicolumn{1}{c}{")
            sb.append("{0:.1f}".format(round(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"]["avg_"+timekind+"_single_csa"],1)))
            sb.append("} \\\\")
            sb.append("\n\n")

    return "".join(map(lambda n:str(n),sb))

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)

    outdir = Path("/home/tg/master_thesis/thesis/tables")

    #wc
    thmoutpath = outdir / "thm_comparison_time_wc"
    with open(thmoutpath,"w+") as fh:
        fh.write(createComparisonTable(["leo","satallax"],prover_dict,"wc"))
    csaoutpath = outdir / "csa_comparison_time_wc"
    with open(csaoutpath,"w+") as fh:
        fh.write(createComparisonNitpickTable(prover_dict,"wc"))

    #cpu
    thmoutpath = outdir / "thm_comparison_time_cpu"
    with open(thmoutpath,"w+") as fh:
        fh.write(createComparisonTable(["leo","satallax"],prover_dict,"cpu"))
    csaoutpath = outdir / "csa_comparison_time_cpu"
    with open(csaoutpath,"w+") as fh:
        fh.write(createComparisonNitpickTable(prover_dict,"cpu"))

if __name__ == "__main__":
    main(sys.argv[1:])