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
import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.slicer.deprecate.SlicerImpl1;
import nju.pa.ats.util.AtomUtil;
import nju.pa.ats.util.FileUtil;
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
public class AtomTestCaseTest {
    private String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    private String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes/net/mooctest";

    @Test
    public void testBuildAtomTest() throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        SlicerImpl1 slicer = new SlicerImpl1();
        AnalysisScope scope = slicer.getDynamicScope(classDir, "", this.getClass().getClassLoader());
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        CallGraphBuilder builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, new AnalysisCacheImpl(), cha, scope
        );
        CallGraph callGraph = builder.makeCallGraph(options, null);

//        final String methodNameStr = "AStar_ESTesttest04tokenhaha";
        final String methodNameStr = "AStar_ESTesttest00tokenhaha";

        List<Statement> statements = new ArrayList<>();
        callGraph.stream()
                .filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    if(node.getMethod().getName().toString().equals(methodNameStr)) {
                        IR ir = node.getIR();
                        Iterator<SSAInstruction> it = ir.iterateAllInstructions();
                        while(it.hasNext()) {
                            statements.add(new NormalStatement(node, it.next().iIndex()));
                        }
                    }
                });

        HashSet<Integer> lineNumbers = AtomUtil.toSrcLineNumbers(statements);
        List<String> contents = FileUtil.readContentsByLineNumbers(javaPath, lineNumbers);

        System.out.println("----------------dumpSnippet--------------------");
        AtomTestCase atomTestCase = new AtomTestCase(methodNameStr, javaPath, contents);
        System.out.println(atomTestCase.dumpSnippet());

    }
}
