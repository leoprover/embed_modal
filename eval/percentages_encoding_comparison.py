import sys
import common
import table_single_provers

def createSemanticsEncodingdict(prover_dict,prover,stat):
    ret = {}
    for system,qlist in prover_dict[prover].items():
        for quantification,statlist in qlist.items():
            systemprefix = system[:len(system)-3]
            systemsuffix = system[len(system)-3:]
            quantificationprefix = quantification[:len(quantification)-3]
            quantificationsuffix = quantification[len(quantification)-3:]
            semantics = systemprefix + quantificationprefix
            encoding = systemsuffix + quantificationsuffix
            #print(semantics,encoding)
            if semantics not in ret:
                ret[semantics] = {}
            if encoding not in ret[semantics]:
                ret[semantics][encoding] = statlist[stat]
    return ret

def compare_encodings(prover_dict,prover):
    d = createSemanticsEncodingdict(prover_dict,prover,"thm_single")
    systems = ["D","T","S4","S5"]
    quantifications = ["const","cumul","vary"]
    semantics = [s+q for s in systems for q in quantifications]
    encodings = ["semsem","semsyn","synsem","synsyn"]
    maximums = {}
    minimums = {}
    averages = {}
    semsems = {}
    second_maximums = {}
    for sem in semantics:
        if "vary" in sem:
            problemkeys = list(filter(lambda k: k in ["semall","synall"],d[sem].keys()))
        else:
            problemkeys = list(filter(lambda k: k in encodings,d[sem].keys()))
        problems = list(map(lambda k: d[sem][k],problemkeys))
        minimums[sem] = min(list(map(lambda l: len(set(l)),problems)))
        maximums[sem] = max(list(map(lambda l: len(set(l)),problems)))
        averages[sem] = sum(list(map(lambda l: len(set(l)),problems))) / len(problems)
        second_maximums[sem] = sorted(list(map(lambda l: len(set(l)),problems)))[1]
        if "vary" in sem:
            semsems[sem] = len(set(d[sem]["semall"]))
        else:
            semsems[sem] = len(set(d[sem]["semsem"]))
    minimum_vs_semsem = {a:b for (a,b) in map(lambda k: (k,round(100/minimums[k]*(semsems[k]-minimums[k]),1)),minimums.keys())}
    average_vs_semsem = {a:b for (a,b) in map(lambda k: (k,round(100/averages[k]*(semsems[k]-averages[k]),1)),averages.keys())}
    second_maximum_vs_semsem = {a:b for (a,b) in map(lambda k: (k,round(100/second_maximums[k]*(semsems[k]-second_maximums[k]),1)),second_maximums.keys())}

    semantics = ["S5const","S5cumul"]
    S5constlen = len(set(d["S5const"]))
    S5cumullen = len(set(d["S5cumul"]))
    S5Ulen = len(set(d["S5Uconst"]['semsem']))
    maximums = {}
    minimums = {}
    averages = {}
    for sem in semantics:
        problemkeys = list(filter(lambda k: k in encodings,d[sem].keys()))
        problems = list(map(lambda k: d[sem][k],problemkeys))
        minimums[sem] = min(list(map(lambda l: len(set(l)),problems)))
        maximums[sem] = max(list(map(lambda l: len(set(l)),problems)))
        averages[sem] = sum(list(map(lambda l: len(set(l)),problems))) / len(problems)
    print(minimums)
    print(maximums)
    minimum_vs_S5U = {a:b for (a,b) in map(lambda k: (k,round(100.0/minimums[k]*(S5Ulen-minimums[k]),1)),minimums.keys())}
    average_vs_S5U = {a:b for (a,b) in map(lambda k: (k,round(100.0/averages[k]*(S5Ulen-averages[k]),1)),averages.keys())}
    maximum_vs_S5U = {a:b for (a,b) in map(lambda k: (k,round(100.0/maximums[k]*(S5Ulen-maximums[k]),1)),maximums.keys())}
    ret = {
        "minimum_vs_semsem": minimum_vs_semsem,
        "average_vs_semsem": average_vs_semsem,
        "second_maximum_vs_semsem": second_maximum_vs_semsem,
        "minimum_vs_S5U":minimum_vs_S5U,
        "average_vs_S5U":average_vs_S5U,
        "maximum_vs_S5U":maximum_vs_S5U
    }
    return ret



def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)

    for prover in ["leo","satallax"]:
        print(prover)
        ret = compare_encodings(prover_dict,prover)
        print("semsem maximally stronger that other encodings by %")
        print(ret["minimum_vs_semsem"])
        print()
        print("semsem on average stronger that ALL encodings by %")
        print(ret["average_vs_semsem"])
        print()
        print("semsem minimally stronger that other encodings by %")
        print(ret["second_maximum_vs_semsem"])
        print("S5U maximally stronger that other encodings by %")
        print(ret["minimum_vs_S5U"])
        print()
        print("S5U on average stronger that ALL encodings by %")
        print(ret["average_vs_S5U"])
        print()
        print("S5U minimally stronger that other encodings by %")
        print(ret["maximum_vs_S5U"])
        print("-----------------------------------------------------------")
    print("===========================================================")

if __name__ == "__main__":
    main(sys.argv[1:])