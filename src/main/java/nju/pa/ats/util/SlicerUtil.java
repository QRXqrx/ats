package nju.pa.ats.util;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.intset.IntSet;
import nju.pa.ats.core.result.AtomCodeSnippet;
import nju.pa.ats.core.result.AtomTestCase;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public class SlicerUtil {

    /** Don't permit user construct this class, as this is a util class. */
    private SlicerUtil() { }


    /**
     *
     * @param contents read from text file contains slice targets
     * @return A list of strings, each of which represent a target method.
     */
    public static List<String> parseTargetMethods(List<String> contents) {
        List<String> targetMethods = new ArrayList<>();
        contents.forEach((content) -> {
            String targetMethod = content
                                    .replace("(","")
                                    .replace(")", "")
                                    .trim();
            targetMethods.add(targetMethod);
        });
        return targetMethods.stream().distinct().collect(Collectors.toList());
    }


    public static List<String> parseTargetMethods(String path) throws IOException {
        return parseTargetMethods(FileUtil.readContentsLineByLine(path));
    }


    /**
     * Make sure every Atom Test Case contains only one assert statement.
     * Assert realated method includes Assert.assert* , Assert.fail.
     * @param atomTestCase needs refinement.
     */
    public static void excludeExtraAssert(AtomTestCase atomTestCase) {
        List<String> srcLines = atomTestCase.getSourceCodeLines();
        excludeExtraAssert(srcLines);
    }

    public static void excludeExtraAssert(List<String> srcLines) {
        List<String> deleteLines = new ArrayList<>();
        int cnt = 1;
        for (String srcLine : srcLines) {
            if(srcLine.contains("assert") || srcLine.contains("fail")) {
                if(cnt != srcLines.size()) {
                    deleteLines.add(srcLine);
                }
            }
            cnt++;
        }
        for (String deleteLine : deleteLines) {
            srcLines.remove(deleteLine);
        }
    }

    /**
     *
     * @param javaSrcPath for getting source code.
     * @param cgNode Search each instruction to find a seed.
     * @param targetMethod is corresponding to target seed.
     * @return List<Statement> seeds: a List of seed
     */
    public static List<Statement> findSeedsBySrc(String javaSrcPath, CGNode cgNode, String targetMethod) {
        List<Statement> seeds = new ArrayList<>();
        IR ir = cgNode.getIR();
        if(ir == null) {
            throw new InvalidParameterException("This node's IR is null:" + cgNode.toString());
        }

        Iterator<SSAInstruction> it = ir.iterateAllInstructions();
        while(it.hasNext()) {
            SSAInstruction ins = it.next();
            int iIndex = ins.iIndex();
            if(iIndex >= 0) {
                Statement s = new NormalStatement(cgNode, iIndex);
                String srcLine = "";
                try {
                    srcLine = FileUtil.readContentByLineNumber(javaSrcPath, AtomUtil.srcLineNumberOf(s));
                } catch (IOException e) {
                    System.out.println("Get srcLine failed, in findSeed");
                    e.printStackTrace();
                }
                if(srcLine.contains(targetMethod)) {
                    seeds.add(s);
                }
            }
            /*else {
                System.out.println("Negative iIndex:" + ins.iIndex() + ":" + ins);
            }*/
        }
        return seeds;
    }

    public static List<Statement> findSeedsBySrc(String javaSrcPath, CGNode cgNode, List<String> targetMethods) {
        List<Statement> seeds = new ArrayList<>();
        targetMethods.forEach((targetMethod) -> seeds.addAll(findSeedsBySrc(javaSrcPath, cgNode, targetMethod)));
        return seeds;
    }

    public static List<Statement> findSeedsBySrc(String javaSrcPath, CallGraph callGraph, String targetMethod) {
        List<Statement> seeds = new ArrayList<>();
        callGraph.stream()
                .filter((cgNode) -> cgNode.getMethod().toString().contains("Application"))
                .forEach((cgNode) -> seeds.addAll(findSeedsBySrc(javaSrcPath, cgNode, targetMethod)));
        return seeds;
    }

    public static List<Statement> findSeedsBySrc(String javaSrcPath, CallGraph callGraph, List<String> targetMethods) {
        List<Statement> seeds = new ArrayList<>();
        callGraph.stream()
                .filter((cgNode) -> cgNode.getMethod().toString().contains("Application"))
                .forEach((cgNode) -> seeds.addAll(findSeedsBySrc(javaSrcPath, cgNode, targetMethods)));
        return seeds;
    }





    /**
     *
     * @param classDirPath From which slicer can read a dynamic scope.
     * @param exPath Path of exclusion file, output a warning when it is "" and use a default exclusion file.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     */
    public static AnalysisScope getDynamicScope(
            String classDirPath,
            String exPath,
            ClassLoader classLoader
    ) throws IOException {
        if(exPath == null) {
            throw new IllegalArgumentException(
                    "Exclusion file path cannot be null! If you want to use default, " +
                            "you could pass an empty string or not only pass classDirPath and classLoader."
            );
        }

        File exFile = new File(exPath);
        File exclusionFile;
        if(!FileUtil.suffixOf(exFile).equals(".txt")) {
            System.err.println("Warning: Please input valid exclusion file. Now using default.");
            exclusionFile = new File("exclusions.txt");
        } else {
            exclusionFile = exFile;
        }

        AnalysisScope scope = AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                classLoader
        );

        List<File> classList = FileUtil.getAllFilesBySuffix(classDirPath, ".class");
        classList.forEach((file) -> {
            try {
                scope.addClassFileToScope(ClassLoaderReference.Application, file);
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            }
        });

        return scope;
    }

    /**
     * This is a simpler way.
     *
     * @param classDirPath from which slicer can read a dynamic scope.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     */
    public static AnalysisScope getDynamicScope(
            String classDirPath,
            ClassLoader classLoader) throws IOException {
        return getDynamicScope(classDirPath, "", classLoader);
    }


    @Deprecated
    public static Statement findCallTo(CGNode node, String methodName) {
        IR ir = node.getIR();
        if(ir == null) {
            throw new InvalidParameterException("Node's IR cannot be null!");
        }
        for(Iterator<SSAInstruction> it = ir.iterateAllInstructions(); it.hasNext(); ) {
            SSAInstruction instruction = it.next();
            if(instruction instanceof SSAInvokeInstruction) {
                SSAInvokeInstruction call = (SSAInvokeInstruction) instruction;

                if(call.getCallSite().getDeclaredTarget().getName().toString().equals(methodName)) {

                    IntSet callInstructionIndices = ir.getCallInstructionIndices(call.getCallSite());
                    System.out.println("callInstructionIndices: "  + callInstructionIndices);
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
}
