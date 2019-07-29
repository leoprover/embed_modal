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
            sys=systems[row]
            quant=quants[column]

            mlean_sys = sys+"all"
            mlean_quant = quant+"all"
            mlean_t_cpu = sorted(list(map(lambda p: p.cpu,prover_dict["mleancop"][mlean_sys][mlean_quant]['thm_single'])))
            mlean_t_wc = sorted(list(map(lambda p: p.wc,prover_dict["mleancop"][mlean_sys][mlean_quant]['thm_single'])))
            mlean_x = [x for x in range(1,len(mlean_t_wc)+1)]
            leo_sys = sys+"sem"
            leo_quant = quant+"sem"
            if quant == "vary":
                leo_quant = quant+"all"
            if sys == "S5" and quant != "vary":
                leo_sys = "S5Uall"
            leo_t_cpu = sorted(list(map(lambda p: p.cpu,prover_dict["leo"][leo_sys][leo_quant]['thm_single'])))
            leo_t_wc = sorted(list(map(lambda p: p.wc,prover_dict["leo"][leo_sys][leo_quant]['thm_single'])))
            leo_x = [x for x in range(1,len(leo_t_wc)+1)]
            satallax_sys = sys+"sem"
            satallax_quant = quant+"sem"
            if quant == "vary":
                satallax_quant = quant+"all"
            if sys == "S5" and quant != "vary":
                satallax_sys = "S5Uall"
            satallax_t_cpu = sorted(list(map(lambda p: p.cpu,prover_dict["satallax"][satallax_sys][satallax_quant]['thm_single'])))
            satallax_t_wc = sorted(list(map(lambda p: p.wc,prover_dict["satallax"][satallax_sys][satallax_quant]['thm_single'])))
            satallax_x = [x for x in range(1,len(satallax_t_wc)+1)]

            show_legend = False
            if row == 0 and column == 0:
                show_legend = True
            mlean_plot_cpu = go.Scatter(name="MLeanCop CPU",showlegend=show_legend,y=mlean_t_cpu,x=mlean_x,marker=dict(color=figure_createall.COL_MLEANCOP_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            mlean_plot_wc = go.Scatter(name="MLeanCop WC",showlegend=show_legend,y=mlean_t_wc,x=mlean_x,marker=dict(color=figure_createall.COL_MLEANCOP_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            leo_plot_cpu = go.Scatter(name="Leo CPU",showlegend=show_legend,y=leo_t_cpu,x=leo_x,marker=dict(color=figure_createall.COL_LEO_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            leo_plot_wc = go.Scatter(name="Leo WC",showlegend=show_legend,y=leo_t_wc,x=leo_x,marker=dict(color=figure_createall.COL_LEO_SECONDARY, size=figure_createall.SIZE_LINE_TIME))
            satallax_plot_cpu = go.Scatter(name="Satallax CPU",showlegend=show_legend,y=satallax_t_cpu,x=satallax_x,marker=dict(color=figure_createall.COL_SATALLAX_PRIMARY, size=figure_createall.SIZE_LINE_TIME))
            satallax_plot_wc = go.Scatter(name="Satallax WC",showlegend=show_legend,y=satallax_t_wc,x=satallax_x,marker=dict(color=figure_createall.COL_SATALLAX_SECONDARY, size=figure_createall.SIZE_LINE_TIME))

            fig.append_trace(mlean_plot_cpu,row=(row+1),col=(column+1))
            fig.append_trace(mlean_plot_wc,row=(row+1),col=(column+1))
            fig.append_trace(leo_plot_cpu,row=(row+1),col=(column+1))
            fig.append_trace(leo_plot_wc,row=(row+1),col=(column+1))
            fig.append_trace(satallax_plot_cpu,row=(row+1),col=(column+1))
            fig.append_trace(satallax_plot_wc,row=(row+1),col=(column+1))

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
            if row == 3:
                xaxis_dict["title_text"]="number of theorems"
            fig.update_yaxes(**yaxis_dict)
            fig.update_xaxes(**xaxis_dict)

    fig.update_layout(
        #title='US Export of Plastic Scrap',
        font_color="black",
        font_size=figure_createall.SIZE_FONT,
        legend_orientation="h",
        legend=dict(
            y=-0.15*1200/(1600*4/3),
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
    path="/home/tg/master_thesis/thesis/plots/thm_comparison_time.png"
    fig.write_image(path,width=1600,height=1600*4/3)

def main(csv_file_list):
    problem_list = common.accumulate_csv(csv_file_list)
    prover_dict = table_single_provers.getTableData(problem_list)
    table_single_provers.createOptHo(prover_dict)
    main_helper(prover_dict)

if __name__ == "__main__":
    main(sys.argv[1:])