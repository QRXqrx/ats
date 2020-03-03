package nju.pa.ats;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test how to get outer dependencies from a test class's source code.
 * Outer dependencies, OD for short.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-01
 */
public class GetOuterDependencyTest {

    private String path = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";

    // TODO: move it into a Util class
    public List<String> getImportsFromJavaFile(String javaPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(javaPath));
        List<String> importLines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("") || line.contains("package")) {
                continue;
            }
            if(!line.contains("import")) {
                break;
            }
            importLines.add(line);
        }

        br.close();
        return importLines;
    }

    private <E> void dumpPrintList(List<E> list) {
        final String SEPARATOR_LINE = "------------------------------------";
        System.out.println(SEPARATOR_LINE);
        list.forEach(System.out::println);
        System.out.println(SEPARATOR_LINE);
    }
    @Test
    public void test1() throws IOException {
        String[] paths = new String[] {
                "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/test/java/nju/pa/ats/ALUFullFullCannotRunTest.java",
                "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/test/java/nju/pa/ats/FileUtilTest.java",
                "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java",
                "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/main/java/nju/pa/ats/core/slicer/Slicer.java"
        };

        for (String onePath : paths) {
            System.out.println(onePath);
            dumpPrintList(getImportsFromJavaFile(onePath));
        }
    }


    @Test
    public void test0() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));

        List<String> importLines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("") || line.contains("package")) {
                continue;
            }
            if(!line.contains("import")) {
                break;
            }
            importLines.add(line);
        }

        importLines.forEach(System.out::println);
    }
}
