from common import Problem
import sys

def resolve_transformation_abbreviation(transformation_param):
    if transformation_param == "semcumul" :
        return "semantic_monotonic_quantification"
    if transformation_param == "semdecr":
        return "semantic_antimonotonic_quantification"
    if transformation_param == "semmod":
        return "semantic_modality_axiomatization"
    if transformation_param == "semconst":
        return "semantic_constant_quantification"
    if transformation_param == "syncumul":
        return "syntactic_monotonic_quantification"
    if transformation_param == "syndecr":
        return "syntactic_antimonotonic_quantification"
    if transformation_param == "synmod":
        return "syntactic_modality_axiomatization"
    if transformation_param == "synconst":
        return "syntactic_constant_quantification"
    return ""

# pair       id,benchmark,                      benchmark id, solver, solver id, configuration,
# 445515705, qmltp_thf_standard/GSE/GSE044+1.p, 9126303,      leo3,   23515,     local_rigid_constant_D_semdecr_semcumul_semconst_semmod,
# 0          1                                  2             3       4          5
#
# configuration id, status,        cpu time, wallclock time, memory usage, result, expected,                    SZSResult, SZSOutput, SZSStatus
# 329839,           timeout (cpu), 240.41,   80.1139,        8293368.0,    --,     varying cumulative constant, UNK-Non,   Non,       UNK
# 6                 7              8         9               10            11      12                           13         14         15
def read_starexec_csv(filename):
    ret = []
    f = open(filename,'r')
    first = True
    pending = False
    for r in f.readlines():
        #APM009+1.p,leo3 1.3,Theorem,2.6,8.8,$modal_system_S4,$cumulative,$local,$rigid,syntactic_modality_axiomatization syntactic_monotonic_quantification semantic_antimonotonic_quantification
        # empty transformation parameter means valid for all transformation parameters
        if r.strip() == '':
            continue
        if first:
            first = False
            continue
        if "pending submission" in r:
            pending = True
            continue
        row = r.strip().split(',')
        filename = row[1].split('/')[-1]
        solver = row[3]
        config = row[5].split('_')
        consequence = "$" + config[0]
        constants = "$" + config[1]
        quantification = "$" + config[2]
        system = "$modal_system_" + config[3]
        transparams = " ".join(list(map(resolve_transformation_abbreviation, config[4:])))
        szs = row[11]
        if  "timeout" in row[7]:
            szs = "Timeout"
        cpu = row[8]
        wc = row[9]
        p = Problem(filename,solver,szs,wc,cpu,system,quantification,consequence,constants,transparams)
        ret.append(p)
    if pending:
        print("Warning: Not all problems have been run by star exec")
    return ret

def main(starexec_csv):
    problems = read_starexec_csv(starexec_csv)
    new_csv = starexec_csv[:len(starexec_csv)-4] + "_transformed.csv"
    with open(new_csv,"w+") as fh:
        for p in problems:
            fh.write(p.to_csv_line() + "\n")
    print("Wrote " + str(len(problems)) + " entries to " + new_csv)

if __name__ == "__main__":
    main(sys.argv[1])