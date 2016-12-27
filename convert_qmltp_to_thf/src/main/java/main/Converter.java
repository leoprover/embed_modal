package main;


import exceptions.ParseException;
import parser.ParseContext;
import util.tree.Node;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Converter {
    Node original;
    Node converted = null;
    String name;
    HashMap<String,Integer> predicateMap; // {$i}^n -> $o
    HashMap<String,Integer> functionMap; // {$i}^n -> $i
    HashSet<String> propositions; // constants of $o
    HashSet<String> constantIndividuals; // constants of $i
    HashSet<String> usedSymbols;
    HashSet<String> modal_identifiers; // box @ modal_identifier @ modal_body
    String defaultIndexType;

    private static final Logger log = Logger.getLogger( "default" );

    public Converter(Node original, String name){
        this.original = original;
        this.name = name;
        this.predicateMap = new HashMap<>();
        this.functionMap = new HashMap<>();
        this.propositions = new HashSet<>();
        this.usedSymbols = new HashSet<>();
        this.modal_identifiers = new HashSet<>();
        this.defaultIndexType = "index_type";
        this.defaultIndexType = getUnusedName(defaultIndexType);
    }

    public ConvertContext convert() throws ConversionException{
        converted = original.deepCopy();
        // if (true) return new ConvertContext(original,converted,"asd","asd"); // parser debug mode

        // correct Fof errors
        correctSyntax();

        // replace qmf by thf
        converted.dfsRuleAllToplevel("qmf_annotated").stream().forEach(q->{
            q.getFirstChild().getFirstLeaf().setLabel("thf");
        });

        // replace single box dia
        List<Node> modal_nodes = converted.dfsRuleAll("fof_modal");
        modal_nodes.forEach(n->{
            if (n.getChild(0).getLabel().equals("#box")){
                n.getChild(0).setLabel("$box");
            } else if (n.getChild(0).getLabel().equals(("#dia"))){
                n.getChild(0).setLabel("$dia");
            }
            n.getChild(1).setLabel("@"); // open paren -> @
        });

        // replace multimodal box and dia
        List<Node> multimodal_nodes = converted.dfsRuleAll("fof_multimodal");
        multimodal_nodes.forEach(n->{
            if (n.getChild(0).getLabel().equals("#box")){
                n.getChild(0).setLabel("$box");
            } else if (n.getChild(0).getLabel().equals(("#dia"))){
                n.getChild(0).setLabel("$dia");
            }
            modal_identifiers.add(n.getChild(2).getLastChild().getLabel());
            n.getChild(1).setLabel("@"); // open paren -> @
            n.getChild(3).setLabel("@"); // closing paren -> @
            n.delChildAt(4); // remove :
            Node index_type = new Node("t_modal_index_type",defaultIndexType);
            n.addChildAt(index_type,1);
            Node at_index_type = new Node("t_modal_index_type_at","@");
            n.addChildAt(at_index_type,1);
        });

        // add types to all quantified variables
        List<Node> fof_variable_lists = converted.dfsRuleAll("fof_variable_list");
        fof_variable_lists.stream().forEach(n -> {
            Node type = new Node( "t_type_introduced" , ":$i");
            n.addChildAt(type,1);
        });

        // collect individual constants
        // filter: on all constants go up in tree until a rule "arguments" is found then its inside a function/proposition
        // or a branching node is found
        this.constantIndividuals = new HashSet<String>(converted.dfsRuleAll("constant").stream()
                .filter(p->{
                    Node parent = p.getParent();
                    while (parent.getChildren().size() == 1){
                        if (parent.getRule().equals("arguments")) return true;
                        parent = parent.getParent();
                    }
                    return false;
                })
                .map(p->p.getFirstLeaf().getLabel()).collect(Collectors.toList()));

        // collect propositions
        // filter: constants which are not individual constants
        this.propositions = new HashSet<String>(converted.dfsRuleAll("constant").stream()
                .filter(p->!this.constantIndividuals.contains(p.getFirstLeaf().getLabel()))
                .map(p->p.getFirstLeaf().getLabel()).collect(Collectors.toList()));

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
        this.usedSymbols = new HashSet(this.propositions);
        this.usedSymbols.addAll(this.constantIndividuals);
        this.usedSymbols.addAll(this.predicateMap.keySet());
        this.usedSymbols.addAll(this.functionMap.keySet());
        for (Node n : this.converted.dfsRuleAll("name")){
            Node leaf = n.getFirstLeaf();
            String name = leaf.getLabel();
            name = this.getUnusedName(name);
            leaf.setLabel(name);
        }

        StringBuilder definitions = new StringBuilder();
        // convert semantics
        definitions.append("% semantics\n");
        List<Node> semantics = converted.dfsRuleAll("tpi_sem_formula");
        for (Node s : semantics){
            definitions.append("thf(");
            definitions.append(getUnusedName("semantics"));
            definitions.append(",logic,($modal :=\n");
            List<String> modal_keywords = s.dfsRuleAll("modal_keyword").stream().map(k->k.getLastChild().getFirstLeaf().getLabel()).collect(Collectors.toList());
            //modal_keywords.forEach(n-> System.out.println(":" + n + ":"));
            definitions.append("[$constants := ");
            if (modal_keywords.contains("rigid")) definitions.append("$rigid");
            else definitions.append("$flexible");
            definitions.append(",\n");
            definitions.append("$quantification := ");
            if (modal_keywords.contains("cumulative")) definitions.append("$cumulative");
            else if (modal_keywords.contains("decreasing")) definitions.append("$decreasing");
            else if (modal_keywords.contains("varying")) definitions.append("$varying");
            else definitions.append("$constant");
            definitions.append(",\n");
            definitions.append("$consequence := ");
            if (modal_keywords.contains("local")) definitions.append("$local");
            else definitions.append("$global");
            definitions.append(",\n");
            List<Node> modality_pairs = s.dfsRuleAll("modality_pair");
            definitions.append("$modalities := [");
            for (int i = 1;i <= modality_pairs.size();i++){
                String modal_identifier = modality_pairs.get(i-1).getChild(1).getFirstLeaf().getLabel();
                String modal_system = modality_pairs.get(i-1).getChild(3).getFirstLeaf().getLabel();
                //System.out.println(modal_identifier + ":" + modal_system);
                definitions.append(modal_identifier);
                definitions.append(" := ");
                definitions.append(mapModalSystem(modal_system));
                if (i != modality_pairs.size()) definitions.append(" , ");
            }
            definitions.append("])).\n\n");
        }
        // declare multimodal accessibility relations
        definitions.append("% modalities\n");
        if (multimodal_nodes.size() != 0){
            definitions.append("thf(");
            definitions.append(getUnusedName("index_type_type"));
            definitions.append(" , type , (");
            definitions.append(defaultIndexType);
            definitions.append(" : $tType ) ).\n");
        }
        for (Node s : semantics){
            List<String> modalitiy_identifiers = s.dfsRuleAll("modality_pair").stream()
                    .map(p->p.getChild(1).getFirstLeaf().getLabel()).collect(Collectors.toList());
            for (String modality : modalitiy_identifiers){
                definitions.append("thf(");
                definitions.append(getUnusedName("rel_" + modality + "_type"));
                definitions.append(",type,(");
                definitions.append(modality);
                definitions.append(" : ");
                definitions.append(defaultIndexType);
                definitions.append(")).\n");
            }
        }
        // add types for propositions
        definitions.append("\n% propositions\n");
        this.propositions.forEach(p->{
            definitions.append("thf(");
            definitions.append(p);
            definitions.append("_type,type,(");
            definitions.append(p);
            definitions.append(" : ($o))).\n");
        });
        // add types for individual constants
        definitions.append("\n% individual constants\n");
        this.constantIndividuals.forEach(p->{
            definitions.append("thf(");
            definitions.append(p);
            definitions.append("_type,type,(");
            definitions.append(p);
            definitions.append(" : ($i))).\n");
        });
        // add types for predicates
        definitions.append("\n% predicates\n");
        this.predicateMap.keySet().forEach(k->{
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
        this.functionMap.keySet().forEach(k->{
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

    private void correctSyntax(){
        // surround negated operand with parentheses, exclude infix inequality terms
        this.converted.dfsRuleAll("fof_unary_formula").stream().filter(n->n.getChildren().size()!=1).forEach(n->{
            Node oParen = new Node ("t_o_paren","(");
            n.addChildAt(oParen,1);
            Node cParen = new Node ("t_c_paren",")");
            n.addChild(cParen);
        });
    }

    private String getUnusedName(String name){
        int i = 0;
        String t_name = name;
        while (this.usedSymbols.contains(name)){
            name = t_name + "_" + String.valueOf(i);
            i++;
        }
        this.usedSymbols.add(name);
        return name;
    }

    private String mapModalSystem(String system) throws ConversionException{
        if (system.equals("k")) return "$modal_system_K";
        else if (system.equals("t")) return "$modal_system_T";
        else if (system.equals("d")) return "$modal_system_D";
        else if (system.equals("s4")) return "$modal_system_S4";
        else if (system.equals("s5")) return "$modal_system_S5";
        else throw new ConversionException(system + " is not a valid modal system");
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

