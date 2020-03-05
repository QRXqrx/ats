package nju.pa.ats;

import nju.pa.ats.core.text.TextSlicer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public class TextSlicerTest {

    static String outputDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/output/new-tests";
    private static TextSlicer textSlicer;
    static String javaPath1 = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    static String[] javaPaths = new String[] {
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java",
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/MonthTest.java",
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/Triangle/src/test/java/net/mooctest/TriangleTest.java",
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/DataLog/src/test/java/net/mooctest/DatalogTest.java",
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/DataLog/src/test/java/net/mooctest/FactTest.java",
            "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/DataLog/src/test/java/net/mooctest/RuleTest.java"
    };

    @Test
    public void testOutput1() throws IOException {
        for (String javaPath : javaPaths) {
            textSlicer = new TextSlicer(javaPath);
            textSlicer.outputNewTestClassTo(outputDir);
        }
    }

    @Test
    public void testOutput0() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        textSlicer.outputNewTestClassTo(outputDir);
    }

    @Test
    public void testOutput() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        textSlicer.outputNewTestClassTo();
    }
    
    
    @Test
    public void testGenerateNewContent() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        System.out.println(textSlicer.generateNewTestClassContent());
    }

    @Test
    public void testDumpNewTests() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        textSlicer.dumpNewTest();
    }

    @Test
    public void testDumpID() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        textSlicer.dumpInnerDepen();
    }

    @Test
    public void testDumpImports() throws IOException {
        textSlicer = new TextSlicer(javaPath1);
        textSlicer.dumpImports();
    }
}
