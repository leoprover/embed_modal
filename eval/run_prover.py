import subprocess
import pathlib
import tempfile
import os
import common
import csv
import datetime
import sys
from extract_qmltp_info import extract_qmltp_info_from_problem_to_dict
from filters_for_the_qmltp import cumul_interesting_problems,qmltp_problems_containing_equality, init, qmltp_problems_without_modal_operators

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
    filename = common.create_temp_file(problem)

    #  execute prover command with tree limited run on temp file
    cmd = prover_command.replace("%s",filename).replace("%d",str(wc_limit))
    stdout,stderr,returncode = common.execute_treelimitedrun(bin_treelimitedrun,cmd, wc_limit, cpu_limit)

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
        szs_status = common.parse_szs_status(str_stdout)
        wc = common.parse_wc(str_stdout)
        cpu = common.parse_cpu(str_stdout)
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




##############################
# here come the helper methods
##############################







def run_embedding_and_prover(problem,embedding_parameters, embedding_semantics, embedding_wc_limit, embedding_cpu_limit,
                             prover_command, prover_wc_limit, prover_cpu_limit):
    embedding_ret = common.embed(bin_treelimitedrun, bin_embed,problem, embedding_parameters, embedding_semantics, embedding_wc_limit, embedding_cpu_limit)
    #print(embedding_ret)
    prover_ret = run_local_prover_helper(prover_command, embedding_ret['embedded_problem'],prover_wc_limit, prover_cpu_limit)
    #print(prover_ret)
    return embedding_ret, prover_ret



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
                        if problem_white_filter != None and not p.name in problem_white_filter:
                            continue
                        if problem_black_filter != None and p.name in problem_black_filter:
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

fhs_log = None
def log(*content):
    out = str(datetime.datetime.now()) + "   " + " ".join(content)
    print(out)
    fhs_log.write(out+"\n")
    fhs_log.flush()

fhs = None
def debug_print_line(line,e,r):
    fhs.write(",".join(line)+"\n")
    fhs.flush()
    log(",".join(line))
    problem_status = extract_qmltp_info_from_problem_to_dict(e['problem'])
    system = e['semantics']['system']
    quant = e['semantics']['quantification']
    if system == "$modal_system_S5U":
        qmltp_szs_status = problem_status["$modal_system_S5"][quant]
    else:
        qmltp_szs_status = problem_status[system][quant]
    prove_status = r['szs_status']
    log("qmltp szs:  ",qmltp_szs_status)
    log("prover szs: ",prove_status)
    if qmltp_szs_status == prove_status and qmltp_szs_status in ["Theorem","Non-Theorem","CounterSatisfiable"]:
        log("szs_match")
    else:
        log("szs_nomatch")
    log("### prover stdout")
    log(r['raw'].replace("\\n","\n"))
    log("### embedding stdout")
    log(e['raw'].replace("\\n","\n"))
    log("### embedded problem")
    log(e['embedded_problem'])
    if \
            prove_status != "TimeoutExecution" and \
            prove_status != "Timeout" and \
            prove_status != "GaveUp" and \
            qmltp_szs_status in ["Theorem","Non-Theorem","CounterSatisfiable"] and \
            qmltp_szs_status != prove_status:
        log("### ERROR: status does not match!")
        log("### original problem")
        log(e['problem'])

    log("====================================================================================")



processed_problems = None
def get_processed_problems():
    processed_problems = {}
    try:
        f = open(save_file,'r')
        for r in f.readlines():
            #APM009+1.p,leo3 1.3,Theorem,2.6,8.8,$modal_system_S4,$cumulative,$local,$rigid,syntactic_modality_axiomatization syntactic_monotonic_quantification semantic_antimonotonic_quantification
            #print(row)
            if r.strip() == '':
                continue
            row = r.split(',')
            p = common.Problem(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9].split(' '))
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
    except:
        pass
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
# embedding settings
###############################################################

system_list = [
    #"$modal_system_K",
    #"$modal_system_D",
    #"$modal_system_T",
    #"$modal_system_S4",
    #"$modal_system_S5",
    "$modal_system_S5U"
]
quantification_list = [
    "$constant",
    "$varying",
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
    #"semantic_modality_axiomatization",
    #"semantic_monotonic_quantification",
    "semantic_antimonotonic_quantification",
    "syntactic_modality_axiomatization",
    "syntactic_monotonic_quantification"
    #"syntactic_antimonotonic_quantification"
]
#semantic_modality_axiomatization semantic_monotonic_quantification semantic_antimonotonic_quantification
#transformation_parameter_list = [ # old params
#    "semantical_modality_axiomatization"
#]

###############################################################
# paths
###############################################################

save_file = "/home/tg/embed_modal/eval/result_leo_s5u_syn.csv"
log_file = "/home/tg/embed_modal/eval/result_leo_s5u_syn.log.csv"
qmltp_path = "/home/tg/data/QMLTP/qmltp_thf_no_mml"


###############################################################
# prover settings
###############################################################

prover_name = "leo3 1.3"
#prover_command = "leo3 %s -t %d"
#prover_command = "java -Xss128m -Xmx4g -Xms1g -jar /home/tg/starexec/uberleo/leo3.jar %s -t %d " \
#                 "--atp iprover=/home/tg/starexec/uberleo/externals/iprover " \
#                 "--atp e=/home/tg/starexec/uberleo/externals/eprover " \
#                 "--atp cvc4=/home/tg/starexec/uberleo/externals/cvc4-1.7-x86_64-linux-opt " \
#                 "--atp vampire=/home/tg/starexec/uberleo/externals/vampire " \
#                 "--atp-max-jobs 2 " \
#                 "--atp-timeout cvc4=30 " \
#                 "--atp-timeout e=30 " \
#                 "--atp-timeout vampire=30 " \
#                 "--atp-timeout iprover=30 " \
#                 "--atp-debug "
prover_command = "java -Xss128m -Xmx4g -Xms1g -jar /home/tg/starexec/uberleo/leo3.jar %s -t %d " \
                     "--atp e=/home/tg/starexec/uberleo/externals/eprover " \
                     "--atp cvc4=/home/tg/starexec/uberleo/externals/cvc4-1.7-x86_64-linux-opt " \
                     "--atp-max-jobs 2 " \
                     "--atp-timeout cvc4=30 " \
                     "--atp-timeout e=30 " \
                     "--atp-debug "
prover_wc_limit = 120
prover_cpu_limit = 120
embedding_wc_limit = 600
embedding_cpu_limit = 3600
#prover_wc_limit = 6
#prover_cpu_limit = 6
#embedding_wc_limit = 6
#embedding_cpu_limit = 6
#problem_file_list = common.get_problem_file_list(common.problem_directory)[:1]
problem_file_list = common.get_problem_file_list(qmltp_path)


###############################################################
# filter for problems
###############################################################

init(qmltp_path)
#problem_white_filter = qmltp_problems_without_modal_operators
#problem_white_filter = cumul_interesting_problems
problem_white_filter = None
problem_black_filter = None
#problem_black_filter = qmltp_problems_containing_equality


###############################################################
# execution
###############################################################

processed_problems = get_processed_problems()
def count_nested_dict(d):
    return sum([count_nested_dict(v) if isinstance(v, dict) else 1 for v in d.values()])
print("already processed: " + str(count_nested_dict(processed_problems)))

fhs = open(save_file,"a+")
fhs_log = open(log_file,"a+")
get_proving_results_from_problem_file_list(debug_print_line,
    prover_name, prover_command, prover_wc_limit, prover_cpu_limit,
    embedding_wc_limit, embedding_cpu_limit,
    problem_file_list,
    system_list, quantification_list, consequence_list, constants_list,
    transformation_parameter_list
)

fhs.close()
