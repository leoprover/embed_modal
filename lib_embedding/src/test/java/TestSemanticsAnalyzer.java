import exceptions.AnalysisException;
import exceptions.ParseException;
import exceptions.TransformationException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Test;
import parser.ThfAstGen;
import transformation.Definitions.Connectives;
import transformation.ModalTransformator;
import transformation.SemanticsAnalyzer;
import util.tree.Node;

import java.util.*;
import static org.junit.Assert.*;


public class TestSemanticsAnalyzer {


    public static List<SemanticsTest> semantics;

    @Test
    public void testSemantics() throws ParseException, AnalysisException, TransformationException {
        for (SemanticsTest t : TestSemanticsAnalyzer.semantics){
            Node n = ThfAstGen.parse(t.input, "tPTP_file", t.name).getRoot();
            ModalTransformator modalTransformator = new ModalTransformator(n);
            modalTransformator.transform();
            SemanticsAnalyzer sa = modalTransformator.semanticsAnalyzer;

            assertEquals(t.constantToConstantType.keySet().size(),sa.constantToConstantType.keySet().size());
            for (String key : t.constantToConstantType.keySet()){
                SemanticsAnalyzer.ConstantType val_expected = t.constantToConstantType.get(key);
                SemanticsAnalyzer.ConstantType val_actual = sa.constantToConstantType.get(key);
                assertNotNull(val_actual);
                assertEquals(val_expected,val_actual);
            }
            assertEquals(t.axiomNameToConsequenceType.keySet().size(),sa.axiomNameToConsequenceType.keySet().size());
            for (String key : t.axiomNameToConsequenceType.keySet()){
                SemanticsAnalyzer.ConsequenceType val_expected = t.axiomNameToConsequenceType.get(key);
                SemanticsAnalyzer.ConsequenceType val_actual = sa.axiomNameToConsequenceType.get(key);
                assertNotNull(val_actual);
                assertEquals(val_expected,val_actual);
            }
            assertEquals(t.domainToDomainType.keySet().size(),sa.domainToDomainType.keySet().size());
            for (String key : t.domainToDomainType.keySet()){
                SemanticsAnalyzer.DomainType val_expected = t.domainToDomainType.get(key);
                SemanticsAnalyzer.DomainType val_actual = sa.domainToDomainType.get(key);
                assertNotNull(val_actual);
                assertEquals(val_expected,val_actual);
            }
            assertEquals(t.modalityToAxiomList.keySet().size(),sa.modalityToAxiomList.keySet().size());
            for (String key : t.modalityToAxiomList.keySet()){
                Set<SemanticsAnalyzer.AccessibilityRelationProperty> val_expected = t.modalityToAxiomList.get(key);
                Set<SemanticsAnalyzer.AccessibilityRelationProperty> val_actual = sa.modalityToAxiomList.get(key);
                assertNotNull(val_actual);
                assertTrue(val_expected.containsAll(val_actual));
                assertTrue(val_actual.containsAll(val_expected));
            }
        }
    }

    static{


        semantics = new ArrayList<>();
        SemanticsTest test;
        String input;
        Map<String, SemanticsAnalyzer.ConstantType> constantToConstantType;
        Map<String, SemanticsAnalyzer.ConsequenceType> axiomNameToConsequenceType;
        Map<String, SemanticsAnalyzer.DomainType> domainToDomainType;
        Map<String, Set<SemanticsAnalyzer.AccessibilityRelationProperty>> modalityToAxiomList;
        Set<SemanticsAnalyzer.AccessibilityRelationProperty> relationProperties;

        test = new SemanticsTest();
        input = "thf( 1 , logic , ( $modal := [" +
                "$constants := $rigid ,"+
                "$quantification := $constant ," +
                "$consequence := $global ," +
                "$modalities := [$modal_system_K, $box_int @ 1 := $modal_system_T, $box_int@ 2 := $modal_system_D, " +
                "$box_int @ 3 := [$modal_axiom_T, $modal_axiom_4] ] ] ) ).";
        constantToConstantType = new HashMap<>();
        axiomNameToConsequenceType = new HashMap<>();
        domainToDomainType = new HashMap<>();
        modalityToAxiomList = new HashMap<>();
        constantToConstantType.put(SemanticsAnalyzer.constantDefault, SemanticsAnalyzer.ConstantType.RIGID);
        axiomNameToConsequenceType.put(SemanticsAnalyzer.consequenceDefault, SemanticsAnalyzer.ConsequenceType.GLOBAL);
        domainToDomainType.put(SemanticsAnalyzer.domainDefault, SemanticsAnalyzer.DomainType.CONSTANT);
        modalityToAxiomList.put(SemanticsAnalyzer.modalitiesDefault,SemanticsAnalyzer.modal_systems.get("$modal_system_K"));
        modalityToAxiomList.put(Connectives.box_int_prefix + "_1",SemanticsAnalyzer.modal_systems.get("$modal_system_T"));
        modalityToAxiomList.put(Connectives.box_int_prefix + "_2",SemanticsAnalyzer.modal_systems.get("$modal_system_D"));
        relationProperties = new HashSet<>();
        relationProperties.add(SemanticsAnalyzer.AccessibilityRelationProperty.T);
        relationProperties.add(SemanticsAnalyzer.AccessibilityRelationProperty.FOUR);
        modalityToAxiomList.put(Connectives.box_int_prefix + "_3", relationProperties);
        test.name = "default + box_int";
        test.input = input;
        test.constantToConstantType = constantToConstantType;
        test.axiomNameToConsequenceType = axiomNameToConsequenceType;
        test.domainToDomainType = domainToDomainType;
        test.modalityToAxiomList = modalityToAxiomList;
        semantics.add(test);

    }
}
