import plotly.graph_objects as go
import plotly
import sys
import common
import table_single_provers
from pathlib import Path

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

    mlean_plot_const = go.Scatter(y=mlean_t_const,x=mlean_x_const,marker=dict(color="Crimson", size=10))
    #leo_plot_const = go.Scatter(y=leo_t_const,x=leo_x_const)
    #satallax_plot_const = go.Scatter(y=satallax_t_const,x=satallax_x_const)
    mlean_plot_cumul = go.Scatter(y=mlean_t_cumul,x=mlean_x_cumul,showlegend=False,marker=dict(color="Crimson", size=10))
    #leo_plot_cumul = go.Scatter(y=leo_t_cumul,x=leo_x_cumul)
    #satallax_plot_cumul = go.Scatter(y=satallax_t_cumul,x=satallax_x_cumul)
    mlean_plot_vary = go.Scatter(y=mlean_t_vary,x=mlean_x_vary,showlegend=False,marker=dict(color="Crimson", size=10))
    #leo_plot_vary = go.Scatter(y=leo_t_vary,x=leo_x_vary)
    #satallax_plot_vary = go.Scatter(y=satallax_t_vary,x=satallax_x_vary)

    systemprint = systemprefix
    if systemprefix == "S5U":
        systemprint = "S5"
    fig = plotly.subplots.make_subplots(
        rows=1, cols=3,
        shared_yaxes=True,
        subplot_titles=(systemprint+'/const', systemprint+'/cumul', systemprint+'vary')
    )
    fig.append_trace(mlean_plot_const,1,1)
    #fig.append_trace(leo_plot_const,1,1)
    #fig.append_trace(satallax_plot_const,1,1)
    fig.append_trace(mlean_plot_cumul,1,2)
    #fig.append_trace(leo_plot_cumul,1,2)
    #fig.append_trace(satallax_plot_cumul,1,2)
    fig.append_trace(mlean_plot_vary,1,3)
    #fig.append_trace(leo_plot_vary,1,3)
    #fig.append_trace(satallax_plot_vary,1,3)

    xaxis_dict = dict(
        title='number of theorems',
        titlefont_size=26,
        titlefont_color="black",
        tickfont_size=26, # font size of T/cumul
        tickfont_color="black"
    )
    yaxis_dict = dict(
        title='time (s)',
        titlefont_size=26, # font size of Number of theorems
        tickfont_size=26, # font size of numbers
        titlefont_color="black",
        tickfont_color="black"
    )
    fig.update_xaxes(range=[0, 9])
    fig.update_layout(
        #title='US Export of Plastic Scrap',
        #legend_orientation="h",
        xaxis=xaxis_dict,
        yaxis=yaxis_dict,
        xaxis2=xaxis_dict,
        yaxis2=yaxis_dict,
        xaxis3=xaxis_dict,
        yaxis3=yaxis_dict,
        legend=dict(
            font_color="black",
            font_size=26
            #    x=0,
            #    y=1.0,
            #    bgcolor='rgba(255, 255, 255, 0)',
            #    bordercolor='rgba(255, 255, 255, 0)'
        )
    )
    return fig

def main(csv_file_list):
    #problem_list = common.accumulate_csv(csv_file_list)
    #prover_dict = table_single_provers.getTableData(problem_list)
    #table_single_provers.createOptHo(prover_dict)

    outdir = Path("/home/tg/master_thesis/thesis/tables")
    #fig = plot(prover_dict,"T")
    fig = plot(None,"T")
    fig.show()

if __name__ == "__main__":
    main(sys.argv[1:])