package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.slicer.Slicer;
import nju.pa.ats.core.slicer.SlicerConfig;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-15
 */
public class SlicerTest1 {

    String javaPathALU = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
    String classDirALU = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/target/test-classes/net/mooctest";
    String javaPathAstar = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    String classDirAstar = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";;

    /*SlicerConfig config = new SlicerConfig(
            scope,
            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.FULL,
            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
    );
    SlicerConfig config = new SlicerConfig(
            scope,
            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
    );
    SlicerConfig config = new SlicerConfig(
            scope,
            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
    );*/

    @Test
    public void testRunAstar() throws IOException {

        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirAstar, this.getClass().getClassLoader());

        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );


        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");

        Slicer mySlicer = new Slicer(config, javaPathAstar, targetMethods);

        List<AtomTestCase> atomTestCases = mySlicer.backwardSlice();
        System.out.println(atomTestCases.size());
        atomTestCases.forEach((ats) -> {
            System.out.println(ats.dumpSnippet());
            System.out.println("--------------------------------------------------------------------");
        });
    }



    @Test
    public void testRunALU() throws IOException {

        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirALU, this.getClass().getClassLoader());

        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );


        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");

        Slicer mySlicer = new Slicer(config, javaPathALU, targetMethods);

        List<AtomTestCase> atomTestCases = mySlicer.backwardSlice();
        System.out.println(atomTestCases.size());
        atomTestCases.forEach((ats) -> {
            System.out.println(ats.dumpSnippet());
            System.out.println("--------------------------------------------------------------------");
        });
    }
}
