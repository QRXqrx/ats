package nju.pa.ats.core.slicer.deprecate;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.intset.IntSet;
import nju.pa.ats.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author QRX
 * @since 2020/02/11
 */
public class SlicerImpl1 implements ISlice{

    @Override
    public Statement findCallTo(CGNode node, String methodName) {
        IR ir = node.getIR();
        if(ir == null) {
//            throw new InvalidParameterException("Node's IR cannot be null!");
            System.err.println("Null IR");
            return null;
        }
        for(Iterator<SSAInstruction> it = ir.iterateAllInstructions(); it.hasNext(); ) {
            SSAInstruction instruction = it.next();
            if(instruction instanceof SSAInvokeInstruction) {
                SSAInvokeInstruction call = (SSAInvokeInstruction) instruction;

                if(call.getCallSite().getDeclaredTarget().getName().toString().equals(methodName)) {

                    IntSet callInstructionIndices = ir.getCallInstructionIndices(call.getCallSite());

                    if(callInstructionIndices.size() != 1) {
                        System.err.println("Expected 1 but got callInstructionIndices.size() = " + callInstructionIndices.size());
                    }
                    return new NormalStatement(node, callInstructionIndices.intIterator().next());
                }
            }
        }
//        Assertions.UNREACHABLE("failed to find call to " + methodName + " in " + node);
        System.err.println("Failed to find call to " + methodName + " in" + node);
        return null;
    }

    @Override
    public List<Integer> sliceToLineNumbers() {
        return null;
    }


    // 将目标文件夹下的所有class文件列入分析域中
    public AnalysisScope getDynamicScope(String dirPath, String exPath, ClassLoader classLoader) throws IOException {
        if(exPath == null) {
            throw new IllegalArgumentException("Exclusion file path cannot be null! You can pass an empty string instead.");
        }

        File exFile = new File(exPath);
        File exclusionFile = null;
        if(!FileUtil.suffixOf(exFile).equals(".txt")) {
            System.err.println("Warning: Please input invalid exclusion file. Now using default.");
            exclusionFile = new File("default-exclusions.txt");
        } else {
            exclusionFile = exFile;
        }

        AnalysisScope scope = AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                classLoader
        );

        List<File> classList = FileUtil.getAllFilesBySuffix(dirPath, ".class");
        classList.forEach((file) -> {
            try {
                scope.addClassFileToScope(ClassLoaderReference.Application, file);
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            }
        });

        return scope;
    }
}
