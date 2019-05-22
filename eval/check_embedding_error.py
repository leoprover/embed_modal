import common
import sys
import filters_for_the_qmltp

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
    "semantic_modality_axiomatization",
    "semantic_monotonic_quantification",
    "semantic_antimonotonic_quantification",
    #"syntactic_modality_axiomatization",
    #"syntactic_monotonic_quantification"
    #"syntactic_antimonotonic_quantification"
]

bin_treelimitedrun = "/home/tg/eval/TreeLimitedRun"
bin_embed = "java -jar /home/tg/embed_modal/embed/target/embed-1.0-SNAPSHOT-shaded.jar"
problem_black_filter = None
#problem_white_filter = filters_for_the_qmltp.qmltp_problems_without_modal_operators
problem_white_filter = None
embedding_wc_limit = 600
embedding_cpu_limit = 3600
qmltp_path = sys.argv[1]
problem_file_list = common.get_problem_file_list(qmltp_path)

def callback(line,e):
    print(line)
    if "Exception" in e['raw']:
        print("### ERROR")
        print(e['raw'])

common.get_embedding_results_from_problem_file_list(callback,bin_treelimitedrun,bin_embed,
                                                    problem_white_filter,problem_black_filter,
                                                    embedding_wc_limit,embedding_cpu_limit, problem_file_list,
                                                    system_list,quantification_list,consequence_list,constants_list,
                                                    transformation_parameter_list)