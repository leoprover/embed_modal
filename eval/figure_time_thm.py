import plotly.graph_objects as go
import plotly
import sys
import common
import table_single_provers
from pathlib import Path
import figure_thm_comparison

def plot(prover_dict,systemprefix):
    mlean_t_const = [20,30,40]
    mlean_x_const = [1,2,3]
    leo_t_const = [21,32,43]
    leo_x_const = [1,2,3]
    satallax_t_const = [22,33,44]
    satallax_x_const = [1,2,3]

    mlean_t_cumul = [20,30,40]
    mlean_x_cumul = [1,2,3]
    leo_t_cumul = [21,32,43]
    leo_x_cumul = [1,2,3]
    satallax_t_cumul = [23,34,45]
    satallax_x_cumul = [1,2,3]

    mlean_t_vary = [20,30,40]
    mlean_x_vary = [1,2,3]
    leo_t_vary = [21,32,43]
    leo_x_vary = [1,2,3]
    satallax_t_vary = [26,37,49]
    satallax_x_vary = [1,2,5]

    systems = ["D","T","S4","S5"]
    quants = ["const","cumul","vary"]
    configurations = [(a,b) for a in systems for b in quants]
    fig = plotly.subplots.make_subplots(
        rows=4, cols=3,
        #shared_yaxes=True,
        horizontal_spacing = 0.05,
        vertical_spacing = 0.05,
        row_heights=[0.3]*4,
        column_widths=[0.3]*3,
        subplot_titles=(list(map(lambda x: x[0]+"/"+x[1],configurations)))
    )
    for row in range(4):
        for column in range(3):
            mlean_t = [20,30,40]
            mlean_x = [1,2,3]
            leo_t = [23,33,46]
            leo_x = [1,2,3]
            satallax_t = [25,39,50]
            satallax_x = [1,2,3]
            show_legend = False
            if row == 0 and column == 0:
                show_legend = True
            mlean_plot = go.Scatter(name="MLeanCop",showlegend=show_legend,y=mlean_t,x=mlean_x,marker=dict(color="Crimson", size=10))
            leo_plot = go.Scatter(name="Leo",showlegend=show_legend,y=leo_t,x=leo_x,marker=dict(color="Blue", size=10))
            satallax_plot = go.Scatter(name="Satallax",showlegend=show_legend,y=satallax_t,x=satallax_x,marker=dict(color="Orange", size=10))

            fig.append_trace(mlean_plot,row=(row+1),col=(column+1))
            fig.append_trace(leo_plot,row=(row+1),col=(column+1))
            fig.append_trace(satallax_plot,row=(row+1),col=(column+1))

            yaxis_dict = dict(titlefont_size=figure_thm_comparison.FONTSIZE,
                              titlefont_color="black",
                              tickfont_color="black",
                              tickfont_size=figure_thm_comparison.FONTSIZE,
                              range=[0, 50], row=row+1, col=column+1)
            xaxis_dict = dict(titlefont_size=figure_thm_comparison.FONTSIZE,
                              titlefont_color="black",
                              tickfont_color="black",
                              tickfont_size=figure_thm_comparison.FONTSIZE,
                              range=[0, 10], row=row+1, col=column+1)
            if column == 0:
                yaxis_dict["title_text"]="time (s)"
            if row == 3:
                xaxis_dict["title_text"]="number of theorems"
            fig.update_yaxes(**yaxis_dict)
            fig.update_xaxes(**xaxis_dict)

    fig.update_layout(
        #title='US Export of Plastic Scrap',
        font_color="black",
        font_size=figure_thm_comparison.FONTSIZE,
        legend_orientation="h",
        legend=dict(
            font_size=figure_thm_comparison.FONTSIZE
            #    x=0,
            #    y=1.0,
            #    bgcolor='rgba(255, 255, 255, 0)',
            #    bordercolor='rgba(255, 255, 255, 0)'
        )
    )
    for i in fig['layout']['annotations']:
        i['font'] = dict(size=figure_thm_comparison.FONTSIZE,color='black')
    return fig

def main_helper(prover_dict):
    #outdir = Path("/home/tg/master_thesis/thesis/tables")
    #fig = plot(prover_dict,"T")
    fig = plot(None,"T")
    #fig.show()
    path="/home/tg/master_thesis/thesis/plots/thm_comparison_time.png"
    fig.write_image(path,width=1600,height=1600*4/3)

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    main_helper(prover_dict)

if __name__ == "__main__":
    main(sys.argv[1:])