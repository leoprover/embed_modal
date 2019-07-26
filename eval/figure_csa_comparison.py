import plotly.graph_objects as go
import sys
import common
import table_single_provers
import figure_thm_comparison

def main_helper(prover_dict):

    #systems=  ["D/const","D/cumul","D/vary","T/const","T/cumul","T/vary"] + ["S4/const","S4/cumul","S4/vary","S5/const","S5/cumul","S5/vary"]
    systems = ["D/const","T/const","S4/const","S5/const","S5/cumul"]
    #data_mleanTotal = [25,35,45,55,66,77]*2
    #data_mleanUniqueVsOptho = [15,25,35,45,55,65]*2 #here only nitpick#

    #data_nitpickTotal = [60,62,64,65,73,85]*2
    #data_nitpickUniqueVsMlean = [14,13,17,18,19,19]*2

    data_mleanTotal = [-1]*5
    data_mleanUniqueVsOptho = [-1]*5 #here only nitpick
    data_nitpickTotal = [-1]*5
    data_nitpickUniqueVsMlean = [-1]*5

    data_mleanTotal[0] = len(set(prover_dict['mleancop']["Dall"]["constall"]["csa_single"]))
    #data_mleanTotal[1] = len(set(prover_dict['mleancop']["Dall"]["cumulall"]["csa_single"]))
    #data_mleanTotal[2] = len(set(prover_dict['mleancop']["Dall"]["varyall"]["csa_single"]))
    data_mleanTotal[1] = len(set(prover_dict['mleancop']["Tall"]["constall"]["csa_single"]))
    #data_mleanTotal[4] = len(set(prover_dict['mleancop']["Tall"]["cumulall"]["csa_single"]))
    #data_mleanTotal[5] = len(set(prover_dict['mleancop']["Tall"]["varyall"]["csa_single"]))
    data_mleanTotal[2] = len(set(prover_dict['mleancop']["S4all"]["constall"]["csa_single"]))
    #data_mleanTotal[7] = len(set(prover_dict['mleancop']["S4all"]["cumulall"]["csa_single"]))
    #data_mleanTotal[8] = len(set(prover_dict['mleancop']["S4all"]["varyall"]["csa_single"]))
    data_mleanTotal[3] = len(set(prover_dict['mleancop']["S5all"]["constall"]["csa_single"]))
    data_mleanTotal[4] = len(set(prover_dict['mleancop']["S5all"]["cumulall"]["csa_single"]))
    #data_mleanTotal[11] = len(set(prover_dict['mleancop']["S5all"]["varyall"]["csa_single"]))

    data_mleanUniqueVsOptho[0] = len(set(prover_dict['mleancop']["Dall"]["constall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[1] = len(set(prover_dict['mleancop']["Dall"]["cumulall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[2] = len(set(prover_dict['mleancop']["Dall"]["varyall"]["csa_unique_compared_to_optho"]))
    data_mleanUniqueVsOptho[1] = len(set(prover_dict['mleancop']["Tall"]["constall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[4] = len(set(prover_dict['mleancop']["Tall"]["cumulall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[5] = len(set(prover_dict['mleancop']["Tall"]["varyall"]["csa_unique_compared_to_optho"]))
    data_mleanUniqueVsOptho[2] = len(set(prover_dict['mleancop']["S4all"]["constall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[7] = len(set(prover_dict['mleancop']["S4all"]["cumulall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[8] = len(set(prover_dict['mleancop']["S4all"]["varyall"]["csa_unique_compared_to_optho"]))
    data_mleanUniqueVsOptho[3] = len(set(prover_dict['mleancop']["S5all"]["constall"]["csa_unique_compared_to_optho"]))
    data_mleanUniqueVsOptho[4] = len(set(prover_dict['mleancop']["S5all"]["cumulall"]["csa_unique_compared_to_optho"]))
    #data_mleanUniqueVsOptho[11] = len(set(prover_dict['mleancop']["S5all"]["varyall"]["csa_unique_compared_to_optho"]))

    data_nitpickTotal[0] = len(set(prover_dict['nitpick']["Dsem"]["constsem"]["csa_single"]))
    #data_nitpickTotal[1] = len(set(prover_dict['nitpick']["Dsem"]["cumulsem"]["csa_single"]))
    #data_nitpickTotal[2] = len(set(prover_dict['nitpick']["Dsem"]["varyall"]["csa_single"]))
    data_nitpickTotal[1] = len(set(prover_dict['nitpick']["Tsem"]["constsem"]["csa_single"]))
    #data_nitpickTotal[4] = len(set(prover_dict['nitpick']["Tsem"]["cumulsem"]["csa_single"]))
    #data_nitpickTotal[5] = len(set(prover_dict['nitpick']["Tsem"]["varyall"]["csa_single"]))
    data_nitpickTotal[2] = len(set(prover_dict['nitpick']["S4sem"]["constsem"]["csa_single"]))
    #data_nitpickTotal[7] = len(set(prover_dict['nitpick']["S4sem"]["cumulsem"]["csa_single"]))
    #data_nitpickTotal[8] = len(set(prover_dict['nitpick']["S4sem"]["varyall"]["csa_single"]))
    data_nitpickTotal[3] = len(set(prover_dict['nitpick']["S5Usem"]["constsem"]["csa_single"]))
    data_nitpickTotal[4] = len(set(prover_dict['nitpick']["S5Usem"]["cumulsem"]["csa_single"]))
    #data_nitpickTotal[11] = len(set(prover_dict['nitpick']["S5sem"]["varyall"]["csa_single"]))

    data_nitpickUniqueVsMlean[0] = len(set(prover_dict['nitpick']["Dsem"]["constsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[1] = len(set(prover_dict['nitpick']["Dsem"]["cumulsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[2] = len(set(prover_dict['nitpick']["Dsem"]["varyall"]["csa_unique_compared_to_mleancop"]))
    data_nitpickUniqueVsMlean[1] = len(set(prover_dict['nitpick']["Tsem"]["constsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[4] = len(set(prover_dict['nitpick']["Tsem"]["cumulsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[5] = len(set(prover_dict['nitpick']["Tsem"]["varyall"]["csa_unique_compared_to_mleancop"]))
    data_nitpickUniqueVsMlean[2] = len(set(prover_dict['nitpick']["S4sem"]["constsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[7] = len(set(prover_dict['nitpick']["S4sem"]["cumulsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[8] = len(set(prover_dict['nitpick']["S4sem"]["varyall"]["csa_unique_compared_to_mleancop"]))
    data_nitpickUniqueVsMlean[3] = len(set(prover_dict['nitpick']["S5Usem"]["constsem"]["csa_unique_compared_to_mleancop"]))
    data_nitpickUniqueVsMlean[4] = len(set(prover_dict['nitpick']["S5Usem"]["cumulsem"]["csa_unique_compared_to_mleancop"]))
    #data_nitpickUniqueVsMlean[11] = len(set(prover_dict['nitpick']["S5sem"]["varyall"]["csa_unique_compared_to_mleancop"]))

    WIDTH = 0.24 * 5.0/6.0
    start = 0
    end = 5
    showLegend = True
    mleanTotal = go.Bar(
        name="mleanTotal",
        x=systems[start:end],
        y=data_mleanTotal[start:end],
        width = WIDTH,
        offset = -1*WIDTH,
        marker_color = "#1DD300"
    )

    mleanUniqueVsOptho = go.Bar(
        name="mleanUniqueVsOptho",
        x=systems[start:end],
        y=data_mleanUniqueVsOptho[start:end],
        width = WIDTH,
        offset = -1*WIDTH,
        marker_color = "#69DE56")

    nitpickTotal = go.Bar(
        name="nitpickTotal",
        x=systems[start:end],
        y=data_nitpickTotal[start:end],
        width = WIDTH,
        offset = 0*WIDTH,
        marker_color = "#FFCD00"
        #marker_color = "#FF6C00"
    )

    nitpickUniqueVsMlean = go.Bar(
        name="nitpickUniqueVsMlean",
        x=systems[start:end],
        y=data_nitpickUniqueVsMlean[start:end],
        width = WIDTH,
        offset = 0*WIDTH,
        marker_color = "#FFE063"
        #marker_color = "#FFA563"
    )

    fig = go.Figure([mleanTotal,mleanUniqueVsOptho,
                     nitpickTotal,nitpickUniqueVsMlean])
    fig.update_layout(
        #title='US Export of Plastic Scrap',
        showlegend=showLegend,
        legend_orientation="h",
        xaxis=dict(
            tickfont_size=figure_thm_comparison.FONTSIZE, # font size of T/cumul
            tickfont_color="black"
        ),
        yaxis=dict(
            title='Number of counter models',
            titlefont_size=figure_thm_comparison.FONTSIZE, # font size of Number of theorems
            tickfont_size=figure_thm_comparison.FONTSIZE, # font size of numbers
            titlefont_color="black",
            tickfont_color="black"
        ),
        legend=dict(
            font_color="black",
            font_size=figure_thm_comparison.FONTSIZE
            #    x=0,
            #    y=1.0,
            #    bgcolor='rgba(255, 255, 255, 0)',
            #    bordercolor='rgba(255, 255, 255, 0)'
        ),
        barmode='group',
        bargroupgap=0 # gap between bars of the same location coordinate.
    )
    path="/home/tg/master_thesis/thesis/plots/csa_comparison.png"
    fig.write_image(path,width=1600,height=900)

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    main_helper(prover_dict)

if __name__ == "__main__":
    main(sys.argv[1:])

