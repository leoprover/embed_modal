package util.tree;

import com.google.common.collect.Lists;
import util.EscapeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Node {

    private String rule = null;
    private String label = null;
    private Node parent = null;
    private List<Node> children;

    public Node() {
        rule = "";
        label = "";
        this.children = new ArrayList<Node>();
    }

    public Node(String rule){
        this.rule = rule;
        if (rule == null) this.rule = "";
        this.label = "";
        this.children = new ArrayList<Node>();
    }

    public Node(String rule, String label){
        this.rule = rule;
        if (rule == null) this.rule = "";
        this.label = label;
        if (label == null) this.label = "";
        this.children = new ArrayList<Node>();
    }

    public Node(Node parent, String rule){
        this.rule = rule;
        if (rule == null) this.rule = "";
        this.parent = parent;
        this.label = "";
        this.children = new ArrayList<Node>();
    }

    public Node(Node parent, String rule, String label){
        this.rule = rule;
        if (rule == null) this.rule = "";
        this.label = label;
        if (label == null) this.label = "";
        this.parent = parent;
        this.children = new ArrayList<Node>();
    }

    public Node copy(){
        Node n = new Node();
        n.rule = this.rule;
        n.label = this.label;
        return n;
    }

    public Node deepCopy() {
        Node root = this.copy();
        root.children = new ArrayList<>();
        for (Node n : this.children) {
            Node child = n.deepCopy();
            child.parent = root;
            root.addChild(child);
        }
        return root;
    }

    public Node getFirstChild() {
        if (!this.children.isEmpty()) {
            return this.children.get(0);
        }
        return null;
    }

    public Node getLastChild() {
        if (this.children.size() > 0) {
            return this.children.get(this.children.size() - 1);
        }
        return null;
    }

    public Node getChild(int i) {
        if (0 <= i && i < this.children.size()){
            return this.children.get(i);
        }
        return null;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Node getParent() {
        return this.parent;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public void addChild(Node newChild) {
        this.children.add(newChild);
        newChild.parent = this;
    }

    public void addChildAt(Node newChild, int at){
        this.children.add(at,newChild);
        newChild.parent = this;
    }

    /*
     * probably not working
    public void addChildAfter(Node newChild, Node afterChild){
        if (this.children.contains(afterChild)){
            this.children.add(this.children.indexOf(afterChild),newChild);
            newChild.parent = this;
        }
    }
    */

    public void delChildAt(int at){
        this.children.remove(at);
    }

    public void delChild(Node n) {
        this.children.remove(n);
    }

    public void delFirstChild(){
        this.children.remove(0);
    }

    public void delLastChild(){
        this.children.remove(this.children.size()-1);
    }

    public void delAllChildren(){
        this.children.clear();
    }
    /*
     * probably not working
    public void replaceChildAt(Node oldChild, Node newChild) {
        if (this.children.contains(oldChild)) {
            this.children.set(this.children.indexOf(oldChild), newChild);
            newChild.parent = this;
        }
    }
    */

    public void replaceChildAt(int oldChild, Node newChild) {
        if (this.children.size() > oldChild) {
            this.children.set(oldChild, newChild);
            newChild.parent = this;
        }
    }

    public String getRule() {
        return this.rule;
    }

    public String getEscapedLabel() {
        return EscapeUtils.escapeSpecialCharacters(this.label);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public List<Node> getLeafsBfs(){
        List<Node> leafs = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.children.isEmpty()){
                leafs.add(current_node);
            }
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return leafs;
    }

    public List<Node> getLeafsDfs(){
        List<Node> leafs = new ArrayList<>();
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.children.isEmpty()){
                leafs.add(current_node);
            }
            for (Node child : Lists.reverse(current_node.getChildren())) {
                nodes_to_visit.push(child);
            }
        }

        return leafs;
    }


    public List<Node> getNodesBfs() {
        List<Node> nodes = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            nodes.add(current_node);
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return nodes;
    }

    /*
     * including THIS node
     */
    public Node getNextTopBranchingNode(){
        Node current = this;
        while (true){
            //System.out.println("TRUE");
            if (current.children.size() > 1){
                return current;
            }
            if (current.getParent() == null || current.getParent() == current){
                return null;
            }
            current = current.getParent();
        }
    }

    /*
     * including THIS node
     */
    public Node getNextBotBranchingNode(){
        Node current = this;
        while (true){
            if (current.children.size() > 1){
                return current;
            }
            if (current.children.isEmpty()){
                return null;
            }
            current = current.getFirstChild();
        }
    }

    public String toDot() {
        StringBuilder dot = new StringBuilder();

        dot.append("graph {\n");
        dot.append("    node [fontname=Helvetica,fontsize=11];\n");
        dot.append("    edge [fontname=Helvetica,fontsize=10];\n");

        List<Node> nodes = this.getNodesBfs();
        // add nodes
        nodes.forEach(
                n -> dot.append("    n" + n.hashCode() + " [label=\"(" + n.hashCode() + ")\\n" +
                        n.getEscapedLabel() + "\\n" + n.getRule().toString() + "\"];\n"));
        // add edges
        nodes.forEach(n -> n.getChildren().stream().filter(c -> c.hasParent()).forEach(c -> dot.append("    n" +
                c.getParent().hashCode() + " -- n" + c.hashCode() + ";\n")));

        dot.append("}\n");

        return dot.toString();
    }

    /**
     * performs depth first search on Ast visiting left children first
     * @param label label of the node which is searched
     * @return node if it could be found
     */
    public Optional<Node> dfsLabel(String label){
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getLabel().equals(label)){
                return Optional.of(current_node);
            }
            for (Node child : Lists.reverse(current_node.children)) {
                nodes_to_visit.add(child);
            }
        }

        return Optional.empty();
    }

    /**
     * performs depth first search on Ast visiting left children first
     * @param rule rule of the node which is searched
     * @return node if it could be found
     */
    public Optional<Node> dfsRule(String rule){
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getRule().equals(rule)){
                return Optional.of(current_node);
            }
            for (Node child : Lists.reverse(current_node.children)) {
                nodes_to_visit.add(child);
            }
        }

        return Optional.empty();
    }

    /**
     * performs depth first search on Ast visiting left children first
     * @param label label of the node which is searched
     * @return list of found nodes
     */
    public List<Node> dfsLabelAll(String label){
        List<Node> all = new ArrayList<>();
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getLabel().equals(label)){
                all.add(current_node);
            }
            for (Node child : Lists.reverse(current_node.children)) {
                nodes_to_visit.add(child);
            }
        }

        return all;
    }

    /**
     * performs depth first search on Ast visiting left children first
     * @param rule rule of the node which is searched
     * @return list of found nodes
     */
    public List<Node> dfsRuleAll(String rule){
        List<Node> all = new ArrayList<>();
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getRule().equals(rule)){
                all.add(current_node);
            }
            for (Node child : Lists.reverse(current_node.children)) {
                nodes_to_visit.add(child);
            }
        }

        return all;
    }

    /**
     * performs depth first search on Ast visiting left children first and pruning all children of found nodes
     * @param label label of the node which is searched
     * @return list of found nodes
     */
    public List<Node> dfsLabelAllToplevel(String label){
        List<Node> all = new ArrayList<>();
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getLabel().equals(label)){
                all.add(current_node);
            }
            else {
                for (Node child : Lists.reverse(current_node.children)) {
                    nodes_to_visit.add(child);
                }
            }
        }

        return all;
    }

    /**
     * performs depth first search on Ast visiting left children first and pruning all children of found nodes
     * @param rule rule of the node which is searched
     * @return list of found nodes
     */
    public List<Node> dfsRuleAllToplevel(String rule){
        List<Node> all = new ArrayList<>();
        Stack<Node> nodes_to_visit = new Stack<>();
        nodes_to_visit.push(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.pop();
            if (current_node.getRule().equals(rule)){
                all.add(current_node);
            }
            else {
                for (Node child : Lists.reverse(current_node.children)) {
                    nodes_to_visit.add(child);
                }
            }
        }

        return all;
    }


    /**
     * performs breadth first search on Ast visiting left children first
     * @param label label of the node which is searched
     * @return node if it could be found
     */
    public Optional<Node> bfsLabel(String label){
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getLabel().equals(label)){
                return Optional.of(current_node);
            }
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return Optional.empty();
    }

    /**
     * performs breadth first search on Ast visiting left children first
     * @param rule rule of the node which is searched
     * @return node if it could be found
     */
    public Optional<Node> bfsRule(String rule){
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getRule().equals(rule)){
                return Optional.of(current_node);
            }
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return Optional.empty();
    }

    /**
     * performs breadth first search on Ast visiting left children first
     * @param label label of the node which is searched
     * @return list of found nodes
     */
    public List<Node> bfsLabelAll(String label){
        List<Node> all = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getLabel().equals(label)){
                all.add(current_node);
            }
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return all;
    }

    /**
     * performs breadth first search on Ast visiting left children first
     * @param rule rule of the node which is searched
     * @return list of found nodes
     */
    public List<Node> bfsRuleAll(String rule){
        List<Node> all = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getRule().equals(rule)){
                all.add(current_node);
            }
            for (Node child : current_node.getChildren()) {
                nodes_to_visit.add(child);
            }
        }

        return all;
    }

    /**
     * performs breadth first search on Ast visiting left children first and pruning all children of found nodes
     * @param label label of the node which is searched
     * @return list of found nodes
     */
    public List<Node> bfsLabelAllToplevel(String label){
        List<Node> all = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getLabel().equals(label)){
                all.add(current_node);
            }
            else {
                for (Node child : current_node.getChildren()) {
                    nodes_to_visit.add(child);
                }
            }
        }

        return all;
    }

    /**
     * performs breadth first search on Ast visiting left children first and pruning all children of found nodes
     * @param rule rule of the node which is searched
     * @return list of found nodes
     */
    public List<Node> bfsRuleAllToplevel(String rule){
        List<Node> all = new ArrayList<>();
        Queue<Node> nodes_to_visit = new LinkedList<>();
        nodes_to_visit.add(this);

        while( !nodes_to_visit.isEmpty() ) {
            Node current_node = nodes_to_visit.poll();
            if (current_node.getRule().equals(rule)){
                all.add(current_node);
            }
            else {
                for (Node child : current_node.getChildren()) {
                    nodes_to_visit.add(child);
                }
            }
        }

        return all;
    }

    public boolean hasOneLeaf(){
        Node node = this;
        while (true) {
            if (node.isLeaf()){
                return true;
            }
            if (node.children.size() > 1){
                return false;
            }
            node = node.getFirstChild();
        }
    }

    public Node getFirstLeaf(){
        Node node = this;
        while (true) {
            if (node.isLeaf()){
                return node;
            }
            node = node.getFirstChild();
        }
    }

    public boolean leafsAre(Predicate<Node> p){
        List<Node> leafs = this.getLeafsDfs();
        for (Node l : leafs){
            if (!p.test(l)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.rule.toString() + " " + this.toStringLeafs();
    }

    public String toStringLeafs(){
        return this.getLeafsDfs().stream().map(l->l.getLabel()).collect(Collectors.joining());
    }

    /*
    public String toStringTree(){

    }
    */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }

        Node n = (Node) o;

        return n.rule.equals(this.rule) &&
                n.label.equals(this.label) &&
                this.children.equals(n.children);
    }
/*
    public boolean deepEquals(Object o){
        if (!(o instanceof Node)) {
            return false;
        }
        Node n = (Node) o;
        boolean label = false;
        boolean rule = false;
        if (this.label == null && n.label == null){
            label = true;
        }
        if (this.rule == null && n.label == null){
            rule = true;
        }
        return
                (label || this.label.equals(n.label) &&
                ( rule || this.label.equals(n.label)) &&

    }
*/
    /*
 * tPTP_input nodes
 * @return tPTP_input nodes
 */
    public List<Node> findtPTP_inputs(){
        Node tPTPLevel = this.getChild(0);
        return tPTPLevel.getChildren();
    }

    /*
 * formatted output
 * @return formatted output
 */
    public String toStringWithLinebreaksFormatted(){
        List<Node> tPTPInputs = findtPTP_inputs();
        List<String> output = new ArrayList<>();
        for (Node current : tPTPInputs){
            if (!current.getChild(0).getRule().equals("comment")) {
                output.add("\n");
                output.add(current.toStringLeafs().trim());
                output.add("\n\n");
            }else{
                output.add(current.toStringLeafs().trim());
                output.add("\n");
            }
        }
        return String.join("",output).trim();
    }

    /*
* one line per sentence output
* @return formatted output
*/
    public String toStringWithLinebreaks() {
        List<Node> tPTPInputs = findtPTP_inputs();
        List<String> output = new ArrayList<>();
        for (Node current : tPTPInputs) {
            output.add(current.toStringLeafs().trim());
            output.add("\n");
        }
        return String.join("", output).trim();
    }

    /*
     * puts % before every line
     */
    public String toStringWithLinebreaksCommented() {
        List<Node> tPTPInputs = findtPTP_inputs();
        List<String> output = new ArrayList<>();
        for (Node current : tPTPInputs) {
            output.add("%");
            output.add(current.toStringLeafs().trim());
            output.add("\n");
        }
        return String.join("", output).trim();
    }


}
