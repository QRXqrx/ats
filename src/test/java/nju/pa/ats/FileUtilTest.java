package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import nju.pa.ats.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtilTest {


    @Test
    public void testFile() {
        System.out.println(File.pathSeparator);
        System.out.println(File.pathSeparatorChar);
        System.out.println(File.separator);
        System.out.println(File.separatorChar);
    }

    @Test
    public void testWriteContent() throws IOException {
        String referencePath1 = "./output1.txt";
        String referencePath2 = "./output2.txt";

        String content = "I love xx";
        List<String> contents = new ArrayList<>();
        contents.add("I ");
        contents.add("love ");
        contents.add("xx");

        String absPath1 = FileUtil.writeContentIntoFile(referencePath1, content);
        String absPath2 = FileUtil.writeContentsIntoFile(referencePath2, contents);
        System.out.println(absPath1);
        System.out.println(absPath2);
    }
    
    @Test
    public void testReadLine() throws IOException {
        String absPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        File file = new File(absPath);

//        List<Integer> nums = new ArrayList<>();
        HashSet<Integer> nums = new HashSet<>();
        nums.add(95);
        nums.add(96);
        nums.add(97);
        nums.add(98);
        nums.add(99);
        nums.add(100);
        nums.add(101);
        List<String> contents = FileUtil.readContentsByLineNumbers(absPath, nums);
        contents.forEach(System.out::println);
    }

    
    
    @Test
    public void testSuffixOf() {
        File file = new File("C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/main");
        System.out.println("Suffix: " + FileUtil.suffixOf(file));
    }

    @Test(timeout = 4000)
    public void testGetSuffix1() {
        File file = new File("exclusions.txt");
        Assert.assertEquals(".txt", FileUtil.suffixOf(file));
    }

    @Test
    public void testCompile4() {

        String javaDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar";
        List<File> javaFiles = FileUtil.getAllFilesBySuffix(javaDir, ".java");

        String ouputPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/output/classes";
        List<String> javaPaths = javaFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());
        javaPaths.forEach((path) -> FileUtil.compileJavaFile(ouputPath, path));
    /*
        String[] javaPaths = (String[]) javaFiles.stream().map(File::getAbsolutePath).toArray();
        FileUtil.compileJavaFile(ouputPath, javaPaths);
    */
    }
    
    @Test
    public void testCompile3() {
        Runtime runtime = Runtime.getRuntime();
        String targetJava = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/Swap.java";
        try {
            Process process = runtime.exec("cmd.exe /c javac " + targetJava);
        } catch (IOException e) {
            e.printStackTrace();
            runtime.exit(-1);
        }
        runtime.exit(0);
    }

    @Test
    public void testCompile2() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("cmd.exe /k java -version");
            InputStream is = process.getInputStream();

            while (is.read() != -1) {
                System.out.println((char)is.read());
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            runtime.exit(-1);
        }
        runtime.exit(0);
    }



    @Test
    public void testCompile1() {
        String path = "mspaint";
//        String path = "java";
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("cmd.exe /k start " + path);
            InputStream in = process.getInputStream();
            while (in.read() != -1) {

                System.out.println(in.read());
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            run.exit(0);
        }
    }

    
    
    @Test
    public void testGetJavaFile() {
//        String dir = "C:/Users/QRX/Desktop/MyWorkplace/programExercise";
//        String dir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src";
//        String dir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test";
//        String dir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target";
        String dir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar";
        final String JAVA_SUFFIX = "class";
        List<File> allJava = FileUtil.getAllFilesBySuffix(dir, JAVA_SUFFIX);
        allJava.forEach((file) -> System.out.println(file.getAbsolutePath()));
    }
    
    // TODO: 该部分和学长们最后拿到的Scope有些不同
    @Test
    public void testDynamicScope() throws InvalidClassFileException, IOException {
//        String classDirPath = "C:/Users/QRX/Desktop/MyWorkplace/programExercise/ats/target/example-programs/AStar/target/classes/net/mooctest";
        String classDirPath = "C:/Users/QRX/Desktop/MyWorkplace/programExercise/ats/target/example-programs/AStar/target/test-classes/net/mooctest";
        File classDirect = new File(classDirPath);
        File[] classFiles = null;
        if(classDirect.isDirectory()) {
            classFiles = classDirect.listFiles();
        }

        File exclusionFile = null;
        try {
            exclusionFile = new File("exclusions.txt");
        } catch (NullPointerException e) {
            exclusionFile = new File("Java60RegressionExclusions.txt");
            e.printStackTrace();
        }
//      exclusionFile = new File("Java60RegressionExclusions.txt");
        final AnalysisScope scope = AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                FileUtilTest.class.getClassLoader()
        );
        if(classFiles != null) {
            for (File classFile : classFiles) {
                System.out.println("ClassFile: " + classFile);
                scope.addClassFileToScope(ClassLoaderReference.Application, classFile);
            }
        }

        List<String> exclusions = Files.readAllLines(Paths.get(exclusionFile.getAbsolutePath()));
        for (String exclusion : exclusions) {
            scope.getExclusions().add(exclusion);
        }
        System.out.println(scope);
    }
}
