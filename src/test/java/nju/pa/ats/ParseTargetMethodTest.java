package nju.pa.ats;

import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-29
 */
public class ParseTargetMethodTest {

    String methodsTxtPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/main/resources/target_methods.txt";

    @Test
    public void testParseTargetMethod2() {

    }

    @Test
    public void testParseTargetMethod1() {
        try {
            List<String> contents = FileUtil.readContentsLineByLine(methodsTxtPath);
            List<String> targetMethods = SlicerUtil.parseTargetMethods(contents);
            targetMethods.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseTxtLineByLine1() {
        try {
            List<String> contents = FileUtil.readContentsLineByLine(methodsTxtPath);
            contents.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testParseTxtLineByLine() {
        List<String> answer = new ArrayList<>();
        answer.add("assert");
        answer.add("fail");

        try {
            List<String> contents = FileUtil.readContentsLineByLine(methodsTxtPath);
            Assert.assertEquals(answer, contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
