package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.util.CancelException;
import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.slicer.Slicer;
import nju.pa.ats.core.slicer.SlicerConfig;
import nju.pa.ats.util.AtomUtil;
import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public class SlicerTest {


    // Slice AStar with cdo = NO_EXCEPTIONAL_EDGES // ddo = REFLECTION
    @Test
    public void testFindSeedAStar2() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDir, this.getClass().getClassLoader());
        /*SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.FULL,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );*/
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );
        /*SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );*/
        Slicer mySlicer = new Slicer(config, javaPath);

        CallGraph cg = mySlicer.getConfig().getCallGraph();

        List<Statement> seeds = new ArrayList<>();
        String targetMethodName = "assert";

        cg.stream()
                .filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    IR ir = node.getIR();
                    Iterator<SSAInstruction> it = ir.iterateAllInstructions();
                    while(it.hasNext()) {
                        SSAInstruction ins = it.next();

                        if(ins.iIndex() >= 0) {
                            Statement statement = new NormalStatement(node, ins.iIndex());
                            int lineNumber = AtomUtil.srcLineNumberOf(statement);
                            String line = " ";
                            try {
                                line = FileUtil.readContentByLineNumber(javaPath, lineNumber);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(line.contains(targetMethodName)) {
                                seeds.add(statement);
                            }
                        } else {
                            System.out.println("Negative iIndex:" + ins.iIndex() + "-" + ins);
                        }

                    }
                });
        int cnt = 0;
        for (Statement seed : seeds) {
            System.out.println("seed: " + seed);
            System.out.println("SeedLine" + FileUtil.readContentByLineNumber(javaPath, AtomUtil.srcLineNumberOf(seed)));
            AtomTestCase atc = mySlicer.backwardSliceOne(seed, String.valueOf(cnt));
            cnt++;
            System.out.println(atc.dumpSnippet());
            System.out.println("---------------------------------------------------------------------");
        }

    }


    // Slice ALU with cdo = NO_EXCEPTIONAL_EDGES // ddo = REFLECTION
    @Test
    public void testFindSeedALU2() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/target/test-classes/net/mooctest";

        AnalysisScope scope = SlicerUtil.getDynamicScope(classDir, this.getClass().getClassLoader());
        /*SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.FULL,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );*/
        /*SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES
        );*/
        SlicerConfig config = new SlicerConfig(
                scope,
                com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.REFLECTION,
                com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.FULL
        );

        Slicer mySlicer = new Slicer(config, javaPath);

        CallGraph cg = mySlicer.getConfig().getCallGraph();

        List<Statement> seeds = new ArrayList<>();
        String targetMethodName = "assert";

        cg.stream()
                .filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    IR ir = node.getIR();
                    Iterator<SSAInstruction> it = ir.iterateAllInstructions();
                    while(it.hasNext()) {
                        SSAInstruction ins = it.next();

                        if(ins.iIndex() >= 0) {
                            Statement statement = new NormalStatement(node, ins.iIndex());
                            int lineNumber = AtomUtil.srcLineNumberOf(statement);
                            String line = " ";
                            try {
                                line = FileUtil.readContentByLineNumber(javaPath, lineNumber);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(line.contains(targetMethodName)) {
                                seeds.add(statement);
                            }
                        } else {
                            System.out.println("Negative iIndex:" + ins.iIndex() + "-" + ins);
                        }

                    }
                });
        int cnt = 0;
        for (Statement seed : seeds) {
            System.out.println("seed: " + seed);
            System.out.println("SeedLine" + FileUtil.readContentByLineNumber(javaPath, AtomUtil.srcLineNumberOf(seed)));
            AtomTestCase atc = mySlicer.backwardSliceOne(seed, String.valueOf(cnt));
            cnt++;
            System.out.println(atc.dumpSnippet());
            System.out.println("---------------------------------------------------------------------");
        }
    }


    // Slice AStar
    @Test
    public void testFindSeedAstar1() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDir, this.getClass().getClassLoader());

        Slicer mySlicer = new Slicer(scope, javaPath);
        CallGraph cg = mySlicer.getConfig().getCallGraph();


        List<Statement> seeds = new ArrayList<>();
        String targetMethodName = "assert";

        cg.stream().filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    System.out.println("NodeID-" + node.getGraphNodeId() + ": " + node);
                    IR ir = node.getIR();
                    Iterator<SSAInstruction> it = ir.iterateAllInstructions();
                    while(it.hasNext()) {
                        SSAInstruction instruction = it.next();

                        System.out.println("InsID-" + instruction.iIndex() + ": " + instruction.getClass());

                        if(instruction.iIndex() > 0) {
                            Statement statement = new NormalStatement(node, instruction.iIndex());
                            int lineNumber = AtomUtil.srcLineNumberOf(statement);
                            String line = null;
                            try {
                                line = FileUtil.readContentByLineNumber(javaPath, lineNumber);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if(line.contains(targetMethodName)) {
                                seeds.add(statement);
                            }
                        }

                    }
                });

        /*seeds.forEach((seed) -> {
            int lineNumber = AtomUtil.srcLineNumberOf(seed);
            try {
                System.out.println(FileUtil.readContentByLineNumber(javaPath1, lineNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        int cnt = 0;
        for (Statement seed : seeds) {
            System.out.println("seed: " + seed);
            System.out.println("seedLine: " + FileUtil.readContentByLineNumber(javaPath, AtomUtil.srcLineNumberOf(seed)));
            AtomTestCase atc = mySlicer.backwardSliceOne(seed, String.valueOf(cnt));
            cnt++;
            System.out.println(atc.dumpSnippet());
            System.out.println("------------------------------------------------------------------------");
        }
    }


    // Slice ALU
    @Test
    public void testFindSeedALU1() throws IOException, CancelException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";
        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/target/test-classes/net/mooctest";
        AnalysisScope scope = SlicerUtil.getDynamicScope(classDir, this.getClass().getClassLoader());
        Slicer mySlicer = new Slicer(scope, javaPath);
        CallGraph cg = mySlicer.getConfig().getCallGraph();

        List<Statement> seeds = new ArrayList<>();
        String targetMethodName = "assert";

        cg.stream().filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    System.out.println("NodeID-" + node.getGraphNodeId() + ": " + node);
                    IR ir = node.getIR();
                    Iterator<SSAInstruction> it = ir.iterateAllInstructions();
                    while(it.hasNext()) {
                        SSAInstruction instruction = it.next();

                        System.out.println("InsID-" + instruction.iIndex() + ": " + instruction.getClass());

                        Statement statement = new NormalStatement(node, instruction.iIndex());
                        int lineNumber = AtomUtil.srcLineNumberOf(statement);
                        String line = null;
                        try {
                            line = FileUtil.readContentByLineNumber(javaPath, lineNumber);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    /*
                    System.out.println(line);
                    System.out.println();
                    */
                        if(line.contains(targetMethodName)) {
                            seeds.add(statement);
                        }
                    }
                });
        /*seeds.forEach((seed) -> {
            int lineNumber = AtomUtil.srcLineNumberOf(seed);
            try {
                System.out.println(FileUtil.readContentByLineNumber(javaPath, lineNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        int cnt = 0;
        for (Statement seed : seeds) {
            System.out.println("seed: " + seed);
            System.out.println("seedLine: " + FileUtil.readContentByLineNumber(javaPath, AtomUtil.srcLineNumberOf(seed)));
            AtomTestCase atc = mySlicer.backwardSliceOne(seed, String.valueOf(cnt));
            cnt++;
            System.out.println(atc.dumpSnippet());
            System.out.println("------------------------------------------------------------------------");
        }

    }



}
