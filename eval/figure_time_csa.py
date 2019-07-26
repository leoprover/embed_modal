import plotly.graph_objects as go
import plotly
import sys
import common
import table_single_provers
from pathlib import Path
import figure_thm_comparison

def main_helper(prover_dict):
    pass

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    main_helper(prover_dict)

if __name__ == "__main__":
    main(sys.argv[1:])