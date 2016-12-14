package main;

import util.tree.Node;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TptpProblem {
    public String prefix;
    public String name;
    public String filename;
    public String problem;
    public Node root;
    public Duration parseTime;
    public String[] meta;
    public String parseError;
    public boolean parsed;


    public TptpProblem(){
        parsed = false;
    }

    public Double getParseTimeInSeconds(){
        if (parseTime == null){
            return null;
        }
        return parseTime.getNano()*0.000000001;
    }

    // Problem         Frm V SZS Rtng  Forms   Type  Units  Atoms  EqAts VarAts Symbls  Preds Arity  Funcs Arity   Vars     !>      ^      !      ?  Conns Ariths category parseTime parseError
    //AGT031^1         TH0 S THM 0.43    118     45      0    481     38    183     49      0     -      0     -    111      0     71     34      6 
    public String getMetaWithTimeCsv(){
        String time = String.valueOf(getParseTimeInSeconds());
        List<String> l = new ArrayList<>(Arrays.asList(meta));
        l.add(prefix);
        l.add(time);
        if (parseError == null) l.add("null"); else l.add(parseError);
        return String.join(",",l);
    }
}
