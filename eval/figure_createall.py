import figure_csa_comparison
import figure_thm_comparison
import figure_time_thm
import figure_time_csa
import sys
import common
import table_single_provers

COL_MLEANCOP_PRIMARY = "#FFCD00"
COL_MLEANCOP_SECONDARY = "#FFE063"
COL_SATALLAX_PRIMARY = "#1DD300"
COL_SATALLAX_SECONDARY = "#69DE56"
COL_LEO_PRIMARY = "#009E8E"
COL_LEO_SECONDARY = "#47B7AB"
COL_NITPICK_PRIMARY = "#0033cc"
COL_NITPICK_SECONDARY = "#3399ff"
COL_OPTHO_PRIMARY = "#F80012"
COL_OPTHO_SECONDARY = "#FA616D"
SIZE_LINE_TIME = 10
SIZE_FONT = 30

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