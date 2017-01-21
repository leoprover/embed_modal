package main.Comparators;

import java.util.Comparator;

public class CombinedComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        int consequence = new ConsequenceComparator().compare(o1,o2);
        if (consequence != 0) return consequence;
        int system = new SystemComparator().compare(o1,o2);
        if (system != 0) return system;
        int domains = new DomainComparator().compare(o1,o2);
        return domains;
        //return 0;
    }
}
