from pathlib import *
import sys
import common
import table_single_provers

def createComparisonTable(embedding_prover_list,prover_dict,szs):
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
            if systemprefix == "S5":
                continue

            sb.append(systemprefix)
            sb.append(" & \\multicolumn{1}{l|}{")
            sb.append(quantificationprefix)
            sb.append("} &")
            sb.append("\n")
            for prover in embedding_prover_list:
                if quantificationprefix == "vary":
                    sb.append(len(set(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"all"][szs+"_single"])))
                elif systemprefix == "S5U":
                    sb.append(len(set(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"sem"][szs+"_single"])))
                else:
                    sb.append(len(set(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"sem"][szs+"_single"])))
                sb.append(" & ")
                if quantificationprefix == "vary":
                    # vary can only be sem
                    sb.append(table_single_provers.SEMANTICS_NON_EXISTENT)
                elif systemprefix == "S5U":
                    sb.append(table_single_provers.SEMANTICS_NON_EXISTENT)
                else:
                    sb.append(len(set(prover_dict[prover][systemprefix+"sem"][quantificationprefix+"syn"][szs+"_single"])))
                sb.append(" & ")
                if quantificationprefix == "vary":
                    sb.append(len(set(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"all"][szs+"_single"])))
                elif systemprefix == "S5U":
                    sb.append(table_single_provers.SEMANTICS_NON_EXISTENT)
                else:
                    sb.append(len(set(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"sem"][szs+"_single"])))
                sb.append(" & \\multicolumn{1}{c|}{")
                if quantificationprefix == "vary":
                    # vary can only be sem
                    sb.append(table_single_provers.SEMANTICS_NON_EXISTENT)
                elif systemprefix == "S5U":
                    sb.append(table_single_provers.SEMANTICS_NON_EXISTENT)
                else:
                    sb.append(len(set(prover_dict[prover][systemprefix+"syn"][quantificationprefix+"syn"][szs+"_single"])))
                sb.append("}\n &")
            sb.append("\\multicolumn{1}{c}{")
            sb.append(len(set(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"][szs+"_single"])))
            sb.append("} \\\\")
            sb.append("\n\n")

    return "".join(map(lambda n:str(n),sb))


def createComparisonNitpickTable(prover_dict):
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

            sb.append(len(set(prover_dict["nitpick"][systemprefix+"sem"][quantificationprefix+"sem"]["csa_single"])))
            sb.append(" & \\multicolumn{1}{c|}{")
            if systemprefix != "S5U":
                sb.append(len(set(prover_dict["nitpick"][systemprefix+"syn"][quantificationprefix+"sem"]["csa_single"])))
            else:
                sb.append("-")
            sb.append("}\n &")

            sb.append("\\multicolumn{1}{c}{")
            sb.append(len(set(prover_dict["mleancop"][systemprefix+"all"][quantificationprefix+"all"]["csa_single"])))
            sb.append("} \\\\")
            sb.append("\n\n")

    return "".join(map(lambda n:str(n),sb))

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)

    outdir = Path("/home/tg/master_thesis/thesis/tables")

    thmoutpath = outdir / "thm_comparison"
    with open(thmoutpath,"w+") as fh:
        fh.write(createComparisonTable(["leo","satallax"],prover_dict,"thm"))
    csaoutpath = outdir / "csa_comparison"
    with open(csaoutpath,"w+") as fh:
        fh.write(createComparisonNitpickTable(prover_dict))


if __name__ == "__main__":
    main(sys.argv[1:])