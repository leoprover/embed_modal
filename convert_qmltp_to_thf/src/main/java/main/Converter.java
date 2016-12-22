package main;


import util.tree.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class Converter {
    Node original;
    Node converted = null;
    String name;
    HashMap<String,Integer> predicateMap;

    private static final Logger log = Logger.getLogger( "default" );

    public Converter(Node original, String name){
        this.original = original;
        this.name = name;
        this.predicateMap = new HashMap<>();
    }

    public ConvertContext convert(){
        converted = original.deepCopy();

        // replace qmf by thf
        converted.dfsRuleAllToplevel("qmf_annotated").stream().forEach(q->{
            q.getChild(0).getFirstLeaf().setLabel("thf");
        });

        // replace box dia colon
        List<Node> modal_nodes = converted.dfsRuleAll("fof_modal");
        modal_nodes.stream().forEach(n->{
            if (n.getChild(0).getLabel().equals("#box")){
                n.getChild(0).setLabel("$box");
            } else if (n.getChild(0).getLabel().equals(("#dia"))){
                n.getChild(0).setLabel("$dia");
            }
            n.getChild(1).setLabel("@");
        });

        // add types to all quantified variables
        List<Node> fof_variable_lists = converted.dfsRuleAll("fof_variable_list");
        fof_variable_lists.stream().forEach(n -> {
            Node type = new Node( "t_type_introduced" , ":$i");
            n.addChildAt(type,1);
        });

        // applied predicates
        // add application and parentheses
        List<Node> predicates = converted.dfsRuleAll("plain_term");
        predicates.addAll(converted.dfsRuleAll("defined_plain_term"));
        predicates.addAll(converted.dfsRuleAll("system_term"));
        predicates.stream().filter(p->p.getChildren().size()!=1).forEach(p->{

            // iterate over arguements
            Node argument = p.getChild(2);
            int arity = 1;
            while (argument.getChildren().size() != 1){
                arity++;
                argument.getChild(1).setLabel("@"); // replace ,
                argument = argument.getLastChild(); // arguments are the rightmost nodes
            }
            predicateMap.put(p.getFirstChild().getFirstLeaf().getLabel(),arity);

            // remove parentheses
            p.delChildAt(1);
            p.delLastChild();

            // replace all , with @
            List<Node> commas = p.dfsLabelAll(",");
            commas.stream().forEach(k->k.setLabel("@"));

            // insert application
            Node application = new Node("t_application","@");
            p.addChildAt(application,1);

            // surround with parentheses
            Node openParen = new Node("t_open_paren","(");
            p.addChildAt(openParen,0);
            Node closeParen = new Node("t_close_paren",")");
            p.addChild(closeParen);


        });

        // all predicates
        // add to predicate set
        //List<Node> functors = converted.dfsRuleAll("functor");
        //functors.addAll(converted.dfsRuleAll("defined_functor"));
        //functors.addAll(converted.dfsRuleAll("system_functor"));

        StringBuilder definitions = new StringBuilder();
        predicateMap.keySet().forEach(k->{
            definitions.append("thf(");
            definitions.append(k);
            definitions.append("_type,type,(");
            definitions.append(k);
            definitions.append(" : (");
            for (int i=1; i <= predicateMap.get(k); i++){
                definitions.append("$i>");
            }
            definitions.append("$o))).\n");
        });

        ConvertContext context = new ConvertContext(original,converted,name,definitions.toString());
        //System.out.println(context.getNewProblem());
        return context;
    }


}

