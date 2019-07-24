import plotly.express as px
import plotly.graph_objects as go
from pandas import DataFrame
import sys
import common
import table_single_provers

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    #systems=  ["T/const","T/cumul","T/vary"]
    systems=  ["D/const","D/cumul","D/vary","T/const","T/cumul","T/vary"] + ["S4/const","S4/cumul","S4/vary","S5/const","S5/cumul","S5/vary"]
    #systems=  ["D/const","T/const","S4/const","S5/const"]


    data_mleanTotal = [25,35,45,55,66,77]*2
    data_mleanUniqueVsOptho = [15,25,35,45,55,65]*2

    data_opthoTotal = [60,62,64,65,73,85]*2
    data_opthoUniqueVsMLean = [14,13,17,18,19,19]*2

    data_leoTotal = [30,40,50,60,70,80]*2
    data_satallaxTotal = [20,30,40,50,60,70]*2
    data_leoUniqueVsMlean = [9,10,11,12,13,14]*2
    data_satallaxUniqueVsMlean = [7,8,9,10,11,12]*2

    #data_mleanTotal[0] = prover_dict['mleancop']["Dall"]["constall"]["thm_single"]
    data_mleanTotal[0] = len(set(prover_dict['mleancop']["Dall"]["constall"]["thm_single"]))
    data_mleanTotal[1] = len(set(prover_dict['mleancop']["Dall"]["cumulall"]["thm_single"]))
    data_mleanTotal[2] = len(set(prover_dict['mleancop']["Dall"]["varyall"]["thm_single"]))
    data_mleanTotal[3] = len(set(prover_dict['mleancop']["Tall"]["constall"]["thm_single"]))
    data_mleanTotal[4] = len(set(prover_dict['mleancop']["Tall"]["cumulall"]["thm_single"]))
    data_mleanTotal[5] = len(set(prover_dict['mleancop']["Tall"]["varyall"]["thm_single"]))
    data_mleanTotal[6] = len(set(prover_dict['mleancop']["S4all"]["constall"]["thm_single"]))
    data_mleanTotal[7] = len(set(prover_dict['mleancop']["S4all"]["cumulall"]["thm_single"]))
    data_mleanTotal[8] = len(set(prover_dict['mleancop']["S4all"]["varyall"]["thm_single"]))
    data_mleanTotal[9] = len(set(prover_dict['mleancop']["S5all"]["constall"]["thm_single"]))
    data_mleanTotal[10] = len(set(prover_dict['mleancop']["S5all"]["cumulall"]["thm_single"]))
    data_mleanTotal[11] = len(set(prover_dict['mleancop']["S5all"]["varyall"]["thm_single"]))

    data_leoTotal[0] = len(set(prover_dict['leo']["Dsem"]["constsem"]["thm_single"]))
    data_leoTotal[1] = len(set(prover_dict['leo']["Dsem"]["cumulsem"]["thm_single"]))
    data_leoTotal[2] = len(set(prover_dict['leo']["Dsem"]["varyall"]["thm_single"]))
    data_leoTotal[3] = len(set(prover_dict['leo']["Tsem"]["constsem"]["thm_single"]))
    data_leoTotal[4] = len(set(prover_dict['leo']["Tsem"]["cumulsem"]["thm_single"]))
    data_leoTotal[5] = len(set(prover_dict['leo']["Tsem"]["varyall"]["thm_single"]))
    data_leoTotal[6] = len(set(prover_dict['leo']["S4sem"]["constsem"]["thm_single"]))
    data_leoTotal[7] = len(set(prover_dict['leo']["S4sem"]["cumulsem"]["thm_single"]))
    data_leoTotal[8] = len(set(prover_dict['leo']["S4sem"]["varyall"]["thm_single"]))
    data_leoTotal[9] = len(set(prover_dict['leo']["S5Usem"]["constsem"]["thm_single"]))
    data_leoTotal[10] = len(set(prover_dict['leo']["S5Usem"]["cumulsem"]["thm_single"]))
    data_leoTotal[11] = len(set(prover_dict['leo']["S5sem"]["varyall"]["thm_single"]))

    data_leoUniqueVsMlean[0] = len(set(prover_dict['leo']["Dsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[1] = len(set(prover_dict['leo']["Dsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[2] = len(set(prover_dict['leo']["Dsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[3] = len(set(prover_dict['leo']["Tsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[4] = len(set(prover_dict['leo']["Tsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[5] = len(set(prover_dict['leo']["Tsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[6] = len(set(prover_dict['leo']["S4sem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[7] = len(set(prover_dict['leo']["S4sem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[8] = len(set(prover_dict['leo']["S4sem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[9] = len(set(prover_dict['leo']["S5Usem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[10] = len(set(prover_dict['leo']["S5Usem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_leoUniqueVsMlean[11] = len(set(prover_dict['leo']["S5sem"]["varyall"]["thm_unique_compared_to_mleancop"]))

    data_satallaxTotal[0] = len(set(prover_dict['satallax']["Dsem"]["constsem"]["thm_single"]))
    data_satallaxTotal[1] = len(set(prover_dict['satallax']["Dsem"]["cumulsem"]["thm_single"]))
    data_satallaxTotal[2] = len(set(prover_dict['satallax']["Dsem"]["varyall"]["thm_single"]))
    data_satallaxTotal[3] = len(set(prover_dict['satallax']["Tsem"]["constsem"]["thm_single"]))
    data_satallaxTotal[4] = len(set(prover_dict['satallax']["Tsem"]["cumulsem"]["thm_single"]))
    data_satallaxTotal[5] = len(set(prover_dict['satallax']["Tsem"]["varyall"]["thm_single"]))
    data_satallaxTotal[6] = len(set(prover_dict['satallax']["S4sem"]["constsem"]["thm_single"]))
    data_satallaxTotal[7] = len(set(prover_dict['satallax']["S4sem"]["cumulsem"]["thm_single"]))
    data_satallaxTotal[8] = len(set(prover_dict['satallax']["S4sem"]["varyall"]["thm_single"]))
    data_satallaxTotal[9] = len(set(prover_dict['satallax']["S5Usem"]["constsem"]["thm_single"]))
    data_satallaxTotal[10] = len(set(prover_dict['satallax']["S5Usem"]["cumulsem"]["thm_single"]))
    data_satallaxTotal[11] = len(set(prover_dict['satallax']["S5sem"]["varyall"]["thm_single"]))

    data_satallaxUniqueVsMlean[0] = len(set(prover_dict['satallax']["Dsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[1] = len(set(prover_dict['satallax']["Dsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[2] = len(set(prover_dict['satallax']["Dsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[3] = len(set(prover_dict['satallax']["Tsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[4] = len(set(prover_dict['satallax']["Tsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[5] = len(set(prover_dict['satallax']["Tsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[6] = len(set(prover_dict['satallax']["S4sem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[7] = len(set(prover_dict['satallax']["S4sem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[8] = len(set(prover_dict['satallax']["S4sem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[9] = len(set(prover_dict['satallax']["S5Usem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[10] = len(set(prover_dict['satallax']["S5Usem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_satallaxUniqueVsMlean[11] = len(set(prover_dict['satallax']["S5sem"]["varyall"]["thm_unique_compared_to_mleancop"]))

    data_opthoTotal[0] = len(set(prover_dict['leo']["Dsem"]["constsem"]["thm_single"] + prover_dict['satallax']["Dsem"]["constsem"]["thm_single"]))
    data_opthoTotal[1] = len(set(prover_dict['leo']["Dsem"]["cumulsem"]["thm_single"] + prover_dict['satallax']["Dsem"]["cumulsem"]["thm_single"]))
    data_opthoTotal[2] = len(set(prover_dict['leo']["Dsem"]["varyall"]["thm_single"] + prover_dict['satallax']["Dsem"]["varyall"]["thm_single"]))
    data_opthoTotal[3] = len(set(prover_dict['leo']["Tsem"]["constsem"]["thm_single"] + prover_dict['satallax']["Tsem"]["constsem"]["thm_single"]))
    data_opthoTotal[4] = len(set(prover_dict['leo']["Tsem"]["cumulsem"]["thm_single"] + prover_dict['satallax']["Tsem"]["cumulsem"]["thm_single"]))
    data_opthoTotal[5] = len(set(prover_dict['leo']["Tsem"]["varyall"]["thm_single"] + prover_dict['satallax']["Tsem"]["varyall"]["thm_single"]))
    data_opthoTotal[6] = len(set(prover_dict['leo']["S4sem"]["constsem"]["thm_single"] + prover_dict['satallax']["S4sem"]["constsem"]["thm_single"]))
    data_opthoTotal[7] = len(set(prover_dict['leo']["S4sem"]["cumulsem"]["thm_single"] + prover_dict['satallax']["S4sem"]["cumulsem"]["thm_single"]))
    data_opthoTotal[8] = len(set(prover_dict['leo']["S4sem"]["varyall"]["thm_single"] + prover_dict['satallax']["S4sem"]["varyall"]["thm_single"]))
    data_opthoTotal[9] = len(set(prover_dict['leo']["S5Usem"]["constsem"]["thm_single"] + prover_dict['satallax']["S5Usem"]["constsem"]["thm_single"]))
    data_opthoTotal[10] = len(set(prover_dict['leo']["S5Usem"]["cumulsem"]["thm_single"] + prover_dict['satallax']["S5Usem"]["cumulsem"]["thm_single"]))
    data_opthoTotal[11] = len(set(prover_dict['leo']["S5sem"]["varyall"]["thm_single"] + prover_dict['satallax']["S5sem"]["varyall"]["thm_single"]))

    data_opthoUniqueVsMLean[0] = len(set(prover_dict['leo']["Dsem"]["constsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Dsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[1] = len(set(prover_dict['leo']["Dsem"]["cumulsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Dsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[2] = len(set(prover_dict['leo']["Dsem"]["varyall"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Dsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[3] = len(set(prover_dict['leo']["Tsem"]["constsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Tsem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[4] = len(set(prover_dict['leo']["Tsem"]["cumulsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Tsem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[5] = len(set(prover_dict['leo']["Tsem"]["varyall"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["Tsem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[6] = len(set(prover_dict['leo']["S4sem"]["constsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S4sem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[7] = len(set(prover_dict['leo']["S4sem"]["cumulsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S4sem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[8] = len(set(prover_dict['leo']["S4sem"]["varyall"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S4sem"]["varyall"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[9] = len(set(prover_dict['leo']["S5Usem"]["constsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S5Usem"]["constsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[10] = len(set(prover_dict['leo']["S5Usem"]["cumulsem"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S5Usem"]["cumulsem"]["thm_unique_compared_to_mleancop"]))
    data_opthoUniqueVsMLean[11] = len(set(prover_dict['leo']["S5sem"]["varyall"]["thm_unique_compared_to_mleancop"] + prover_dict['satallax']["S5sem"]["varyall"]["thm_unique_compared_to_mleancop"]))

    WIDTH = 0.25


    for i in range(2):

        if i==0:
            start = 0
            end = 6
            showLegend=False
        else:
            start=6
            end=12
            showLegend=True
        mleanTotal = go.Bar(
            name="mleanTotal",
            x=systems[start:end],
            y=data_mleanTotal[start:end],
            width = WIDTH,
            offset = -1.5*WIDTH,
            marker_color = "#1DD300"
        )

        mleanUniqueVsOptho = go.Bar(
            name="mleanUniqueVsOptho",
            x=systems[start:end],
            y=data_mleanUniqueVsOptho[start:end],
            width = WIDTH,
            offset = -1.5*WIDTH,
            marker_color = "#69DE56")

        leoTotal = go.Bar(
            name="leoTotal",
            x=systems[start:end],
            y=data_leoTotal[start:end],
            width = WIDTH,
            offset = -0.5*WIDTH,
            marker_color = "#009E8E"
        )

        leoUniqueVsMlean = go.Bar(
            name="leoUniqueVsMlean",
            x=systems[start:end],
            y=data_leoUniqueVsMlean[start:end],
            width = WIDTH,
            offset = -0.5*WIDTH,
            marker_color = "#47B7AB"
        )

        satallaxTotal = go.Bar(
            name="satallaxTotal",
            x=systems[start:end],
            y=data_satallaxTotal[start:end],
            width = WIDTH,
            offset = 0.5*WIDTH,
            marker_color = "#FFCD00"
            #marker_color = "#FF6C00"
        )

        satallaxUniqueVsMlean = go.Bar(
            name="satallaxUniqueVsMlean",
            x=systems[start:end],
            y=data_satallaxUniqueVsMlean[start:end],
            width = WIDTH,
            offset = 0.5*WIDTH,
            marker_color = "#FFE063"
            #marker_color = "#FFA563"
        )

        opthoTotal = go.Bar(
            name="opthoTotal",
            x=systems[start:end],
            y=data_opthoTotal[start:end],
            width = 2*WIDTH,
            offset = -0.5*WIDTH,
            marker_color = "#F80012"
        )

        opthoUniqueVsMLean = go.Bar(
            name="opthoUniqueVsMLean",
            x=systems[start:end],
            y=data_opthoUniqueVsMLean[start:end],
            width = 2*WIDTH,
            offset = -0.5*WIDTH,
            marker_color = "#FA616D"
        )

        fig = go.Figure([mleanTotal,mleanUniqueVsOptho,
                         opthoTotal,
                         leoTotal,satallaxTotal,
                         opthoUniqueVsMLean,
                         leoUniqueVsMlean,satallaxUniqueVsMlean])
        fig.update_layout(
            #title='US Export of Plastic Scrap',
            showlegend=showLegend,
            legend_orientation="h",
            xaxis=dict(
                tickfont_size=26, # font size of T/cumul
                tickfont_color="black"
            ),
            yaxis=dict(
                title='Number of theorems',
                titlefont_size=26, # font size of Number of theorems
                tickfont_size=26, # font size of numbers
                titlefont_color="black",
                tickfont_color="black"
            ),
            legend=dict(
                font_color="black",
                font_size=26
            #    x=0,
            #    y=1.0,
            #    bgcolor='rgba(255, 255, 255, 0)',
            #    bordercolor='rgba(255, 255, 255, 0)'
            ),
            barmode='group',
            bargroupgap=0 # gap between bars of the same location coordinate.
        )
        path="/home/tg/master_thesis/thesis/plots/thm_comparison_"+str(i)+".png"
        fig.write_image(path,width=1400,height=800)



if __name__ == "__main__":
    main(sys.argv[1:])


