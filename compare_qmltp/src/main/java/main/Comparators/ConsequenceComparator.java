package main.Comparators;

import java.util.Comparator;

public class ConsequenceComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();
        if (s1.contains("local")) return -1;
        if (s2.contains("local")) return 1;
        return 0;
    }
}
