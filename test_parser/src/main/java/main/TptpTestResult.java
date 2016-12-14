package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TptpTestResult{
    private List<TptpProblem> problems;
    private List<TptpProblem> parsedProblemsWithErrors;
    private List<TptpProblem> parsedProblemsWithoutErrors;

    // Problem         Frm V SZS Rtng  Forms   Type  Units  Atoms  EqAts VarAts Symbls  Preds Arity  Funcs Arity   Vars     !>      ^      !      ?  Conns Ariths category parseTime parseError
    //AGT031^1         TH0 S THM 0.43    118     45      0    481     38    183     49      0     -      0     -    111      0     71     34      6

    public static HashMap<String,Integer> metaIndexTypes;
    static{
        metaIndexTypes = new HashMap<>();
        metaIndexTypes.put("Rtng",4);
        metaIndexTypes.put("Forms",5);
        metaIndexTypes.put("Type",6);
        metaIndexTypes.put("Units",7);
        metaIndexTypes.put("Atoms",8);
        metaIndexTypes.put("EqAts",9);
        metaIndexTypes.put("VarAts",10);
        metaIndexTypes.put("Symbls",11);
        metaIndexTypes.put("Vars",16);
        metaIndexTypes.put("Lambda",18);
        metaIndexTypes.put("Forall",19);
        metaIndexTypes.put("Exists",20);
        metaIndexTypes.put("Conns",21);
    }

    public TptpTestResult(List<TptpProblem> pr){
        problems = pr;
        parsedProblemsWithErrors = problems.stream().filter(p->p.parseError != null && p.parsed).collect(Collectors.toList());
        parsedProblemsWithoutErrors = problems.stream().filter(p->p.parseError == null && p.parsed).collect(Collectors.toList());
    }

    public double getMeanParsingTime(){
        double averageTime = 0;
        for (TptpProblem current : parsedProblemsWithoutErrors){
            averageTime += current.getParseTimeInSeconds();
        }
        return averageTime / parsedProblemsWithoutErrors.size();
    }

    public double getMedianParsingTime(){
        int middle = parsedProblemsWithoutErrors.size()/2;
        return parsedProblemsWithoutErrors.stream()
                .sorted((v1,v2)->Double.compare(v1.getParseTimeInSeconds(),v2.getParseTimeInSeconds()))
                .collect(Collectors.toList())
                .get(middle).getParseTimeInSeconds();
    }

    private HashMap<String,Double> getTimePerMetaIndex(String index){
        HashMap<String,Double> sizeToTime = new HashMap<>();
        for (TptpProblem current : parsedProblemsWithoutErrors){

            String column = current.meta[metaIndexTypes.get(index)];
            // some meta points may be not numbers e.g. - or ?
            if (!column.matches("^\\d+$") && !column.matches("^\\d+.\\d*$")){
                //System.err.println("NAN " + column );
                //continue;
                column = "0";
            }

            Double time = sizeToTime.getOrDefault(column,0.0);
            time += current.getParseTimeInSeconds();

            sizeToTime.put(column, time);
        }

        return sizeToTime;
    }

    private HashMap<String,Double> getAverageTimePerMetaIndex(String index){
        HashMap<String,Double> sizeToTime = new HashMap<>();
        HashMap<String,Integer> sizeToProblems = new HashMap<>();
        for (TptpProblem current : parsedProblemsWithoutErrors){

            String column = current.meta[metaIndexTypes.get(index)];
            // some meta points may be not numbers e.g. - or ?
            if (!column.matches("^\\d+$") && !column.matches("^\\d+.\\d*$")){
                //System.err.println("NAN " + column );
                //continue;
                column = "0";
            }
            Integer numProblems = sizeToProblems.getOrDefault(column, 0);
            numProblems++;
            sizeToProblems.put(column, numProblems);

            Double time = sizeToTime.getOrDefault(column,0.0);
            time += current.getParseTimeInSeconds();

            sizeToTime.put(column, time);
        }
        for (String key : sizeToTime.keySet()){
            sizeToTime.put(key, sizeToTime.get(key) / sizeToProblems.get(key));
        }
        return sizeToTime;
    }

    private HashMap<String,Double> getAverageTimePerCategory(){
        HashMap<String,Double> categoryToTime = new HashMap<>();
        HashMap<String,Integer> categoryToProblems = new HashMap<>();
        for (TptpProblem current : parsedProblemsWithoutErrors){

            String category = current.prefix;

            Integer numProblems = categoryToProblems.getOrDefault(category, 0);
            numProblems++;
            categoryToProblems.put(category, numProblems);

            Double time = categoryToTime.getOrDefault(category,0.0);
            time += current.getParseTimeInSeconds();

            categoryToTime.put(category, time);
        }
        for (String key : categoryToTime.keySet()){
            categoryToTime.put(key, categoryToTime.get(key) / categoryToProblems.get(key));
        }
        return categoryToTime;
    }

    public String getAverageTimePerCategoryLatexCode(){
        int row = 1;
        int column = 1;
        int maxColumns = 10;
        StringBuilder sb = new StringBuilder();
        HashMap<String,Double> categoryToTime = getAverageTimePerCategory();
        List<String> values = new ArrayList<>();
         for (String key : categoryToTime.keySet()){
            if (column == 1){
                sb.append("{\\tabcolsep=0pt\\def\\arraystretch{1.3}\n");
                sb.append("\\begin{tabularx}{350pt}{c *{");
                sb.append(maxColumns-1);
                sb.append("}{>{\\Centering}X}}\n");
            }
            sb.append(key);
            values.add(String.format("%.3f",categoryToTime.get(key)));
            if (column < maxColumns){
                sb.append(" & ");
                column++;
            }
            else {
                sb.append("\\tabularnewline \\midrule\n");
                int i = 1;
                for (String val : values){
                    sb.append(val);
                    if (i < values.size())
                        sb.append(" & ");
                    i++;
                }
                values.clear();
                sb.append("\\tabularnewline\n");
                sb.append("\\end{tabularx}}\n\n");
                column = 1;
                row++;
            }
            if ((row-1) * maxColumns + column == categoryToTime.keySet().size()+1){
                for (int j = column; j < maxColumns; j++) sb.append(" &");
                sb.append("\\tabularnewline \\midrule\n");
                int i = 1;
                for (String val : values) {
                    sb.append(val);
                    if (i < values.size())
                        sb.append(" & ");
                    i++;
                }
                for (int j = column; j < maxColumns; j++) sb.append(" &");
                sb.append("\\tabularnewline\n");
            }
        }
        sb.append("\\end{tabularx}}");
        return sb.toString();
    }

    private String[] getAverageTimePerMetaIndexMatrix(String index){
        HashMap<String,Double> sizeToTime = getAverageTimePerMetaIndex(index);
        // "x" => [1, 2, 3, 4],
        // "y" => [10, 15, 13, 17]
        String[] x = new String[sizeToTime.keySet().size()];
        String[] y = new String[sizeToTime.keySet().size()];
        int i = 0;
        for (String key : sizeToTime.keySet()){
            x[i] = key;
            y[i] = String.valueOf(sizeToTime.get(key));
            i++;
        }
        String xs = String.join(", ",x);
        String ys = String.join(", ",y);
        String[] ret = new String[2];
        ret[0] = "[" + xs + "]";
        ret[1] = "[" + ys + "]";
        return ret;
    }

    private String[] getTimePerMetaIndexMatrix(String index){
        HashMap<String,Double> sizeToTime = getTimePerMetaIndex(index);
        // "x" => [1, 2, 3, 4],
        // "y" => [10, 15, 13, 17]
        String[] x = new String[sizeToTime.keySet().size()];
        String[] y = new String[sizeToTime.keySet().size()];
        int i = 0;
        for (String key : sizeToTime.keySet()){
            x[i] = key;
            y[i] = String.valueOf(sizeToTime.get(key));
            i++;
        }
        String xs = String.join(", ",x);
        String ys = String.join(", ",y);
        String[] ret = new String[2];
        ret[0] = "[" + xs + "]";
        ret[1] = "[" + ys + "]";
        return ret;
    }

    public String getAverageTimePerMetaIndexPythonScript(){
        return getOptionTimePerMetaIndexPythonScript("getAverageTimePerMetaIndexPythonScript");
    }

    public String getTimePerMetaIndexPythonScript(){
        return getOptionTimePerMetaIndexPythonScript("getTimePerMetaIndexPythonScript");
    }

    private String getOptionTimePerMetaIndexPythonScript(String option){
        List<String> scatter = new ArrayList<>();
        List<String> fig = new ArrayList<>();
        List<String> plotTitle = new ArrayList<>();
        int row = 1;
        int column = 1;
        int maxRows = 7;
        int maxColumns = 2;
        for (String key : TptpTestResult.metaIndexTypes.keySet()){
            plotTitle.add("\'" + key + "\'");
            String[] values = null;
            if (option.equals("getAverageTimePerMetaIndexPythonScript"))
                values = getAverageTimePerMetaIndexMatrix(key);
            if (option.equals("getTimePerMetaIndexPythonScript"))
                values = getTimePerMetaIndexMatrix(key);
            scatter.add(key + " = go.Scatter( x=" + values[0] + " , y=" + values[1] + " , mode='markers' , name=\'" + key + "\'" +
                    " , marker = { 'size' : 4 , 'color' : 'rgba(152, 0, 0, .8)' } )");
            fig.add("fig.append_trace( " + key + " , " + row + " , " + column + " )");
            column++;
            if (column > maxColumns){
                column = 1;
                row++;
            }
        }
        String pythonScript = "from plotly import tools\n" +
                "import plotly\n" +
                "import plotly.graph_objs as go\n" +
                "\n" +
                //"Rtng = go.Scatter(x=[1, 2, 3], y=[4, 5, 6])\n" +
                String.join("\n",scatter) +
                "\n" +
                "fig = tools.make_subplots( rows="+ maxRows + " , cols=" + maxColumns + " , subplot_titles=(" +
                String.join(",",plotTitle) + "))\n" +
                "\n" +
                //"fig.append_trace(trace1, 1, 1)\n" +
                String.join("\n",fig) +
                "\n" +
                "fig['layout'].update(height=2800, width=800, title='Parser Performance by Meta Information', showlegend=False )\n" +
                "\n" +
                "plot_url = plotly.offline.plot(fig, filename='parser_performance')";
        return pythonScript;
    }

}