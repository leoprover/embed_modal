import common
import sys
from collections import deque
from count_status import count_iteration_callback
from pathlib import *

SEMANTICS_N_A = "$\\dagger$"
SEMANTICS_WITH_CONSTSEM = "$\\ddagger$"

def prettifySystem(system):
    if system.startswith("$modal_system_"):
        return system[len("$modal_system_"):]
    else:
        return system

def prettifyQuantification(quantification):
    if quantification == "$varying":
        return "vary"
    elif quantification == "$cumulative":
        return "cumul"
    elif quantification == "$decreasing":
        return "decr"
    elif quantification == "$constant":
        return "const"
    else:
        return quantification

def prettifyStatus(status):
    if status == "Theorem":
        return "THM"
    elif status == "CounterSatisfiable":
        return "CSA"
    elif status == "GaveUp":
        return "GUP"
    elif status == "Timeout":
        return "TMO"
    elif status == "Satisfiable":
        return "SAT"

def prettifyProver(prover):
    if "leo" in prover.lower():
        return "leo"
    elif "satallax" in prover.lower():
        return "satallax"
    elif "nitpick" in prover.lower() or "isabelle" in prover.lower():
        return "nitpick"
    elif "mleancop" in prover.lower():
        return "mleancop"
    elif "qmltp" in prover.lower():
        return "qmltp"

# returns prover -> system -> quantification -> [sum,problem_list]
# system and quantification have suffix sem,syn,all
# all has problems, that are semantically the same e.g. const_sem, const_sym
# hence avg. cpu/wc is on all thm/csa even on (semantically) duplicate problems
def getProverPrimaryValues(problem_list):
    problem_dict = common.create_dict_from_problems(problem_list)
    count_dict = {}
    common.iterate_dict(problem_dict, count_iteration_callback, count_dict)
    prover_dict = {}
    for system,vs in count_dict.items():
        for quant,vq in vs.items():
            for prover, szs_dict in vq.items():
                if not prover in prover_dict:
                    prover_dict[prover] = {}
                if not system in prover_dict[prover]:
                    prover_dict[prover][system] = {}
                if not quant in prover_dict[prover][system]:
                    prover_dict[prover][system][quant] = {}
                prover_dict[prover][system][quant]['thm_single']            = szs_dict.get('Theorem',[])
                prover_dict[prover][system][quant]['csa_single']            = szs_dict.get('CounterSatisfiable',[])
                prover_dict[prover][system][quant]['sum_single']            = prover_dict[prover][system][quant]['thm_single'] + prover_dict[prover][system][quant]['csa_single']
                if prover_dict[prover][system][quant]['sum_single']:
                    prover_dict[prover][system][quant]['avg_cpu_single']    = sum(map(lambda p: p.cpu,prover_dict[prover][system][quant]['sum_single'])) \
                                                                              / len(prover_dict[prover][system][quant]['sum_single'])
                else:
                    prover_dict[prover][system][quant]['avg_cpu_single']    = 0.0
                if prover_dict[prover][system][quant]['sum_single']:
                    prover_dict[prover][system][quant]['avg_wc_single']     = sum(map(lambda p: p.wc,prover_dict[prover][system][quant]['sum_single'])) \
                                                                              / len(prover_dict[prover][system][quant]['sum_single'])
                else:
                    prover_dict[prover][system][quant]['avg_wc_single']     = 0.0
                #prover_dict[prover][system][quant]['sat_single']        = szs_dict.get("Satisfiable",[])
                prover_dict[prover][system][quant]['gup_single']        = szs_dict.get('GaveUp',[])
                prover_dict[prover][system][quant]['tmo_single']        = szs_dict.get('Timeout',[]) + szs_dict.get('Unknown',[])
    return prover_dict

def singleUniqueProverComparison(prover_dict, prover_to_compare, with_prover_list, system_manipulator={},quant_manipulator={}):
    """
    creates a dictionary similar to prover_dict that contains problems that were solved by the prover_to_compare alone when compared to all provers in the with_prover_list
    system_manipulator and quant_manipulator yield a reference prover as key with a function as value that takes a system/quant string and replaces it as needed
    e.g. when comparing a embedding prover with all its embedding variants against mleancop that only has one (all suffix)
    :param prover_dict: prover -> system -> quantification -> problem_list ( as provided by getProverPrimaryValues )
    :param prover_to_compare:
    :param with_prover_list:
    :return:
    """
    ret_dict = {}
    ret_dict[prover_to_compare] = {}
    for system,quant_dict in prover_dict[prover_to_compare].items():
        if system not in ret_dict[prover_to_compare]:
            ret_dict[prover_to_compare][system] = {}
        for quant, szs_dict in quant_dict.items():
            if quant not in ret_dict[prover_to_compare][system]:
                ret_dict[prover_to_compare][system][quant] = {}

            # collect all solvable problems with the same filename and semantics from the reference provers
            reference_theorems = set()
            reference_countersatisfiable = set()
            for reference_prover in with_prover_list:
                reference_system = system
                reference_quant = quant
                if reference_prover in system_manipulator:
                    reference_system = system_manipulator[reference_prover](reference_system)
                if reference_prover in quant_manipulator:
                    reference_quant = quant_manipulator[reference_prover](reference_quant)
                if reference_system not in prover_dict[reference_prover] or reference_quant not in prover_dict[reference_prover][reference_system]:
                    #print("passed",reference_prover,reference_system,reference_quant,"for original",system,quant)
                    pass
                else:
                    #print("updaaa",reference_prover,reference_system,reference_quant,"for original",system,quant)
                    #print("update",prover_dict[reference_prover][reference_system][reference_quant]['thm_single'])
                    reference_theorems.update(prover_dict[reference_prover][reference_system][reference_quant]['thm_single'])
                    reference_countersatisfiable.update(prover_dict[reference_prover][reference_system][reference_quant]['csa_single'])

            # compare reference theorems / countersatisfiable with selected prover
            unique_theorems = []
            unique_countersatisfiable = []
            if system not in prover_dict[prover_to_compare] or quant not in prover_dict[prover_to_compare][system]:
                pass
            else:
                for p_theorem in prover_dict[prover_to_compare][system][quant]['thm_single']:
                    if not p_theorem in reference_theorems:
                        unique_theorems.append(p_theorem)
                for p_countersatisfiable in prover_dict[prover_to_compare][system][quant]['csa_single']:
                    if not p_countersatisfiable in reference_countersatisfiable:
                        unique_countersatisfiable.append(p_countersatisfiable)
            #print(system,quant)
            #print("ref",reference_theorems)
            #print("uni",unique_theorems)
            ret_dict[prover_to_compare][system][quant]['thm_unique'] = unique_theorems
            ret_dict[prover_to_compare][system][quant]['csa_unique'] = unique_countersatisfiable
            ret_dict[prover_to_compare][system][quant]['sum_unique'] = unique_theorems + unique_countersatisfiable
    return ret_dict

def integrateDict(prover_dict,additional_dict,result_name_manipulator):
    """
    Integrates additional_dict into prover_dict
    :param prover_dict:
    :param additional_dict:
    :return:
    """
    for prover,system_dict in additional_dict.items():
        for system,quant_dict in system_dict.items():
            for quant, result_dict in quant_dict.items():
                for r,val in result_dict.items():
                    prover_dict[prover][system][quant][result_name_manipulator[r]] = val

def compareAllEmbeddingProversWithEmbeddingProvers(prover_dict,embedding_prover_list):
    """
    Compares one embedding prover with all other embedding provers on the same semantics (syn/sem sensitive)
    :param prover_dict:
    :param embedding_prover_list:
    :return:
    """
    for embedding_prover in embedding_prover_list:
        reference_provers = set(embedding_prover_list)
        reference_provers.remove(embedding_prover)
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,reference_provers)
        #print(ret_dict)
        result_name_manipulator = {
            'thm_unique':'thm_unique_compared_to_other_embedding_provers',
            'csa_unique':'csa_unique_compared_to_other_embedding_provers',
            'sum_unique':'sum_unique_compared_to_other_embedding_provers'
        }
        integrateDict(prover_dict,ret_dict,result_name_manipulator)

def nonEmbeddingReferenceSystem(system):
    return system[:len(system)-3] + "all"

def nonEmbeddingReferenceQuant(quant):
    return quant[:len(quant)-3] + "all"

def compareAllEmbeddingProversWithMleancop(prover_dict,embedding_prover_list):
    """
    Compares every single embedding prover to mleancop
    :param prover_dict:
    :param embedding_prover_list:
    :return:
    """
    for embedding_prover in embedding_prover_list:
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,['mleancop'],
                                                system_manipulator={'mleancop':nonEmbeddingReferenceSystem},
                                                quant_manipulator={'mleancop':nonEmbeddingReferenceQuant})
        result_name_manipulator = {
            'thm_unique':'thm_unique_compared_to_mleancop',
            'csa_unique':'csa_unique_compared_to_mleancop',
            'sum_unique':'sum_unique_compared_to_mleancop'
        }
        integrateDict(prover_dict,ret_dict,result_name_manipulator)

def compareAllEmbeddingProversWithQMLTP(prover_dict,embedding_prover_list):
    """
    Compares every single embedding prover to QMLTP
    :param prover_dict:
    :param embedding_prover_list:
    :return:
    """
    for embedding_prover in embedding_prover_list:
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,['qmltp'])
        result_name_manipulator = {
            'thm_unique':'thm_unique_compared_to_qmltp',
            'csa_unique':'csa_unique_compared_to_qmltp',
            'sum_unique':'sum_unique_compared_to_qmltp'
        }
        integrateDict(prover_dict,ret_dict,result_name_manipulator)

def getPreparedProblems(problem_list,whitefilter=None,blackfilter=None):
    ret = []
    qu = deque(problem_list)
    while qu:
        p = qu.popleft()
        #filter
        if whitefilter and not whitefilter(p):
            continue
        if blackfilter and blackfilter(p):
            continue

        # adjust prover name
        p.prover = prettifyProver(p.prover)

        # remove all CSA for vary,cumul,decr quantification on non-S5U systems due to completeness issue
        # S5U vary is assumed not to be in the dataset
        if \
                p.szs == "CounterSatisfiable" and \
                p.quantification in ["$varying","$cumulative","$decreasing"] and \
                p.system != "$modal_system_S5U" and \
                not p.prover in ["qmltp","mleancop"]:
            continue

        # remove all $modal_system_K since its qmltp status is invalid and mleancop does not support this
        if p.system == "$modal_system_K":
            continue

        # remove all $decreasing
        if p.quantification == "$decreasing":
            continue

        # create distinct system names for syn/sem embedding
        if not (p.system.endswith("all") or p.system.endswith("sem") or p.system.endswith("syn")): # was already processed by syn or sem case and is now an all case
            p.system = prettifySystem(p.system)
            if p.syntactic_modality_axiomatization(): # for modal embeddings
                pnew = p.returnCopy()
                pnew.system += "all"
                qu.append(pnew)
                p.system += "syn"
            elif p.semantic_modality_axiomatization(): # for modal embeddings
                pnew = p.returnCopy()
                pnew.system += "all"
                qu.append(pnew)
                p.system += "sem"
            else: # for qmltp / mleancop
                if p.system == "S5":
                    pnew = p.returnCopy()
                    pnew.system = "S5Uall"
                    qu.append(pnew)
                p.system += "all"

        # create distinct quantification
        if not (p.quantification.endswith("all") or p.quantification.endswith("sem") or p.quantification.endswith("syn")) :
            p.quantification = prettifyQuantification(p.quantification)
            if p.quantification == "const" and p.syntactic_constant_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "syn"
            elif p.quantification == "const" and p.semantic_constant_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "sem"
            elif p.quantification == "cumul" and p.syntactic_cumulative_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "syn"
            elif p.quantification == "cumul" and p.semantic_cumulative_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "sem"
            elif p.quantification == "decr" and p.syntactic_decreasing_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "syn"
            elif p.quantification == "decr" and p.semantic_decreasing_quantification():
                pnew = p.returnCopy()
                pnew.quantification += "all"
                qu.append(pnew)
                p.quantification += "sem"
            else: # qmltp/mleancop
                p.quantification += "all"

        ret.append(p)

    return ret

def getTableData(problem_list,whitefilter=None,blackfilter=None):
    prepared_problem_list = getPreparedProblems(problem_list,whitefilter,blackfilter)
    #for p in prepared_problem_list:
    #    #print (p.filename,p.system,p.quantification,p.prover)
    prover_dict = getProverPrimaryValues(prepared_problem_list)
    compareAllEmbeddingProversWithEmbeddingProvers(prover_dict,['leo','satallax','nitpick'])
    compareAllEmbeddingProversWithMleancop(prover_dict,['leo','satallax','nitpick'])
    #compareAllEmbeddingProversWithQMLTP(prover_dict,['leo','satallax','nitpick'])
    return prover_dict

def getMLeanCopTable(single_prover_dict):
    sb = []
    for system,qlist in single_prover_dict.items():
        for quantification,statlist in qlist.items():
            systemprefix = system[:len(system)-3]
            systemsuffix = system[len(system)-3:]
            quantificationprefix = quantification[:len(quantification)-3]
            quantificationsuffix = quantification[len(quantification)-3:]
            if (quantificationsuffix == "all" and systemsuffix != "all") or (quantificationsuffix != "all" and systemsuffix == "all"):
                continue
            sb.append(systemprefix)
            sb.append(" & \\multicolumn{1}{l|}{")
            sb.append(quantificationprefix)
            sb.append("} & ")
            sb.append("\n")
            sb.append(len(set(statlist['sum_single'])))
            sb.append(" & ")
            sb.append(len(set(statlist['thm_single'])))
            sb.append(" & ")
            sb.append(" & ")
            sb.append(len(set(statlist['gup_single'])))
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append(len(set(statlist['tmo_single'])))
            sb.append("} & ")
            sb.append("\n")
            sb.append("{0:.1f}".format(round(statlist['avg_cpu_single'],1)))
            sb.append(" & \\multicolumn{1}{c}{")
            sb.append("{0:.1f}".format(round(statlist['avg_wc_single'],1)))
            sb.append("} ")
            sb.append("\\\\")
            sb.append("\n\n")
    return "".join(map(lambda n:str(n),sb))

def sortSystems(item):
    key=item[0]
    precedence = 0
    if key.startswith("D"): precedence = 10
    elif key.startswith("T"): precedence = 20
    elif key.startswith("S4"): precedence = 30
    elif key.startswith("S5U"): precedence = 50
    elif key.startswith("S5"): precedence = 40
    else:
        raise Exception("no system")
    if key.endswith("sem"):
        precedence +=1
    elif key.endswith("syn"):
        precedence +=2
    elif key.endswith("all"):
        precedence +=3
    else:
        raise Exception("nosynsem")
    return precedence

def sortQuantification(item):
    key=item[0]
    precedence = 0
    if key.startswith("const"): precedence = 10
    elif key.startswith("cumul"): precedence = 20
    elif key.startswith("decr"): precedence = 30
    elif key.startswith("vary"): precedence = 40
    else:
        raise Exception("no system")
    if key.endswith("sem"):
        precedence +=1
    elif key.endswith("syn"):
        precedence +=2
    elif key.endswith("all"):
        precedence +=3
    else:
        raise Exception("nosynsem")
    return precedence

def getEmbeddingProverTable(single_prover_dict):
    """
    Returns the table contents of the performance of a single embedding prover
    :param status:
    :param single_prover_dict: contains systems
    :return:
    """
    """
sys & \multicolumn{1}{c|}{quant} &
$\Sigma$ & THM & CSA & GUP & \multicolumn{1}{c|}{TMO} &
CPU & \multicolumn{1}{c|}{WC} &
$\Sigma$ & THM & \multicolumn{1}{c|}{CSA}  &
$\Sigma$ & THM & \multicolumn{1}{c}{CSA} \ \
    \hline
"""
    sb = []
    for system,qlist in sorted(single_prover_dict.items(),key=sortSystems):
        for quantification,statlist in sorted(qlist.items(), key=sortQuantification):
            systemprefix = system[:len(system)-3]
            systemsuffix = system[len(system)-3:]
            quantificationprefix = quantification[:len(quantification)-3]
            quantificationsuffix = quantification[len(quantification)-3:]

            # filter
            if systemsuffix != "all" and quantificationsuffix == "all" and quantificationprefix != "vary":
                continue
            if systemsuffix == "all" and quantificationsuffix != "all":
                continue
            if system == "S5Uall":
                continue

            if system == "S5Usem":
                sb.append("S5U")
            else:
                sb.append(systemprefix + "\\textsubscript{" + systemsuffix + "}")
            sb.append(" & \\multicolumn{1}{l|}{")
            if system == "S5Usem":
                sb.append(quantificationprefix)
            elif quantification == "varyall":
                sb.append(quantificationprefix)
            else:
                sb.append(quantificationprefix + "\\textsubscript{" + quantificationsuffix + "}")
            sb.append("} & ")
            sb.append("\n")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['sum_single'])))
            elif quantification == "constall":
                # THM_all + CSA_sem
                sb.append(len(set(statlist['thm_single'])) + len(set(qlist["constsem"]["csa_single"])))
                #sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(len(set(statlist['thm_single'])))
            sb.append(" & ")
            sb.append(len(set(statlist['thm_single'])))
            sb.append(" & ")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['csa_single'])))
            elif quantification == "constall":
                # CSA_sem
                sb.append(len(set(qlist["constsem"]["csa_single"])))
                sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(SEMANTICS_N_A)
            sb.append(" & ")
            sb.append(len(set(statlist['gup_single'])))
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append(len(set(statlist['tmo_single'])))
            sb.append("} & ")
            sb.append("\n")
            sb.append("{0:.1f}".format(round(statlist['avg_cpu_single'],1)))
            sb.append(" & \\multicolumn{1}{c|}{")
            sb.append("{0:.1f}".format(round(statlist['avg_wc_single'],1)))
            sb.append("} & ")
            sb.append("\n")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['sum_unique_compared_to_other_embedding_provers'])))
            elif quantification == "constall":
                # THM_all + CSA_sem
                sb.append(len(set(statlist['thm_unique_compared_to_other_embedding_provers'])) + len(set(qlist["constsem"]["csa_unique_compared_to_other_embedding_provers"])))
                #sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(len(set(statlist['thm_unique_compared_to_other_embedding_provers'])))
            sb.append(" & ")
            sb.append(len(set(statlist['thm_unique_compared_to_other_embedding_provers'])))
            sb.append(" & \\multicolumn{1}{c|}{")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['csa_unique_compared_to_other_embedding_provers'])))
            elif quantification == "constall":
                # CSA_sem
                sb.append(len(set(qlist["constsem"]["csa_unique_compared_to_other_embedding_provers"])))
                sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(SEMANTICS_N_A)
            sb.append("} & ")
            sb.append("\n")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['sum_unique_compared_to_mleancop'])))
            elif quantification == "constall":
                # THM_all + CSA_sem
                sb.append(len(set(statlist['thm_unique_compared_to_mleancop'])) + len(set(qlist["constsem"]["csa_unique_compared_to_mleancop"])))
                #sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(len(set(statlist['thm_unique_compared_to_mleancop'])))
            sb.append(" & ")
            sb.append(len(set(statlist['thm_unique_compared_to_mleancop'])))
            sb.append(" & \\multicolumn{1}{c}{")
            if quantification == "constsem" or system == "S5Usem":
                sb.append(len(set(statlist['csa_unique_compared_to_mleancop'])))
            elif quantification == "constall":
                # CSA_sem
                sb.append(len(set(qlist["constsem"]["csa_unique_compared_to_mleancop"])))
                sb.append("\\textsuperscript{" + SEMANTICS_WITH_CONSTSEM + "}")
            else:
                sb.append(SEMANTICS_N_A)
            sb.append("} \\\\")
            sb.append("\n\n")
    return "".join(map(lambda n:str(n),sb))

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = getTableData(problem_list)

    outdir = Path("/home/tg/master_thesis/thesis/tables")
    for prover,system_list in prover_dict.items():
        outpath = outdir / (prover+"_single")
        if prover in ["leo","satallax","nitpick"]:
            with open(outpath,"w+") as fh:
                fh.write(getEmbeddingProverTable(system_list))
        #if prover == "nitpick":
        #    with open(outpath,"w+") as fh:
        #        fh.write(getCounterModelFinderProverTable(system_list))
        if prover == "mleancop":
            with open(outpath,"w+") as fh:
                fh.write(getMLeanCopTable(system_list))


    """
    for p,slist in prover_dict.items():
        for s,qlist in slist.items():
            if __name__ == '__main__':
                for q,statlist in qlist.items():
                    print("thm_single",p,s,q,len(statlist['thm_single']))
                    print(p,s,q,len(statlist['thm_single']))
                    #print(p,s,q,len(statlist['thm_single']))
                    #for problem in statlist['thm_single']:
                    #    print(problem.filename,p,s,q,problem.transformation)
    """

if __name__ == "__main__":
    main(sys.argv[1:])