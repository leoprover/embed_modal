import plotly.express as px
import plotly.graph_objects as go
from pandas import DataFrame

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

WIDTH = 0.24
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
            tickfont_size=12, # font size of T/cumul
            tickfont_color="black"
        ),
        yaxis=dict(
            title='Number of theorems',
            titlefont_size=12, # font size of Number of theorems
            tickfont_size=12, # font size of numbers
            titlefont_color="black",
            tickfont_color="black"
        ),
        legend=dict(
            font_color="black",
            font_size=12
        #    x=0,
        #    y=1.0,
        #    bgcolor='rgba(255, 255, 255, 0)',
        #    bordercolor='rgba(255, 255, 255, 0)'
        ),
        barmode='group',
        bargroupgap=0 # gap between bars of the same location coordinate.
    )
    path="/home/tg/master_thesis/thesis/plots/thm_comparison_"+str(i)+".png"
    fig.write_image(path)
#fig.show()



#df = DataFrame(datadict)
#fig = px.bar(df, x="systems", y="total_bill", color='time')
#fig = px.bar(tips, x="sex", y="total_bill", color='time')
#fig.show()