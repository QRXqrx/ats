package nju.pa.ats;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import nju.pa.ats.core.slicer.deprecate.SlicerImpl1;
import nju.pa.ats.util.AtomUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-12
 */
public class AtomUtilTest {
    private String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";

    @Test
    public void testGetSourceLineNumbers() throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        SlicerImpl1 slicer = new SlicerImpl1();
        AnalysisScope scope = slicer.getDynamicScope(classDir, "", this.getClass().getClassLoader());
        ClassHierarchy cha =  ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        CallGraphBuilder builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, new AnalysisCacheImpl(), cha, scope
        );
        CallGraph callGraph = builder.makeCallGraph(options, null);

//        callGraph.stream()
//                .filter((node) -> node.getMethod().toString().contains("Application"))
//                .forEach((node) -> {
//                    if(node.getMethod().getName().toString().equals("AStar_ESTesttest04tokenhaha")) {
//                        IR ir = node.getIR();
//                        Iterator<SSAInstruction> iterator = ir.iterateAllInstructions();
//                        while(iterator.hasNext()) {
//                            SSAInstruction instruction = iterator.next();
//                            Statement s = new NormalStatement(node, instruction.iIndex());
//                            System.out.println(AtomUtil.srcLineNumberOf(s));
//                        }
//                    }
//                });

        List<Statement> statements = new ArrayList<>();
        callGraph.stream()
                .filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    if(node.getMethod().getName().toString().equals("AStar_ESTesttest04tokenhaha")) {
                        IR ir = node.getIR();
                        Iterator<SSAInstruction> iterator = ir.iterateAllInstructions();
                        while(iterator.hasNext()) {
                            SSAInstruction instruction = iterator.next();
                            statements.add(new NormalStatement(node, instruction.iIndex()));
                        }
                    }
                });
        HashSet<Integer> lineNumbers = AtomUtil.toSrcLineNumbers(statements);
        System.out.println(lineNumbers);
    }

    @Test
    public void test() {
        HashSet<Integer> set = new HashSet<>();
        set.add(1);
        set.add(3);
        set.add(4);
        set.add(5);

        for (Integer integer : set) {
            System.out.println(integer);
        }

        set.forEach(System.out::println);
    }
}
