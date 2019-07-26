import figure_csa_comparison
import figure_thm_comparison
import figure_time_thm
import figure_time_csa
import sys
import common
import table_single_provers

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    figure_csa_comparison.main_helper(prover_dict)
    figure_thm_comparison.main_helper(prover_dict)
    figure_time_thm.main_helper(prover_dict)
    figure_time_csa.main_helper(prover_dict)



if __name__ == "__main__":
    main(sys.argv[1:])