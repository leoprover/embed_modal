import plotly.graph_objects as go
import plotly
import sys
import common
import table_single_provers
import figure_createall

def plot(prover_dict):

    systems = ["D","T","S4","S5"]
    quants = ["const","cumul","vary"]
    configurations = [(a,b) for a in systems for b in quants]
    fig = plotly.subplots.make_subplots(
        rows=2, cols=3,
        #shared_yaxes=True,
        horizontal_spacing = 0.05,
        vertical_spacing = 0.10,
        row_heights=[0.3]*2,
        column_widths=[0.3]*3,
        subplot_titles=(["D/const","T/const","S4/const","S5/const","S5/cumul"])
    )
    for row in range(2):
        for column in range(3):
            if row == 1 and column == 2:
                continue
            if row == 0:
                sys = systems[column]
            else:
                sys = "S5"
            if row == 1 and column == 1:
                quant = "cumul"
            else:
                quant = "const"


            mlean_sys = sys+"all"
            mlean_quant = quant+"all"
            mlean_t_cpu = sorted(list(map(lambda p: p.cpu,prover_dict["mleancop"][mlean_sys][mlean_quant]['csa_single'])))
            mlean_t_wc = sorted(list(map(lambda p: p.wc,prover_dict["mleancop"][mlean_sys][mlean_quant]['csa_single'])))
            mlean_x = [x for x in range(1,len(mlean_t_wc)+1)]
            nitpick_sys = sys+"sem"
            nitpick_quant = quant+"all"
            if quant == "vary":
                nitpick_quant = quant+"all"
            if sys == "S5" and quant != "vary":
                nitpick_sys = "S5Usem"
            if sys == "D" and quant == "const":
                nitpick_sys = "Tsyn"
            nitpick_t_cpu = sorted(list(map(lambda p: p.cpu,prover_dict["nitpick"][nitpick_sys][nitpick_quant]['csa_single'])))
            nitpick_t_wc = sorted(list(map(lambda p: p.wc,prover_dict["nitpick"][nitpick_sys][nitpick_quant]['csa_single'])))
            nitpick_x = [x for x in range(1,len(nitpick_t_wc)+1)]

            show_legend = False
            if row == 0 and column == 0:
                show_legend = True
            mlean_plot_cpu = go.Scatter(name="MLeanCop CPU",showlegend=show_legend,y=mlean_t_cpu,x=mlean_x,marker=dict(color=figure_createall.COL_MLEANCOP_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            mlean_plot_wc = go.Scatter(name="MLeanCop WC",showlegend=show_legend,y=mlean_t_wc,x=mlean_x,marker=dict(color=figure_createall.COL_MLEANCOP_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            nitpick_plot_cpu = go.Scatter(name="Nitpick CPU",showlegend=show_legend,y=nitpick_t_cpu,x=nitpick_x,marker=dict(color=figure_createall.COL_NITPICK_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            nitpick_plot_wc = go.Scatter(name="Nitpick WC",showlegend=show_legend,y=nitpick_t_wc,x=nitpick_x,marker=dict(color=figure_createall.COL_NITPICK_SECONDARY, size=figure_createall.SIZE_LINE_TIME))

            fig.append_trace(mlean_plot_cpu,row=(row+1),col=(column+1))
            fig.append_trace(mlean_plot_wc,row=(row+1),col=(column+1))
            fig.append_trace(nitpick_plot_cpu,row=(row+1),col=(column+1))
            fig.append_trace(nitpick_plot_wc,row=(row+1),col=(column+1))

            yaxis_dict = dict(titlefont_size=figure_createall.SIZE_FONT,
                              titlefont_color="black",
                              tickfont_color="black",
                              tickfont_size=figure_createall.SIZE_FONT,
                              range=[0, 240], row=row+1, col=column+1)
            xaxis_dict = dict(titlefont_size=figure_createall.SIZE_FONT,
                              titlefont_color="black",
                              tickfont_color="black",
                              tickfont_size=figure_createall.SIZE_FONT,
                              range=[0, 450], row=row+1, col=column+1)
            if column == 0:
                yaxis_dict["title_text"]="time (s)"
            if row == 1:
                xaxis_dict["title_text"]="number of counter models"
            fig.update_yaxes(**yaxis_dict)
            fig.update_xaxes(**xaxis_dict)

    fig.update_layout(
        #title='US Export of Plastic Scrap',
        font_color="black",
        font_size=figure_createall.SIZE_FONT,
        legend_orientation="h",
        legend=dict(
            y=-0.15,
            font_size=figure_createall.SIZE_FONT
            #    x=0,
            #    y=1.0,
            #    bgcolor='rgba(255, 255, 255, 0)',
            #    bordercolor='rgba(255, 255, 255, 0)'
        )
    )
    for i in fig['layout']['annotations']:
        i['font'] = dict(size=figure_createall.SIZE_FONT,color='black')
    return fig

def main_helper(prover_dict):
    fig = plot(prover_dict)
    #fig.show()
    path="/home/tg/master_thesis/thesis/plots/csa_comparison_time.png"
    fig.write_image(path,width=1600, height=1200)

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    main_helper(prover_dict)

if __name__ == "__main__":
    main(sys.argv[1:])