package main.Comparators;

import java.util.Comparator;

public class SystemComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();
        String k1 = s1.substring(0,1);
        String k2 = s2.substring(0,1);
        String kk1 = s1.substring(0,2);
        String kk2 = s2.substring(0,2);
        if (kk1.equals(kk2)) return 0;
        if (k1.equals("k")) return -1;
        if (k2.equals("k")) return 1;
        if (k1.equals("d")) return -1;
        if (k2.equals("d")) return 1;
        if (k1.equals("t")) return -1;
        if (k2.equals("t")) return 1;
        if (kk1.equals("s4")) return -1;
        if (kk2.equals("s4")) return 1;
        if (kk1.equals("s5")) return -1;
        if (kk2.equals("s5")) return 1;
        return 0;
    }
}
