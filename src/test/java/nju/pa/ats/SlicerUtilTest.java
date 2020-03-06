package nju.pa.ats;

import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SlicerUtil;
import nju.pa.ats.util.TextUtil;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-06
 */
public class SlicerUtilTest {

    final String JAVA_SUFFIX = ".java";
    final String CLASS_SUFFIX = ".class";

    private void dumpMap(Map<String, List<File>> map) {
        map.forEach((k, v) -> {
            System.out.println("-----------------------------------------------------------");
            System.out.println(k);
            v.forEach((classFile) -> System.out.println("\t->\t" + classFile.getAbsolutePath()));
            System.out.println("-----------------------------------------------------------");
        });
    }

    private void demoTest(String javaDirPath, String classDirPath) {
        List<File> javaFiles = FileUtil.getAllFilesBySuffix(javaDirPath, JAVA_SUFFIX);
        List<File> classFiles = FileUtil.getAllFilesBySuffix(classDirPath, CLASS_SUFFIX);
        Map<String, List<File>> map = SlicerUtil.relateSrcFileWithClasses(javaFiles, classFiles);
        dumpMap(map);
    }

    @Test
    public void test() {
        String javaDirPath = "";
        String classDirPath = "";
        demoTest(javaDirPath, classDirPath);
    }

    @Test
    public void testRelateSrcFileWithClasses1() {
        String javaDirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar/src/test/java/net/mooctest";
        String classDirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/AStar/target/test-classes";
        demoTest(javaDirPath, classDirPath);
    }

    @Test
    public void testRelateSrcFileWithClasses0() {
        String javaDirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/src/test/java/net/mooctest";
        String classDirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/TrySlicer/AtomTestExperiment/ats_AStarEX/NextDay/target/test-classes";
        demoTest(javaDirPath, classDirPath);
    }



}
