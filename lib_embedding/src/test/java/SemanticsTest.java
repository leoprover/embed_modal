import transformation.SemanticsAnalyzer;

import java.util.Map;
import java.util.Set;

public class SemanticsTest{
    String name;
    String input;
    Map<String, SemanticsAnalyzer.ConstantType> constantToConstantType;
    Map<String, SemanticsAnalyzer.ConsequenceType> axiomNameToConsequenceType;
    Map<String, SemanticsAnalyzer.DomainType> domainToDomainType;
    Map<String, Set<SemanticsAnalyzer.AccessibilityRelationProperty>> modalityToAxiomList;
}