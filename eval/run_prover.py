import subprocess
import pathlib
import tempfile
import os
import common

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

    szs_status = parse_szs_status(str_stdout)
    wc = parse_wc(str_stdout)
    cpu = parse_cpu(str_stdout)

    # success data
    send_data = {}
    send_data['status'] = 'ok'
    send_data['szs_status'] = szs_status
    send_data['wc'] = wc
    send_data['cpu'] = cpu
    send_data['raw'] = str_stdout + "\n" + str_err
    send_data['return_code'] = str_returncode
    return send_data


def embed(problem,params,semantics,wc_limit,cpu_limit):
    # validate semantics
    # TODO
    if semantics == None:
        filecontent = problem
    else:
        filecontent =  semantics + "\n" + problem

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
        send_data['plain_prover_output'] = str_stdout + "\n" + str_err
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
    send_data['console'] = str_stdout + "\n" + str_err
    send_data['return_code'] = str_returncode
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
    newcmd = str(bin_treelimitedrun) + " " + str(cpu_limit) + " " + str(wc_limit) + " " + cmd
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
    return prover_ret

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
                        semantics = create_semantics(system,quantification,consequence,constants)
                        r = run_embedding_and_prover(content,transformation_parameter_list, semantics,
                                                              embedding_wc_limit, embedding_cpu_limit,
                                                              prover_command, prover_wc_limit, prover_cpu_limit)
                        line = [p.name, prover_name, r['szs_status'],r['wc'],r['cpu'],
                                         system,quantification,consequence,constants,
                                         " ".join(transformation_parameter_list)]
                        callback(line)

save_file = "/home/tg/eval/output.csv"
fhs = open(save_file,"w")
def debug_print_line(line):
    print(",".join(line))
    fhs.write(",".join(line)+"\n")
    fhs.flush()

prover_name = "leo3 1.3"
prover_command = "leo3 %s -t %d"
prover_wc_limit = 10
prover_cpu_limit = 10
embedding_wc_limit = 10
embedding_cpu_limit = 10
problem_file_list = common.get_problem_file_list(common.problem_directory)[:1]

system_list = [
    "$modal_system_K",
    "$modal_system_D",
    "$modal_system_T",
    "$modal_system_S4",
    "$modal_system_S5"
]
quantification_list = [
    "$constant",
    "$varying",
    "$cumulative",
    "$decreasing"
]
consequence_list = [
    "$local",
    "$global"
]
constants_list = [
    "$rigid"
]
transformation_parameter_list = [
    "semantic_modality_axiomatization",
    "semantic_monotonic_quantification",
    "semantic_antimonotonic_quantification"
]

get_proving_results_from_problem_file_list(debug_print_line,
    prover_name, prover_command, prover_wc_limit, prover_cpu_limit,
    embedding_wc_limit, embedding_cpu_limit,
    problem_file_list,
    system_list, quantification_list, consequence_list, constants_list,
    transformation_parameter_list
)

fhs.close()
