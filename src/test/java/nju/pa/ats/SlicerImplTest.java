package nju.pa.ats;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import nju.pa.ats.core.staticpa.deprecate.SlicerImpl1;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

public class SlicerImplTest {
    private String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";
    @Test
    public void test() {

    }

    private void printSrcLineNumber(Statement statement) {
        if (!(statement.getKind() == Statement.Kind.NORMAL)) { // ignore special kinds of statements
            throw new IllegalArgumentException("Special statement: " + statement.toString());
        }
        /*
        int instructionIndex = ((NormalStatement) statement).getInstructionIndex();
        int lineNum =  statement.getNode().getMethod().getLineNumber(instructionIndex);
        System.out.println("Source line number = " + lineNum );
        */
        int bcIndex;
        int instructionIndex = ((NormalStatement) statement).getInstructionIndex();

        try {
            bcIndex = ((ShrikeBTMethod) statement.getNode().getMethod()).getBytecodeIndex(instructionIndex);
            try {
                int src_line_number = statement.getNode().getMethod().getLineNumber(bcIndex);
                System.out.println ( "Source line number = " + src_line_number );
            } catch (Exception e) {
                System.err.println("Bytecode index no good");
                System.err.println(e.getMessage());
            }
        } catch (Exception e ) {
            System.err.println("it's probably not a BT method (e.g. it's a fakeroot method)");
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testPrintSrcLineNumber1() throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        SlicerImpl1 slicer = new SlicerImpl1();
        AnalysisScope scope = slicer.getDynamicScope(classDir, "", this.getClass().getClassLoader());
        ClassHierarchy cha =  ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        CallGraphBuilder builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, new AnalysisCacheImpl(), cha, scope
        );
        CallGraph callGraph = builder.makeCallGraph(options, null);

        callGraph.stream()
                .filter((node) -> node.getMethod().toString().contains("Application"))
                .forEach((node) -> {
                    if(node.getMethod().getName().toString().equals("AStar_ESTesttest04tokenhaha")) {
                        System.out.println(node);
                        IR ir = node.getIR();
                        Iterator<SSAInstruction> iterator =  ir.iterateAllInstructions();
                        while(iterator.hasNext()) {
                            SSAInstruction instruction = iterator.next();
                            int instructionIndex = instruction.iIndex();
                            NormalStatement statement = new NormalStatement(node, instructionIndex);
                            printSrcLineNumber(statement);
                        }
                    }
                });

        /*callGraph.forEach((node) -> {
            if(node.getMethod().toString().contains("Application")) {
                IR ir = node.getIR();
                Iterator<SSAInstruction> iterator =  ir.iterateAllInstructions();
                while(iterator.hasNext()) {
                    SSAInstruction instruction = iterator.next();
                    int instructionIndex = instruction.iIndex();
                    NormalStatement statement = new NormalStatement(node, instructionIndex);
                    printSrcLineNumber(statement);
                }
            }

        });*/
    }

    @Test
    public void testAnalysis() throws ClassHierarchyException, IOException, CallGraphBuilderCancelException {
//        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target";
        String exPath = "Java60RegressionExclusions.txt";

        SlicerImpl1 slicer = new SlicerImpl1();
        AnalysisScope scope = slicer.getDynamicScope(classDir, exPath, this.getClass().getClassLoader());
    /*
        try {
            scope = getDynamicScope(classDir, exPath, this.getClass().getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(scope); // scope is ok
    */
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
    /*
        System.out.println(cha);
        cha.forEach(System.out::println); // cha is ok
    */
        Iterable<Entrypoint> entrypoints = new AllApplicationEntrypoints(scope, cha);
//        entrypoints.forEach(System.out::println); entrypoints is ok
        AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
        CallGraphBuilder builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, new AnalysisCacheImpl(), cha ,scope
        );

        CallGraph cg = builder.makeCallGraph(options, null);
//        cg.forEach(System.out::println); // cg is ok

        PointerAnalysis pa = builder.getPointerAnalysis();
//        System.out.println(pa); pa is ok

//        cg.forEach((node) -> System.out.println(node.getMethod().getName()));
        cg.forEach((node) -> {
            Iterator<SSAInstruction> insIter = node.getIR().iterateAllInstructions();
            while(insIter.hasNext()) {
                System.out.println(insIter.next());
            }
        });
    }
}
