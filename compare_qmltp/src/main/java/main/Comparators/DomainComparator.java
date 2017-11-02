package main.Comparators;

import java.util.Comparator;

public class DomainComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();
        if (s1.contains("vary") && s2.contains("vary")) return 0;
        if (s1.contains("cumul") && s2.contains("cumul")) return 0;
        if (s1.contains("decr") && s2.contains("decr")) return 0;
        if (s1.contains("const") && s2.contains("const")) return 0;
        //String kk1 = s1.substring(0,2);
        //String kk2 = s2.substring(0,2);
        //if (kk1.equals(kk2)) return 0;
        if (s1.contains("vary")) return -1;
        if (s2.contains("vary")) return 1;
        if (s1.contains("cumul")) return -1;
        if (s2.contains("cumul")) return 1;
        if (s1.contains("decr")) return -1;
        if (s2.contains("decr")) return 1;
        if (s1.contains("const")) return -1;
        if (s2.contains("const")) return 1;
        return 0;
    }
}
