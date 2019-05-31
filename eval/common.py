import os
import tempfile
import subprocess
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

class Node:
    def __init__(self,rule,content):
        self.rule = rule
        self.content = content
        self.children = []
    def getRule(self):
        return self.rule
    def getContent(self):
        return self.content
    def setContent(self,newContent):
        self.content = newContent
    def setParent(self,parent):
        self.parent = parent
    def getParent(self):
        return self.parent
    def hasChildren(self):
        return len(self.children) != 0
    def getChild(self,n):
        return self.children[n]
    def addChildBack(self,childNode):
        self.children.append(childNode)
        childNode.setParent(self)
    def addChildFront(self,childNode):
        self.children = [childNode] + self.children
    def childCount(self):
        return len(self.children)
    def removeChild(self,n):
        if self.childCount() < n:
            raise Exception("not enough children")
        elif self.childCount() == n:
            self.children = self.children[:n]
        else:
            self.children = self.children[:n] + self.children[n+1:]
    def replaceChild(self,n,newChild):
        self.children[n] = newChild
        newChild.setParent(self)
    def removeChildren(self):
        self.children = []
    def getFirstTerminal(self):
        current = self
        while current.hasChildren():
            current = current.getChild(0)
        return current
    def __str__(self):
        self.strret = ""
        self.dfs(Node.string_helper,self)
        ret = self.strret
        del self.strret
        return ret.strip()
    @staticmethod
    def string_helper(node,root_node):
        if node.rule == "tPTP_input":
            root_node.strret += "\n"
        if not node.hasChildren():
            root_node.strret += node.getContent()
    def __repr__(self):
        return self.__str__()
    # callbacks have the form mycallback(node,args)
    def dfs(self,callback,*callback_args):
        stack = [self]
        while len(stack) != 0:
            current = stack.pop()
            callback(current,*callback_args)
            for child in current.children[::-1]:
                stack.append(child)

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
        system = p.system
        if system == "$modal_system_S5U":
            system = "$modal_system_S5"
        if not system in ret[p.filename]:
            ret[p.filename][system] = {}
        if not p.quantification in ret[p.filename][system]:
            ret[p.filename][system][p.quantification] = []
        ret[p.filename][system][p.quantification].append(p)
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

def create_temp_file(content):
    fd, filename = tempfile.mkstemp()
    os.write(fd,content.encode('utf-8'))
    os.close(fd)
    return filename

def create_semantics(system,quantification,consequence,constants):
    ret = """thf(simple_s5,logic,(
    $modal :=
        [   
            $constants := {0},
            $quantification := {1},
            $consequence := {2},
            $modalities := {3}
        ] 
)).""".format(constants,quantification,consequence,system)
    return ret

def execute_treelimitedrun(bin_treelimitedrun,cmd,wc_limit,cpu_limit):
    newcmd = str(bin_treelimitedrun) + " " + str(cpu_limit+3) + " " + str(wc_limit+3) + " " + cmd
    process = subprocess.Popen(newcmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    process.wait()
    stdout, stderr = process.communicate()
    return stdout, stderr, process.returncode

def parse_cpu(s):
    cpu_start = s.find("FINAL WATCH:") + 12
    cpu_end = s.find("CPU")
    return s[cpu_start:cpu_end].strip()

def parse_wc(s):
    wc_start = s.find("CPU") + 3
    wc_end = s.find("WC")
    return s[wc_start:wc_end].strip()

def parse_szs_status(s):
    status_start = s.find("SZS status")
    if status_start == -1:
        raise OutputNotInterpretable("status_start == -1")
    status_start += 10
    s = s[status_start:].strip()
    status_end = s.find(" ")
    return s[:status_end].strip()

def embed(bin_tree_limited_run, bin_embed,problem,params,semantics,wc_limit,cpu_limit):
    semantics_to_prepend = create_semantics(semantics['system'],semantics['quantification'],semantics['consequence'],semantics['constants'])
    filecontent =  semantics_to_prepend + "\n" + problem

    # create temp files
    filename_original = create_temp_file(filecontent) # TODO pass semantics through cli somehow or strip semantics from problem first
    filename_embedded = create_temp_file("")

    #  execute prover command with tree limited run on temp file
    cmd = str(bin_embed) + " -i " + filename_original + " -o " + filename_embedded
    if len(params) != 0:
        cmd += " -t " + ",".join(params)
    stdout, stderr, returncode = execute_treelimitedrun(bin_tree_limited_run,cmd, wc_limit, cpu_limit)
    str_stdout = stdout.decode('utf-8')
    str_err = stderr.decode('utf-8')
    str_returncode = str(returncode)
    try:
        fd = open(filename_embedded,"r")
        problem_embedded = fd.read()
    except:
        send_data = {}
        send_data['status'] = 'error_output_file_not_readable'
        send_data['raw'] = str_stdout + "\n" + str_err
        send_data['return_code'] = str_returncode
        return send_data
    finally:
        try:
            fd.close()
        except:
            pass

    # delete temp files
    try:
        os.remove(filename_original)
    except:
        pass
    try:
        os.remove(filename_embedded)
    except:
        pass

    # extract information from embedding console
    wc = parse_wc(str_stdout)
    cpu = parse_cpu(str_stdout)

    # success data
    send_data = {}
    send_data['status'] = 'ok'
    send_data['wc'] = wc
    send_data['cpu'] = cpu
    send_data['problem'] = problem
    send_data['raw'] = str_stdout + "\n" + str_err
    send_data['console'] = str_stdout + "\n" + str_err
    send_data['return_code'] = str_returncode
    send_data['semantics'] = semantics
    send_data['embedded_problem'] = problem_embedded
    return send_data

def get_embedding_results_from_problem_file_list(callback, bin_treelimitedrun, bin_embed,
                                                 problem_white_filter, problem_black_filter,
                                                 embedding_wc_limit, embedding_cpu_limit,
                                                 problem_file_list,
                                                 system_list, quantification_list, consequence_list, constants_list,
                                                 transformation_parameter_list):
    for p in problem_file_list:
        with open(p,"r") as fh:
            content = fh.read()
        for system in system_list:
            for quantification in quantification_list:
                for consequence in consequence_list:
                    for constants in constants_list:
                        if problem_white_filter != None and not p.name in problem_white_filter:
                            continue
                        if problem_black_filter != None and p.name in problem_black_filter:
                            continue
                        semantics = {"system":system,"quantification":quantification,"consequence":consequence,"constants":constants}
                        e = embed(bin_treelimitedrun,bin_embed,content,transformation_parameter_list, semantics,
                                    embedding_wc_limit, embedding_cpu_limit)
                        line = [p.name,
                                system,quantification,consequence,constants,
                                " ".join(transformation_parameter_list)]
                        callback(line, e)
