import common
import sys
from collections import deque
from count_status import count_iteration_callback

def prettifySystem(system):
    return system[len("$modal_system_"):]

def prettifyQuantification(quantification):
    if quantification == "$varying":
        return "vary"
    elif quantification == "$cumulative":
        return "cumul"
    elif quantification == "$decreasing":
        return "decr"
    elif quantification == "$constant":
        return "const"

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
                #prover_dict[prover][system][quant]['gup_single']        = szs_dict.get('GaveUp',[])
                #prover_dict[prover][system][quant]['tmo_single']        = szs_dict.get('Timeout',[]) + szs_dict.get('Unknown',[])
    return prover_dict

def singleUniqueProverComparison(prover_dict, prover_to_compare, with_prover_list):
    """
    creates a dictionary similar to prover_dict that contains problems that were solved by the prover_to_compare alone when compared to all provers in the with_prover_list
    :param prover_dict: prover -> system -> quantification -> problem_list ( as provided by getProverPrimaryValues )
    :param prover_to_compare:
    :param with_prover_list:
    :return:
    """
    ret_dict = {}
    for prover,system_dict in prover_dict.items():

        if prover not in ret_dict:
            ret_dict[prover] = {}
        for system,quant_dict in system_dict.items():
            if system not in ret_dict[prover]:
                ret_dict[prover][system] = {}
            for quant, szs_dict in quant_dict.items():
                if quant not in ret_dict[prover][system]:
                    ret_dict[prover][system][quant] = {}

                # collect all solvable problems with the same filename and semantics from the reference provers
                reference_theorems = set()
                reference_countersatisfiable = set()
                for reference_prover in with_prover_list:
                    if system not in prover_dict[reference_prover] or quant not in prover_dict[reference_prover][system]:
                        pass
                    else:
                        reference_theorems.update(prover_dict[reference_prover][system][quant]['thm_single'])
                        reference_countersatisfiable.update(prover_dict[reference_prover][system][quant]['csa_single'])

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
                ret_dict[prover][system][quant]['thm_unique'] = unique_theorems
                ret_dict[prover][system][quant]['csa_unique'] = unique_countersatisfiable
                ret_dict[prover][system][quant]['sum_unique'] = unique_theorems + unique_countersatisfiable
    return ret_dict

def integrateDict(prover_dict,additional_dict):
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
                    prover_dict[prover][system][quant][r] = val

def compareAllEmbeddingProversWithEmbeddingProvers(prover_dict,embedding_prover_list):
    for embedding_prover in embedding_prover_list:
        reference_provers = set(embedding_prover_list)
        reference_provers.remove(embedding_prover)
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,reference_provers)
        integrateDict(prover_dict,ret_dict)

def compareAllEmbeddingProversWithMleancop(prover_dict,embedding_prover_list):
    for embedding_prover in embedding_prover_list:
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,['mleancop'])
        integrateDict(prover_dict,ret_dict)

def compareAllEmbeddingProversWithQMLTP(prover_dict,embedding_prover_list):
    for embedding_prover in embedding_prover_list:
        ret_dict = singleUniqueProverComparison(prover_dict,embedding_prover,['qmltp'])
        integrateDict(prover_dict,ret_dict)

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
        if p.szs == "CounterSatisfiable" and p.quantification in ["$varying","$cumulative","$decreasing"] and p.system != "$modal_system_S5U":
            continue
        # remove all $modal_system_K from qmltp since its status is invalid
        if p.prover == "qmltp" and p.system == "$modal_system_K":
            continue
        ret.append(p)

        # create distinct system names for syn/sem embedding
        if not p.system.endswith("all"): # was already processed by syn or sem case and is now an all case
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
                p.system += "all"
        """
        # create distinct quantification
        if not p.quantification.endswith("all"):
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
        """
    return ret

def getTableData(problem_list,whitefilter=None,blackfilter=None):
    prepared_problem_list = getPreparedProblems(problem_list,whitefilter,blackfilter)
    for p in prepared_problem_list:
        print (p.filename,p.system,p.quantification,p.prover)
    prover_dict = getProverPrimaryValues(prepared_problem_list)
    compareAllEmbeddingProversWithEmbeddingProvers(prover_dict,['leo','satallax','nitpick'])
    compareAllEmbeddingProversWithMleancop(prover_dict,['leo','satallax','nitpick'])
    compareAllEmbeddingProversWithQMLTP(prover_dict,['leo','satallax','nitpick'])
    return prover_dict

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = getTableData(problem_list)


    """
    for p,slist in prover_dict.items():
        for s,qlist in slist.items():
            if __name__ == '__main__':
                for q,statlist in qlist.items():
                    for problem in statlist['thm_single']:
                        print(problem.filename,p,s,q,problem.transformation)
    """

if __name__ == "__main__":
    main(sys.argv[1:])