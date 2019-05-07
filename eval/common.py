# filename, status, time, system, quantification, consequence, constants
import os
from pathlib import Path

problem_directory = "/home/tg/data/QMLTP/qmltp_thf_no_mml"

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
        return self.szs
    def to_string_important(self):
        return ",".join([
            self.filename,
            self.prover,
            self.szs,
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
    f = open(save_file,'r')
    for r in f.readlines():
        #APM009+1.p,leo3 1.3,Theorem,2.6,8.8,$modal_system_S4,$cumulative,$local,$rigid,syntactic_modality_axiomatization syntactic_monotonic_quantification semantic_antimonotonic_quantification
        #print(row)
        if r.strip() == '':
            continue
        row = r.split(',')
        p = common.Problem(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9].split(' '))
        ret.append(p)
    return p

# nested dict with
# filename -> system -> quantification -> prover
def create_dict_from_problems(problem_list):
    ret = {}
    for p in problem_list:
        if not p.filename in ret:
            ret[p.filename] = {}
        if not p.system in ret[p.filename]:
            ret[p.filename][p.system] = {}
        if not p.quantification in ret[p.filename][p.system]:
            ret[p.filename][p.system][p.quantification] = {}
        if not p.prover in ret[p.filename][p.system][p.quantification]:
            ret[p.filename][p.system][p.quantification][p.prover] = []
        ret[p.filename][p.system][p.quantification][p.prover].append(p)
    return ret

def representation_of_problem_list(problem_list):
    ret = ""
    for p in problem_list:


