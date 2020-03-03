package nju.pa.ats;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-02
 */
public class GetOuterDependencyTest1 {

    private static GetOuterDependencyTest odTest = new GetOuterDependencyTest();


    @Test
    public void test2() throws IOException {
        String javaPath1 = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar0/src/test/java/net/mooctest/AtomSuiteTest3.java";
        String javaPath2 = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar0/src/test/java/net/mooctest/AtomSuiteTest4.java";
        Assert.assertEquals(
                odTest.getImportsFromJavaFile(javaPath1),
                odTest.getImportsFromJavaFile(javaPath2)
        );
    }

    @Test
    public void test1() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
        System.out.println(odTest.getImportsFromJavaFile(javaPath));
    }

    @Test
    public void test0() throws IOException {
        String javaPath1 = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar0/src/test/java/net/mooctest/AStarTest.java";
        String javaPath2 = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar0/src/test/java/net/mooctest/AtomSuiteTest3.java";
        Assert.assertEquals(
                odTest.getImportsFromJavaFile(javaPath1),
                odTest.getImportsFromJavaFile(javaPath2)
        );
    }
}
