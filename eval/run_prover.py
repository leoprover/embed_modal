import subprocess
import pathlib
import tempfile
import os
import common
import csv
import sys
from extract_qmltp_info import extract_qmltp_info_from_problem_to_dict,Problem
from interesting_problems import cumul_interesting_problems

class OutputNotInterpretable(Exception):
    def __init__(self, msg):
        self.msg = msg
    def __str__(self):
        return repr(self.msg)


bin_treelimitedrun = "/home/tg/eval/TreeLimitedRun"
bin_embed = "java -jar /home/tg/embed_modal/embed/target/embed-1.0-SNAPSHOT-shaded.jar"

#-consequences local -constants rigid - systems K -domains varying,cumulative -diroutput joint -i data/QMLTP/qmltp_thf/APM/APM010+1.p -o embed_modal/qmltp_embedded/APM010+1.p


def run_local_prover_helper(prover_command, problem, wc_limit, cpu_limit):
    # create temp file
    filename = create_temp_file(problem)

    #  execute prover command with tree limited run on temp file
    cmd = prover_command.replace("%s",filename).replace("%d",str(wc_limit))
    stdout,stderr,returncode = execute_treelimitedrun(cmd, wc_limit, cpu_limit)

    # delete temp file
    try:
        os.remove(filename)
    except:
        pass

    # extract information from prover result
    str_stdout = stdout.decode('utf-8')
    str_err = stderr.decode('utf-8')
    str_returncode = str(returncode)
    #print(stdout)
    #print(stderr)

    try:
        szs_status = parse_szs_status(str_stdout)
        wc = parse_wc(str_stdout)
        cpu = parse_cpu(str_stdout)
    except:
        szs_status = "TimeoutExecution"
        wc = str(prover_wc_limit)
        cpu = str(prover_cpu_limit)

    # success data
    send_data = {}
    send_data['status'] = 'ok'
    send_data['problem'] = problem
    send_data['szs_status'] = szs_status
    send_data['wc'] = wc
    send_data['cpu'] = cpu
    send_data['raw'] = str_stdout + "\n" + str_err
    send_data['return_code'] = str_returncode
    return send_data


def embed(problem,params,semantics,wc_limit,cpu_limit):
    semantics_to_prepend = create_semantics(semantics['system'],semantics['quantification'],semantics['consequence'],semantics['constants'])


    filecontent =  semantics_to_prepend + "\n" + problem

    # create temp files
    filename_original = create_temp_file(filecontent) # TODO pass semantics through cli somehow or strip semantics from problem first
    filename_embedded = create_temp_file("")

    #  execute prover command with tree limited run on temp file
    cmd = str(bin_embed) + " -i " + filename_original + " -o " + filename_embedded
    if len(params) != 0:
        cmd += " -t " + ",".join(params)
    stdout, stderr, returncode = execute_treelimitedrun(cmd, wc_limit, cpu_limit)
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

##############################
# here come the helper methods
##############################

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

def execute_treelimitedrun(cmd,wc_limit,cpu_limit):
    newcmd = str(bin_treelimitedrun) + " " + str(cpu_limit+3) + " " + str(wc_limit+3) + " " + cmd
    process = subprocess.Popen(newcmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
    process.wait()
    stdout, stderr = process.communicate()
    return stdout, stderr, process.returncode

def create_temp_file(content):
    fd, filename = tempfile.mkstemp()
    os.write(fd,content.encode('utf-8'))
    os.close(fd)
    return filename

def run_embedding_and_prover(problem,embedding_parameters, embedding_semantics, embedding_wc_limit, embedding_cpu_limit,
                             prover_command, prover_wc_limit, prover_cpu_limit):
    embedding_ret = embed(problem, embedding_parameters, embedding_semantics, embedding_wc_limit, embedding_cpu_limit)
    #print(embedding_ret)
    prover_ret = run_local_prover_helper(prover_command, embedding_ret['embedded_problem'],prover_wc_limit, prover_cpu_limit)
    #print(prover_ret)
    return embedding_ret, prover_ret

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

# filename, prover, status, wc_time, cpu_time, system, quantification, consequence, constants, transformation_parameter_list
def get_proving_results_from_problem_file_list(callback, prover_name, prover_command, prover_wc_limit, prover_cpu_limit,
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
                        if not p.name in problem_white_filter:
                            continue
                        if already_processed(p.name,system,quantification):
                            continue
                        semantics = {"system":system,"quantification":quantification,"consequence":consequence,"constants":constants}
                        e,r = run_embedding_and_prover(content,transformation_parameter_list, semantics,
                                                              embedding_wc_limit, embedding_cpu_limit,
                                                              prover_command, prover_wc_limit, prover_cpu_limit)
                        line = [p.name, prover_name, r['szs_status'],r['wc'],r['cpu'],
                                         system,quantification,consequence,constants,
                                         " ".join(transformation_parameter_list)]
                        callback(line, e,r)

fhs = None
def debug_print_line(line,e,r):
    fhs.write(",".join(line)+"\n")
    fhs.flush()
    print(",".join(line))

    problem_status = extract_qmltp_info_from_problem_to_dict(e['problem'])
    system = e['semantics']['system']
    quant = e['semantics']['quantification']
    qmltp_szs_status = problem_status[system][quant]
    prove_status = r['szs_status']
    print("qmltp szs: ",qmltp_szs_status)
    print("prover szs: ",prove_status)
    if \
            prove_status != "TimeoutExecution" and \
            prove_status != "Timeout" and \
            prove_status != "GaveUp" and \
            qmltp_szs_status in ["Theorem","CounterSatisfiable"] and \
            qmltp_szs_status != prove_status:
        print("### ERROR: status does not match!")
        print("### embedding stdout")
        print(e['raw'].replace("\\n","\n"))
        print("### prover stdout")
        print(r['raw'].replace("\\n","\n"))
        print("### original problem")
        print(e['problem'])
        print("### embedded problem")
        print(e['embedded_problem'])
    print("====================================================================================")



processed_problems = None
def get_processed_problems():
    f = open(save_file,'r')
    processed_problems = {}
    for r in f.readlines():
        #APM009+1.p,leo3 1.3,Theorem,2.6,8.8,$modal_system_S4,$cumulative,$local,$rigid,syntactic_modality_axiomatization syntactic_monotonic_quantification semantic_antimonotonic_quantification
        #print(row)
        if r.strip() == '':
            continue
        row = r.split(',')
        p = Problem(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9].split(' '))
        filename = row[0]
        system = row[5]
        quantification=row[6]
        if not filename in processed_problems:
            processed_problems[filename] = {}
        if not system in processed_problems[filename]:
            processed_problems[filename][system] = {}
        if not quantification in processed_problems[filename][system]:
            processed_problems[filename][system][quantification] = p
    f.close()
    return processed_problems

def already_processed(filename,system,quantification):
    if not filename in processed_problems:
        return False
    if not system in processed_problems[filename]:
        return False
    if not quantification in processed_problems[filename][system]:
        return False
    return True

###############################################################
# prover settings
###############################################################

prover_name = "leo3 1.3"
prover_command = "leo3 %s -t %d"
prover_wc_limit = 10
prover_cpu_limit = 10
embedding_wc_limit = 60
embedding_cpu_limit = 60
#prover_wc_limit = 6
#prover_cpu_limit = 6
#embedding_wc_limit = 6
#embedding_cpu_limit = 6
#problem_file_list = common.get_problem_file_list(common.problem_directory)[:1]
problem_file_list = common.get_problem_file_list(common.problem_directory)

###############################################################
# embedding settings
###############################################################

system_list = [
    #"$modal_system_K"#,
    #"$modal_system_D",
    #"$modal_system_T",
    "$modal_system_S4"
    #"$modal_system_S5"
]
quantification_list = [
    #"$constant"#,
    #"$varying",
    "$cumulative",
    #"$decreasing"
]
consequence_list = [
    "$local"#,
    #"$global"
]
constants_list = [
    "$rigid"
]
transformation_parameter_list = [
    "semantic_modality_axiomatization",
    #"semantic_monotonic_quantification",
    #"semantic_antimonotonic_quantification"
    #"syntactic_modality_axiomatization",
    "syntactic_monotonic_quantification",
    "syntactic_antimonotonic_quantification"
]
#semantic_modality_axiomatization semantic_monotonic_quantification semantic_antimonotonic_quantification
#transformation_parameter_list = [ # old params
#    "semantical_modality_axiomatization"
#]
###############################################################
# filter for problems
###############################################################

problem_white_filter = None
problem_white_filter = cumul_interesting_problems

###############################################################
# output file
###############################################################

save_file = "/home/tg/embed_modal/eval/output.csv"
# file -> system -> quantsemn

###############################################################
# execution
###############################################################

processed_problems = get_processed_problems()
def count_nested_dict(d):
    return sum([count_nested_dict(v) if isinstance(v, dict) else 1 for v in d.values()])
print("already processed: " + str(count_nested_dict(processed_problems)))

fhs = open(save_file,"a+")
get_proving_results_from_problem_file_list(debug_print_line,
    prover_name, prover_command, prover_wc_limit, prover_cpu_limit,
    embedding_wc_limit, embedding_cpu_limit,
    problem_file_list,
    system_list, quantification_list, consequence_list, constants_list,
    transformation_parameter_list
)

fhs.close()
