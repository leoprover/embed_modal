import plotly.express as px
import plotly.graph_objects as go
from pandas import DataFrame

tips = px.data.tips()
datadict = {
    "systems": ["T/const","T/vary"],
    "mleancop": [20,40],
    "ho": [30,50],
}

systems=  ["T/const","T/cumul","T/vary"]
mleancop= [20,40]
ho=  [30,50]

#erst das höhere stacken


data_mleanTotal = [25,35,45]
data_mleanUniqueVsOptho = [15,25,35]

data_opthoTotal = [60,62,64]
data_opthoUniqueVsMLean = [14,13,17]

data_leoTotal = [30,40,50]
data_satallaxTotal = [20,30,40]
data_leoUniqueVsMlean = [9,10,11]
data_satallaxUniqueVsMlean = [7,8,9]

WIDTH = 0.22
mleanTotal = go.Bar(
    name="mleanTotal",
    x=systems,
    y=data_mleanTotal,
    width = WIDTH,
    offset = -1.5*WIDTH,
    marker_color = "#1DD300"
)

mleanUniqueVsOptho = go.Bar(
    name="mleanUniqueVsOptho",
    x=systems,
    y=data_mleanUniqueVsOptho,
    width = WIDTH,
    offset = -1.5*WIDTH,
    marker_color = "#69DE56")

leoTotal = go.Bar(
    name="leoTotal",
    x=systems,
    y=data_leoTotal,
    width = WIDTH,
    offset = -0.5*WIDTH,
    marker_color = "#009E8E"
)

leoUniqueVsMlean = go.Bar(
    name="leoUniqueVsMlean",
    x=systems,
    y=data_leoUniqueVsMlean,
    width = WIDTH,
    offset = -0.5*WIDTH,
    marker_color = "#47B7AB"
)

satallaxTotal = go.Bar(
    name="satallaxTotal",
    x=systems,
    y=data_satallaxTotal,
    width = WIDTH,
    offset = 0.5*WIDTH,
    marker_color = "#FFCD00"
    #marker_color = "#FF6C00"
)

satallaxUniqueVsMlean = go.Bar(
    name="satallaxUniqueVsMlean",
    x=systems,
    y=data_satallaxUniqueVsMlean,
    width = WIDTH,
    offset = 0.5*WIDTH,
    marker_color = "#FFE063"
    #marker_color = "#FFA563"
)

opthoTotal = go.Bar(
    name="opthoTotal",
    x=systems,
    y=data_opthoTotal,
    width = 2*WIDTH,
    offset = -0.5*WIDTH,
    marker_color = "#F80012"
)

opthoUniqueVsMLean = go.Bar(
    name="opthoUniqueVsMLean",
    x=systems,
    y=data_opthoUniqueVsMLean,
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
    xaxis_tickfont_size=12, # font size of T/cumul
    yaxis=dict(
        title='Number of Theorems',
        titlefont_size=14, # font size of Number of theorems
        tickfont_size=12, # font size of numbers
    ),
    #legend=dict(
    #    x=0,
    #    y=1.0,
    #    bgcolor='rgba(255, 255, 255, 0)',
    #    bordercolor='rgba(255, 255, 255, 0)'
    #),
    barmode='group',
    bargroupgap=0 # gap between bars of the same location coordinate.
)
path="/home/tg/master_thesis/thesis/plots/n1.png"
fig.write_image(path)
#fig.show()

#df = DataFrame(datadict)
#fig = px.bar(df, x="systems", y="total_bill", color='time')
#fig = px.bar(tips, x="sex", y="total_bill", color='time')
#fig.show()