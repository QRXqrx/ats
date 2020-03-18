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
import nju.pa.ats.core.result.AtomTestCase;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
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
     * TODO: add descriptions
     * @param javaFiles
     * @param classFiles
     * @return
     */
    public static Map<String, List<File>> relateSrcFileWithClasses(List<File> javaFiles, List<File> classFiles) {

        Map<String, List<File>> map = new HashMap<>();
        List<String> javaAbsPaths = javaFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());

        for (String javaAbsPath : javaAbsPaths) {
            String javaFileName = FileUtil.fileSimpleNameExcludeSuffix(javaAbsPath);
            List<File> relatedClassFiles = classFiles.stream()
                    .filter((classFile) -> {
                        String classFileName = FileUtil.fileSimpleNameExcludeSuffix(classFile.getAbsolutePath());
                        return classFileName.contains(javaFileName);
                    })
                    .collect(Collectors.toList());
            map.put(javaAbsPath, relatedClassFiles);
        }

        return map;
    }

    /**
     *
     * @param contents read from text file contains slice targets
     * @return A list of strings, each of which represent a target method.
     */
    public static List<String> parseTargets(List<String> contents) {
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


    public static List<String> parseTargets(String path) throws IOException {
        return parseTargets(FileUtil.readContentsLineByLine(path));
    }


    /**
     * Make sure every Atom Test Case contains only one assert statement.
     * Assert realated method includes Assert.assert*() or Assert.fail().
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
     * Find seed statements for slicing by text source code line.
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
                    int srcLinuNum = AtomUtil.srcLineNumberOf(s);
                    if(srcLinuNum == -1) { // See javadoc of AtomUtil.srcLineNumberOf
                        continue;
                    }
                    srcLine = FileUtil.readContentByLineNumber(javaSrcPath, srcLinuNum);
                } catch (IOException e) {
                    System.out.println("Getting srcLine failed due to IOException, in findSeed");
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // When pass a negative line number in to FileUtil.readContentByLineNumber
                    System.out.println("Getting srcLine failed due to negetive srcLine number, in findSeed");
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
     * Get <code>AnalysisScope</code> dynamically.
     *
     * @param classFiles A list of test class files, each of which represent a class file you want to add
     *                   into AnalysisScope.
     * @param exPath Path of exclusion file, output a warning when it is "" and use a default exclusion file.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     */
    public static AnalysisScope getDynamicScope(
            List<File> classFiles,
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
            System.err.println("Invalid exclusion file. Now using default.");
            exclusionFile = new File("default-exclusions.txt");
        } else {
            exclusionFile = exFile;
        }

        AnalysisScope scope = AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                classLoader
        );

        classFiles.forEach((file) -> {
            try {
                scope.addClassFileToScope(ClassLoaderReference.Application, file);
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            }
        });

        return scope;
    }


    /**
     * Get <code>AnalysisScope</code> dynamically.
     *
     * @param classDirPath From which staticpa can read a dynamic scope.
     * @param exPath Path of exclusion file, output a warning when it is "" and use a default exclusion file.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     * @throws IllegalArgumentException when classDirPath is invalid.
     */
    public static AnalysisScope getDynamicScope(
            String classDirPath,
            String exPath,
            ClassLoader classLoader
    ) throws IOException {
        File dir = new File(classDirPath);
        if(!dir.exists()) {
            throw new IllegalArgumentException("Wrong classDirPath: Path does not exists");
        }
        if(!dir.isDirectory()) {
            throw new IllegalArgumentException("Wrong classDirPath: Path does not refer to a directory.");
        }
        List<File> classFiles = FileUtil.getAllFilesBySuffix(classDirPath, ".class");
        return getDynamicScope(classFiles, exPath, classLoader);

    }



    /**
     * This is a simpler way.
     *
     * @param classDirPath from which staticpa can read a dynamic scope.
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

    public static AnalysisScope getDynamicScope(
            List<File> classFiles,
            ClassLoader classLoader) throws IOException {
        return getDynamicScope(classFiles, "", classLoader);
    }

    /**
     *
     *
     * Only a example.
     *
     */
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
