package main;


import util.tree.Node;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Converter {
    Node original;
    Node converted = null;
    String name;
    HashMap<String,Integer> predicateMap;
    HashMap<String,Integer> functionMap;
    HashSet<String> propositions;
    HashSet<String> usedNames;

    private static final Logger log = Logger.getLogger( "default" );

    public Converter(Node original, String name){
        this.original = original;
        this.name = name;
        this.predicateMap = new HashMap<>();
        this.functionMap = new HashMap<>();
        this.propositions = new HashSet<>();
    }

    public ConvertContext convert(){
        converted = original.deepCopy();

        // replace qmf by thf
        converted.dfsRuleAllToplevel("qmf_annotated").stream().forEach(q->{
            q.getFirstChild().getFirstLeaf().setLabel("thf");
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

        // collect propositions
        this.propositions = new HashSet<String>(converted.dfsRuleAll("constant").stream().map(p->p.getFirstLeaf().getLabel()).collect(Collectors.toList()));

        // convert applied predicates
        List<Node> predicates = converted.dfsRuleAllToplevel("plain_term");
        predicates.addAll(converted.dfsRuleAllToplevel("defined_plain_term"));
        predicates.addAll(converted.dfsRuleAllToplevel("system_term"));
        predicates.stream().filter(p->p.getChildren().size()!=1).forEach(p->convertFunctor(p,true));

        // convert applied functions
        List<Node> functions = new ArrayList<>();
        for (Node p : predicates){
            for (Node c : p.getChildren()){
                functions.addAll(c.dfsRuleAll("plain_term"));
                functions.addAll(c.dfsRuleAll("defined_plain_term"));
                functions.addAll(c.dfsRuleAll("system_term"));
            }
        }
        functions.stream().filter(p->p.getChildren().size()!=1).forEach(p->convertFunctor(p,false));

        // check if thf formula names interfere with newly defined symbols
        HashSet<String> usedSymbols = new HashSet(propositions);
        usedSymbols.addAll(this.predicateMap.keySet());
        usedSymbols.addAll(this.functionMap.keySet());
        for (Node n : this.converted.dfsRuleAll("name")){
            Node leaf = n.getFirstLeaf();
            String name = leaf.getLabel();
            int i = 0;
            String t_name = name;
            while (usedSymbols.contains(name)){
                name = t_name + "_" + String.valueOf(i);
                i++;
            }
            usedSymbols.add(name);
            leaf.setLabel(name);
        }
        StringBuilder definitions = new StringBuilder();
        // add types for propositions
        definitions.append("% propositions\n");
        propositions.forEach(p->{
            definitions.append("thf(");
            definitions.append(p);
            definitions.append("_type,type,(");
            definitions.append(p);
            definitions.append(" : ($o))).\n");
        });
        // add types for predicates
        definitions.append("\n% predicates\n");
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
        // add types for functions
        definitions.append("\n% functions\n");
        functionMap.keySet().forEach(k->{
            definitions.append("thf(");
            definitions.append(k);
            definitions.append("_type,type,(");
            definitions.append(k);
            definitions.append(" : (");
            for (int i=1; i <= functionMap.get(k); i++){
                definitions.append("$i>");
            }
            definitions.append("$i))).\n");
        });


        ConvertContext context = new ConvertContext(original,converted,name,definitions.toString());
        //System.out.println(context.getNewProblem());
        return context;
    }

    private void convertFunctor(Node functor, boolean isPredicate){
        // iterate over arguements
        Node argument = functor.getChild(2);
        int arity = 1;
        while (argument.getChildren().size() != 1){
            arity++;
            argument.getChild(1).setLabel("@"); // replace ,
            argument = argument.getLastChild(); // arguments are the rightmost nodes
        }
        if (isPredicate) predicateMap.put(functor.getFirstChild().getFirstLeaf().getLabel(),arity);
        else functionMap.put(functor.getFirstChild().getFirstLeaf().getLabel(),arity);

        // remove parentheses
        functor.delChildAt(1);
        functor.delLastChild();

        // replace all , with @
        List<Node> commas = functor.dfsLabelAll(",");
        commas.stream().forEach(k->k.setLabel("@"));

        // insert application
        Node application = new Node("t_application","@");
        functor.addChildAt(application,1);

        // surround with parentheses
        Node openParen = new Node("t_open_paren","(");
        functor.addChildAt(openParen,0);
        Node closeParen = new Node("t_close_paren",")");
        functor.addChild(closeParen);
    }
}

