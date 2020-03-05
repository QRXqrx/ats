package nju.pa.ats;

import nju.pa.ats.util.FileUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-02
 */
public class GetInnerDependencyTest1 {
    private String path = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    private final String SEPARATOR = "-----------------------------\n";

    private String trimTail(String content) {
        final String TARGET = "}";
        int lastIndexOfTarget = content.lastIndexOf(TARGET);
        return content.substring(0, lastIndexOfTarget);
    }

    private String getLinesAfterExclusionFromJavaFile(String javaPath) throws IOException {
        String testClassName = FileUtil.fileSimpleNameExcludeSuffix(javaPath);
        String classHead = "class " + testClassName;

        BufferedReader br = new BufferedReader(new FileReader(javaPath));
        StringBuilder strbuilder = new StringBuilder();
        String line;
        boolean startRead = false;
        while((line = br.readLine()) != null) {
            if(!startRead) {
                if(line.contains(classHead)) {
                    startRead = true;
                }
                continue;
            }
            if(line.equals("")) {
                continue;
            }
            if(line.contains("@Test") || line.contains("@org.junit.Test")) {
                strbuilder.append(SEPARATOR);
            }
            strbuilder.append(line).append("\n");
        }
        br.close();

        return trimTail(strbuilder.toString());
    }

    private List<String> getAllTests(String allLines) {
        String[] parts = allLines.split(SEPARATOR);

        List<String> newPartList = new ArrayList<>();
        for (String part : parts) {
            if(!(part.contains("@Test") || part.contains("@org.junit.Test"))) {
                continue;
            }
            boolean flag = false;
            int cnt = 0;
            StringBuilder newPartBuilder = new StringBuilder(part.length());
            String[] lines = part.split("\n");
            for (String oneline : lines) {
                if((cnt == 0) && flag) {
                    break;
                }
                if(oneline.contains("{")) {
                    if(!flag) {
                        flag = true;
                    }
                    if(!oneline.contains("}")) {
                        cnt++;
                    }
                } else {
                    if(oneline.contains("}")) {
                        cnt--;
                    }
                }
                newPartBuilder.append(oneline).append("\n");
            }
            newPartList.add(newPartBuilder.toString());
        }

        return newPartList;
    }

    private String cleanSEPARTOR(String lines) {
        return lines.replace(SEPARATOR, "");
    }

    public String getAllInnerDependenciesFromJavaFile(String javaPath) throws IOException {
        String allLines = getLinesAfterExclusionFromJavaFile(javaPath);
        List<String> allTests = getAllTests(allLines);

        for (String oneTest : allTests) {
            allLines = allLines.replace(oneTest, "");
        }

        return cleanSEPARTOR(allLines);
    }

    @Test
    public void testGetInnerDepen() throws IOException {
        String depenStrs = getAllInnerDependenciesFromJavaFile(path);
        System.out.println(depenStrs);
    }


    @Test
    public void test1() throws IOException {
        String allLines = getLinesAfterExclusionFromJavaFile(path);
        List<String> allTests = getAllTests(allLines);
        System.out.println(allTests);
    }

    @Test
    public void test0() throws IOException {
        System.out.println(getLinesAfterExclusionFromJavaFile(path));
    }
}
