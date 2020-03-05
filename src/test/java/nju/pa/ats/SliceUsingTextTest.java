package nju.pa.ats;

import nju.pa.ats.util.TextUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-04
 */
public class SliceUsingTextTest {
    private String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    private void dumpTests(List<String> testStrs) {
        String lineSign = "------------------------------------------------------------------------------------------------";
        testStrs.forEach((test) -> {
            System.out.println(lineSign);
            System.out.print(test);
            System.out.println(lineSign);
        });
    }

    private String makeMethodHead(int cnt, String oldHead) {
        int loc = oldHead.indexOf("(");
        String subStr = oldHead.substring(loc);
        String replaceStr = "_" + cnt + subStr;
        return oldHead.replace(subStr, replaceStr);
    }

    private boolean needSplit(String test) {
        final String ASSERT_STR = "assert";
        int loc1 = test.indexOf(ASSERT_STR);
        int loc2 = test.lastIndexOf(ASSERT_STR);
        return !(loc1 == loc2);
    }

    private List<String> splitTest(String test) {
        List<String> newTests = new ArrayList<>();
        if(needSplit(test)) {
            String[] lines = test.split("\n");
            String oldMethodHead = lines[1];
            String testAnnoStr = lines[0];
            int cnt = 0;
            for(int i = 0 ; i < lines.length; i++) {
                if(!lines[i].contains("assert")) {
                    continue;
                }
                String newMethodHead = makeMethodHead(cnt, oldMethodHead);
                cnt++;
                StringBuilder newTestBuilder = new StringBuilder(test.length() + 5);
                newTestBuilder.append(testAnnoStr).append("\n").append(newMethodHead).append("\n");
                for(int j = 2 ; j < i; j++) {
                    if(lines[j].contains("assert")) {
                        continue;
                    }
                    newTestBuilder.append(lines[j]).append("\n");
                }
                newTestBuilder.append(lines[i]).append("\n");
                newTestBuilder.append("}").append("\n");
                newTests.add(newTestBuilder.toString());
            }
        } else {
            newTests.add(test);
        }
        return newTests;
    }

    private List<String> buildNewTests(List<String> tests) {
        List<String> newTests = new ArrayList<>();
        tests.forEach((test) -> newTests.addAll(splitTest(test)));
        return newTests;
    }

    @Test
    public void testbuildNewTests() throws IOException {
        List<String> tests = TextUtil.getAllTestsFromJavaFile(javaPath);
        List<String> newTests = buildNewTests(tests);
        dumpTests(newTests);
    }

    @Test
    public void testMakeMethodHead() {
        String oldHead = "public void testTxtSplit0() throws IOException {";
        String newHead = makeMethodHead(1, oldHead);
        System.out.println("old: " + oldHead);
        System.out.println("new: " + newHead);
    }

    @Test
    public void testTxtSplit1() throws IOException {
        List<String> tests = TextUtil.getAllTestsFromJavaFile(javaPath);

        List<String> newTests = new ArrayList<>();
    }

    @Test
    public void testTxtSplit0() throws IOException {
        List<String> tests = TextUtil.getAllTestsFromJavaFile(javaPath);

        List<String> newTests = new ArrayList<>();
        for (String test : tests) {
            boolean containsAssert = false;
            String[] lines = test.split("\n");
            for(int i = 0 ; i < lines.length; i++) {
                if(!lines[i].contains("assert")) {
                    continue;
                }
                if (!containsAssert) {
                    containsAssert = true;
                }
                StringBuilder newTestBuider = new StringBuilder(test.length() + 5); // 肯定不会超过这个范围
                for(int j = 0 ; j < i ; j++) {
                    if(lines[j].contains("assert")) {
                        continue;
                    }
                    newTestBuider.append(lines[j]).append("\n");
                }
                newTestBuider.append(lines[i]).append("\n");
                newTestBuider.append("\t").append("}").append("\n");
                newTests.add(newTestBuider.toString());
            }

            if(!containsAssert) {
                newTests.add(test);
            }
        }

        dumpTests(newTests);
    }

    @Test
    public void test0() throws IOException {
        List<String> tests = TextUtil.getAllTestsFromJavaFile(javaPath);
        tests.forEach(System.out::println);
    }

    @Test
    public void testString3() {
        String line = "I love XX";
        int loc1 = line.indexOf("assert");
        int loc2 = line.lastIndexOf("assert");
        Assert.assertTrue((loc1 == loc2));
    }

    @Test
    public void testString2() {
        String line = "1assert";
        int loc1 = line.indexOf("assert");
        int loc2 = line.lastIndexOf("assert");
        Assert.assertTrue((loc1 == loc2));
    }
    @Test
    public void testString1() {
        String line = "1assert 2assert";
        int loc1 = line.indexOf("assert");
        int loc2 = line.lastIndexOf("assert");
        Assert.assertFalse((loc1 == loc2));
    }


    @Test
    public void testString0() {
        String line = "I love XX";
        int locOfX = line.indexOf('X');
        String subStr = line.substring(locOfX);
        System.out.println(subStr);
        System.out.println(line.replace(subStr, "W" + subStr));
    }
}
