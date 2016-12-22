package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class EscapeUtils {

    // graphviz characters
    private static Character[] chars = new Character[]{'+', '{', '}', '(', ')', '[', ']', '&', '^', '-', '?', '*', '\"', '$', '<', '>', '.', '|', '#'};
    private static Set<Character> charmap = new HashSet<Character>(Arrays.asList(chars));

    public static String escapeSpecialCharacters(String s) {
        if (s == null)
            return "";
        StringBuilder ret = new StringBuilder();
        char p = ' ';
        for (char c : s.toCharArray()) {
            if (p != '\\' && charmap.contains(c)) {
                ret.append("\\" + c);
            } else if (p == '\\' && charmap.contains(c)) {
                ret.deleteCharAt(ret.length() - 1);
                ret.append(c);
            } else {
                ret.append(c);
            }
            p = c;
        }
        return ret.toString();
    }

}