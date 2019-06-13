from common import embed,accumulate_csv,create_dict_from_problems,iterate_dict,filename_to_path,run_local_prover
from check_consistency import check_consistency_iteration_callback
from starexec_create_configurations import get_transformation_abbreviation
import sys
from pathlib import Path

def main(qmltp_dir,out_dir,csv_file_list):
    bin_treelimitedrun = "/home/tg/embed_modal/eval/TreeLimitedRun"
    bin_embed = "java -jar /home/tg/embed_modal/embed/target/embed-1.0-SNAPSHOT-shaded.jar" # dev
    bin_solver = "/home/tg/embed_modal/eval/provers/isabelle2018/isabelle/bin/isabelle tptp_nitpick %d %s"

    #quantification = "$varying"
    #system = "$modal_system_S4"
    #sem = {"system":system,"quantification":quantification,"consequence":"$local","constants":"$rigid"}
    #params = ["semantic_constant_quantification","semantic_cumulative_quantification","semantic_decreasing_quantification","semantic_modality_axiomatization"]
    #params_old = ["semantic_monotonic_quantification","semantic_antimonotonic_quantification","semantic_modality_axiomatization"]
    #params_very_old = []
    #problemfile = "/home/tg/embed_modal/eval/datasets/qmltp_thf_standard/GLC/GLC414+1.p"

    problem_list = accumulate_csv(csv_file_list)
    problem_dict = create_dict_from_problems(problem_list)
    filename_to_issue = {}
    iterate_dict(problem_dict, check_consistency_iteration_callback, filename_to_issue)
    Path(out_dir).mkdir(exist_ok=True)
    for filename in filename_to_issue:
        issue_list = filename_to_issue[filename]
        for issue_dict in issue_list:
            quantification = issue_dict['quantification']
            system = issue_dict['system']
            sem = {"system":system,"quantification":quantification,"consequence":"$local","constants":"$rigid"}
            params = ["semantic_constant_quantification",
                      "semantic_cumulative_quantification",
                      "semantic_decreasing_quantification",
                      "semantic_modality_axiomatization"]
            if quantification != "$varying":
                continue
            if system == "$modal_system_K":
                continue

            problemfile = filename_to_path(qmltp_dir,filename)
            print("currently processing",problemfile,system,quantification)
            with open(problemfile,"r") as fh:
                problem = fh.read()
                if not "GSY364+1.p" in str(problemfile):
                    continue
                outpathembedding = Path(out_dir) / (filename + "_" + system.replace("$modal_system","") + "_" + quantification.replace("$","") + ".p")
                outpathoriginal = Path(out_dir) / (filename + "_" + system.replace("$modal_system","") + "_" + quantification.replace("$","") + "_original.p")
                outpathmodel = Path(out_dir) / (filename + "_" + system.replace("$modal_system","") + "_" + quantification.replace("$","") + "_model.p")
                if outpathmodel.exists():
                    print(str(outpathmodel) + " already exists. Skip.")
                    continue

                e = embed(bin_treelimitedrun, bin_embed,problem,params,sem,120,120)
                with open(outpathembedding,"w+")as fw:
                    fw.write(e['embedded_problem'])

                with open(outpathoriginal,"w+")as fw:
                    fw.write("% " + str(sem))
                    fw.write(problem)

                r = run_local_prover(bin_treelimitedrun,bin_solver, e['embedded_problem'], 500, 500)
                modelstart = r['raw'].find("% SZS status")
                if modelstart == -1:
                    modelstart = 0
                model = r['raw'][modelstart:]
                with open(outpathmodel,"w+")as fw:
                    fw.write(model)

if __name__ == "__main__":
    main(sys.argv[1],sys.argv[2],sys.argv[3:])
