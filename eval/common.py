import os
from pathlib import Path


def get_problem_file_list(problem_directory):
    ret = []
    for dirpath, dirs, files in os.walk(problem_directory):
        for filename in files:
            path = Path(os.path.join(dirpath, filename))
            if path.is_file() and path.suffix == ".p":
                ret.append(path)
    return ret


class Problem:
    def __init__(self,filename,prover,szs,wc,cpu,system,quantification,consequence,constants,transformation):
        self.filename=filename
        self.prover=prover
        self.szs = szs
        self.wc = wc
        self.cpu = cpu
        self.system = system
        self.quantification = quantification
        self.consequence = consequence
        self.constants = constants
        self.transformation = transformation # list of params
    def __repr__(self):
        return ",".join([
            self.filename,
            self.prover,
            self.szs,
            self.system,
            self.quantification,
            self.consequence,
            self.constants,
            " ".join(self.transformation)
        ])
    def to_string_important(self):
        return ",".join([
            self.filename,
            self.prover,
            self.szs,
            self.system,
            self.quantification,
            self.consequence,
            self.constants,
            " ".join(self.transformation)
            ])
    def to_csv_line(self):
        return ",".join([
            self.filename,
            self.prover,
            self.szs,
            self.wc,
            self.cpu,
            self.system,
            self.quantification,
            self.consequence,
            self.constants,
            " ".join([self.transformation])
        ])
    def syntactic_modality_axiomatization(self):
        return "syntactic_modality_axiomatization" in self.transformation
    def syntactic_monotonic_quantification(self):
        return "syntactic_monotonic_quantification" in self.transformation
    def syntactic_antimonotonic_quantification(self):
        return "syntactic_antimonotonic_quantification" in self.transformation
    def semantic_modality_axiomatization(self):
        return "semantic_modality_axiomatization" in self.transformation
    def semantic_monotonic_quantification(self):
        return "semantic_monotonic_quantification" in self.transformation
    def semantic_antimonotonic_quantification(self):
        return "semantic_antimonotonic_quantification" in self.transformation

def read_csv(filename):
    ret = []
    f = open(filename,'r')
    for r in f.readlines():
        #APM009+1.p,leo3 1.3,Theorem,2.6,8.8,$modal_system_S4,$cumulative,$local,$rigid,syntactic_modality_axiomatization syntactic_monotonic_quantification semantic_antimonotonic_quantification
        # empty transformation parameter means valid for all transformation parameters
        if r.strip() == '':
            continue
        row = r.split(',')
        p = Problem(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9].strip().split(' '))
        ret.append(p)
    return ret

def accumulate_csv(filenames):
    ret = []
    for f in filenames:
        ret = ret + read_csv(f)
    return ret

# nested dict with
# filename -> system -> quantification
def create_dict_from_problems(problem_list):
    ret = {}
    for p in problem_list:
        if not p.filename in ret:
            ret[p.filename] = {}
        if not p.system in ret[p.filename]:
            ret[p.filename][p.system] = {}
        if not p.quantification in ret[p.filename][p.system]:
            ret[p.filename][p.system][p.quantification] = []
        ret[p.filename][p.system][p.quantification].append(p)
    return ret

def iterate_dict(problem_dict, callback, *callback_args):
    for filename, system_dict in problem_dict.items():
        for system, quantification_dict in system_dict.items():
            for quantification, problem_list in quantification_dict.items():
                callback(filename, system, quantification, problem_list, *callback_args)

def representation_of_problem_list(problem_list):
    return "\n".join(map(lambda p: p.to_string_important(),problem_list ))

def representation_of_configuration(system,quantification,problem_list):
    ret = system + " " + quantification + "\n"
    ret += "-"*(len(system+quantification)+1) + "\n"
    for p in problem_list:
        ret += "{: <15} {: <15} {: <40}".format(p.prover,p.szs," ".join(p.transformation)) + "\n"
    return ret

def create_szs_dict_of_configuration(problem_list):
    ret = {}
    for p in problem_list:
        if p.szs not in ret:
            ret[p.szs] = []
        ret[p.szs].append(p)
    return ret

