package nju.pa.ats;

import nju.pa.ats.util.DependencyUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-04
 */
public class DependencyUtilTest {

    private void dumpTests(List<String> testStrs) {
        testStrs.forEach((test) -> {
            System.out.println("------------------------------------------------");
            System.out.print(test);
            System.out.println("------------------------------------------------");
        });
    }
    
    @Test
    public void testGetTests5() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/DayTest.java";
        dumpTests(DependencyUtil.getAllTestsFromJavaFile(javaPath));
    }
    @Test
    public void testGetTests4() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
        dumpTests(DependencyUtil.getAllTestsFromJavaFile(javaPath));
    }
    @Test
    public void testGetTests3() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/YearTest.java";
        dumpTests(DependencyUtil.getAllTestsFromJavaFile(javaPath));
    }
    @Test
    public void testGetTests2() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        dumpTests(DependencyUtil.getAllTestsFromJavaFile(javaPath));
    }
    @Test
    public void testGetTests1() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/Triangle/src/test/java/net/mooctest/TriangleTest.java";
        dumpTests(DependencyUtil.getAllTestsFromJavaFile(javaPath));
    }
    
    
    private void printOD(String javaPath) {
        try {
            System.out.println(DependencyUtil.getAllInnerDependenciesFromJavaFile(javaPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOuter6() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/Triangle/src/test/java/net/mooctest/TriangleTest.java";
        printOD(javaPath);
    }
    @Test
    public void testOuter5() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/YearTest.java";
        printOD(javaPath);
    }
    @Test
    public void testOuter4() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/NextdayTest.java";
        printOD(javaPath);
    }
    @Test
    public void testOuter3() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/MonthTest.java";
        printOD(javaPath);
    }
    @Test
    public void testOuter2() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/DayTest.java";
        printOD(javaPath);
    }
    @Test
    public void testOuter1() {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/DateTest.java";
        printOD(javaPath);
    }


    @Test
    public void testInner1() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest/DateTest.java";
        List<String> actual = DependencyUtil.getImportsFromJavaFile(javaPath);
        String[] imports = new String[]{
                "import static org.junit.Assert.*;",
                "import java.io.ByteArrayOutputStream;",
                "import java.io.PrintStream;",
                "import org.junit.BeforeClass;",
                "import org.junit.Rule;",
                "import org.junit.Test;",
                "import org.junit.rules.ExpectedException;"
        };
        List<String> expected = Arrays.asList(imports);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInner0() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/Triangle/src/test/java/net/mooctest/TriangleTest.java";
        List<String> actual = DependencyUtil.getImportsFromJavaFile(javaPath);
        String[] imports = new String[]{
                "import static org.junit.Assert.*;",
                "import org.junit.Ignore;",
                "import org.junit.Test;"
        };
        List<String> expected = Arrays.asList(imports);
        Assert.assertEquals(expected, actual);
    }
}
