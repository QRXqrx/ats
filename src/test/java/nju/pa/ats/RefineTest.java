package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.staticpa.StaticSlicer;
import nju.pa.ats.core.staticpa.SlicerConfig;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-17
 */
public class RefineTest {

    String javaPathALU = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
    String classDirALU = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/target/test-classes/net/mooctest";
    String javaPathAstar = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    String classDirAstar = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";


    @Test
    public void test8() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("178");
        list.add("123");
        System.out.println(list);
        list.remove("123");
        System.out.println(list);
    }

    @Test
    public void test7() throws IOException {
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirAstar, this.getClass().getClassLoader());
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.NONE,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );

        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");
        StaticSlicer staticSlicer = new StaticSlicer(config, javaPathAstar, targetMethods);
        List<AtomTestCase> atomTestCases = staticSlicer.slice();
        System.out.println(atomTestCases.size());
        staticSlicer.setDistinct(true);
        atomTestCases = staticSlicer.slice();
        System.out.println(atomTestCases.size());
    }

    @Test
    public void test6() throws IOException {
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirAstar, this.getClass().getClassLoader());
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.NONE,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );

        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");
        StaticSlicer staticSlicer = new StaticSlicer(config, javaPathAstar, targetMethods);
        List<AtomTestCase> atomTestCases = staticSlicer.slice();

        List<AtomTestCase> atsAfterDistinct = atomTestCases.stream()
                .distinct()
                .collect(Collectors.toList());

        atsAfterDistinct.forEach((atc) -> {
            System.out.println(atc.dumpSnippet());
            System.out.println("-----------------------------------------------------");
        });
        System.out.println(atsAfterDistinct.size());
    }

    @Test
    public void test5() {
        List<String> srcLines1 = new ArrayList<>();
        srcLines1.add("qwe");
        srcLines1.add("  qwe");
        srcLines1.add("      qwe   ");

        List<String> srcLines2 = new ArrayList<>();
        srcLines2.add("qwe");
        srcLines2.add("  qwe");
        srcLines2.add("      qwe   ");

        List<AtomTestCase> list = new ArrayList<>();
        AtomTestCase atc1 = new AtomTestCase("test1", srcLines1);
        AtomTestCase atc2 = new AtomTestCase("test2", srcLines2);
        AtomTestCase atc3 = new AtomTestCase("test3", srcLines1);
        list.add(atc1);
        list.add(atc2);
        list.add(atc3);

        System.out.println(atc1.equals(atc2));

        System.out.println(list);

        List<AtomTestCase> list1 = list.stream().distinct().collect(Collectors.toList());
        System.out.println(list1);

        System.out.println();
    }


    @Test
    public void test4() throws IOException {
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirAstar, this.getClass().getClassLoader());
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.NONE,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );

        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");
        StaticSlicer staticSlicer = new StaticSlicer(config, javaPathAstar, targetMethods);
        List<AtomTestCase> atomTestCases = staticSlicer.slice();

        List<AtomTestCase> atsAfterDistinct = atomTestCases.stream()
                .distinct()
                .collect(Collectors.toList());

        System.out.println(atsAfterDistinct.size());
    }

    @Test
    public void test3() throws IOException {
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDirAstar, this.getClass().getClassLoader());
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.NONE,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );

        List<String> targetMethods = new ArrayList<>();
        targetMethods.add("assert");
        StaticSlicer staticSlicer = new StaticSlicer(config, javaPathAstar, targetMethods);
        List<AtomTestCase> atomTestCases = staticSlicer.slice();

        atomTestCases.forEach((ats) -> {
            System.out.println(ats.dumpSnippet());
            System.out.println("----------------------------------------");
        });

        System.out.println(atomTestCases.size());
    }


    @Test
    public void test2() {
        List<String> list = new ArrayList<>();

        list.add("  1 23    ");
        list.add("1  3   ");

        List<String> list1 = list.stream().map(String::trim).collect(Collectors.toList());
        System.out.println(list1);
    }

    @Test
    public void test1() {
        List<String> list = new ArrayList<>();

        list.add("123");
        list.add("13");

        List<String> list1 = new ArrayList<>();
        list1.add("13");
        list1.add("123");


        System.out.println(list.equals(list1));
    }
    @Test
    public void test0() {
        List<String> list = new ArrayList<>();

        list.add("123");
        list.add("123");
        list.add("123");
        list.add("14");
        list.add("14");
        list.add("133");

        List<String> afterDistinct = list.stream().distinct().collect(Collectors.toList());
        System.out.println(afterDistinct);
        System.out.println(afterDistinct.size());
    }
}
