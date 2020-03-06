package nju.pa.ats.util;

import nju.pa.ats.core.text.TextSrcBlock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class mainly offers static methods that can be of help when
 * getting inner dependencies(e.g. inner class) and outer dependencies
 * (e.g. imports) from original test class.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-04
 */
public class TextUtil {

    private TextUtil() {}

    /** Using for getting inner dependencies */
    public static final String SEPARATOR = "-----------------------------\n";
    public static final String PUBLIC_VOID = "public void ";
    public static final String ASSERT = "assert";
    public static final String TRY = "try";
    public static final String CATCH = "catch";

    /**
     * Exclude blank lines in text test code.
     *
     * @param tests Text source codes of tests, each test represent a test method.
     * @return A list strings. Every string represent a test after exclusion of blank lines.
     */
    public static List<String> refineTxtTests(List<String> tests) {
        List<String> refinedTests = new ArrayList<>();
        for (String test : tests) {
            String[] lines = test.split("\n");
            StringBuilder refineTestBuilder = new StringBuilder(test.length());
            for (String line : lines) {
                if(!"".equals(line)) {
                    refineTestBuilder.append(line).append("\n");
                }
            }
            refinedTests.add(refineTestBuilder.toString());
        }
        return refinedTests;
    }


    /**
     * Concatenate import statements.
     *
     * @param imports A list of string, each of which represent a import statement.
     * @return Import string block.
     */
    public static String concatenateImports(List<String> imports) {
        StringBuilder importsBuilder = new StringBuilder(100 * imports.size());
        imports.forEach((anImport) -> importsBuilder.append(anImport).append("\n"));
        return importsBuilder.toString();
    }


    /**
     * At this stage, we assume that every test method is named in a formal way, i.e., every test
     * method is annotated by @Test or @org.junit.Test, and its method declaration is started with
     * <code> public void </code>.
     * This method can parse out test name from its source code.
     *
     * @param test is a string represent a test method.
     * @return The method name of <param>test</param>.
     */
    public static String parseTestName(String test) {
        int loc1 = test.indexOf(PUBLIC_VOID);
        String sub1 = test.substring(loc1 + PUBLIC_VOID.length());
        int loc2 = sub1.indexOf("(");
        return sub1.substring(0, loc2);
    }

    /**
     * Traverse and print content of a list, in a relatively formal way.
     *
     * @param list a list.
     * @param <E> Element type of the list. Generally, E should be a String or String-like type.
     */
    public static <E> void dump(List<E> list) {
        final String lineSign = "------------------------------------------------------------------------------------------------";
        list.forEach((test) -> {
            System.out.println(lineSign);
            System.out.print(test);
            System.out.println(lineSign);
        });
    }


    /**
     * In this stage, we assume that a assert statement is written in a formal way, i.e.,
     * invoke <code>Assert.assert*()</code> to make assertions, and all <code>Assert.fail()</code>
     * is surrounded by try-catch block.
     *
     * @param test is a string represent source code lines of a test method.
     * @return A list of string, each of with represent a assert line within <param>test</param>
     */
    public static List<String> drawAsserts(String test) {
        List<String> assertStrs = new ArrayList<>();
        String[] lines = test.split("\n");
        for (String line : lines) {
            if(line.contains(ASSERT)) {
                assertStrs.add(line);
            }
        }
        return assertStrs;
    }


    /**
     * In this stage, we assume that all the try-catch blocks in test coded is written "normally".
     * That is, a try-catch will be organized as follows:
     * __________________________________________________________________
     * *  try {                    *                                    *
     * *                           *                                    *
     * *  } catch(Exception e) {   *     try{ } catch(Exception e) {}   *
     * *                           *                                    *
     * *  }                        *                                    *
     * ------------------------------------------------------------------
     * This method can draw out try-catch blocks from source code of test methods. These try-catch
     * blocks refer to the outer nested try-catch, not including inner try-catch in a nested
     * try-catch.
     *
     * @param test is a string represent source code lines of a test method.
     * @return A list of TextSrcBlock, each of which represent a try-catch block.
     */
    public static List<TextSrcBlock> drawTryCatchs(String test) {
        List<TextSrcBlock> tryCatchBlocks = new ArrayList<>();
        String[] lines = test.split("\n");
        List<String> tryCatchLines = new ArrayList<>();
        boolean isStart = false;
        boolean prepareForEnding = false;
        int cnt = 0;
        for (String line : lines) {
            if(!line.contains(TRY) && !isStart) { // 还没开始构建try-catch
                continue;
            }
            if(line.contains(TRY)) {
                if(!isStart) {
                    isStart = true;
                }
            }
            if(isStart) {
                if(line.contains("{")) {
                    cnt++;
                }
                if(line.contains("}")) {
                    cnt--;
                }
                if(line.contains(CATCH)) {
                    if(!prepareForEnding) {
                        prepareForEnding = true;
                    }
                }
                tryCatchLines.add(line);
                if((cnt == 0) && prepareForEnding) {
                    List<String> temp = new ArrayList<>(tryCatchLines);
                    tryCatchBlocks.add(new TextSrcBlock(temp));
                    tryCatchLines.clear();
                    isStart = false;
                    prepareForEnding = false;
                }
            }
        }
        return tryCatchBlocks;
    }

    /**
     * Draw out all the text source codes represent test methods in target java file.
     *
     * @param javaPath path of target java file.
     * @return A list of strings, each of which represent a test method of the java file.
     * @throws IOException when target file doesn't exist or read wrongly.
     */
    public static List<String> getAllTestsFromJavaFile(String javaPath) throws IOException {
        String lines = getLinesAfterExclusionFromJavaFile(javaPath);
        return getAllTests(lines);
    }


    /**
     *
     * @param content inner dependencies followed by a '}'
     * @return original content if cannot find '}' or
     *         Inner dependencies exclude '}' if find target normally.
     *
     */
    public static String trimTail(String content) {
        final String TARGET = "}";
        int lastIndexOfTarget = content.lastIndexOf(TARGET);
        if(lastIndexOfTarget == -1) {
            return content;
        }
        return content.substring(0, lastIndexOfTarget);
    }

    /**
     * This is a private method prepared for <code>getAllInnerDependenciesFromJavaFile</code>
     *
     * @param javaPath path of target java file.
     * @return A string block contains lines read from a java source file, which not included content
     *         before <code>class className{</code>
     * @throws IOException when target file doesn't exist or read wrongly.
     */
    private static String getLinesAfterExclusionFromJavaFile(String javaPath) throws IOException {
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

    /**
     * This is a private method prepared for <code>getAllInnerDependenciesFromJavaFile</code>
     *
     * @param allLines All lines read from a java source file.
     * @return A list of strings, each of which represent a test method. The head of the string
     *         is in the form of <code>@Test/@org.junit.Test \n public void test()</code>
     */
    private static List<String> getAllTests(String allLines) {
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

    /**
     * This is a private method prepared for <code>getAllInnerDependenciesFromJavaFile</code>
     *
     * @param lines after excluding lines of test cases.
     * @return A string block, exclude all <constant>SEPARATOR</constant>
     */
    private static String cleanSEPARTOR(String lines) {
        return lines.replace(SEPARATOR, "");
    }

    /**
     *
     * @param javaPath path of target java file.
     * @return A list of statements, representing inner dependencies, e.g. static inner methods
     *         of the target test class, inner classes.
     * @throws IOException when target file doesn't exist or read wrongly.
     */
    public static String getAllInnerDependenciesFromJavaFile(String javaPath) throws IOException {
        String allLines = getLinesAfterExclusionFromJavaFile(javaPath);
        List<String> allTests = getAllTests(allLines);

        for (String oneTest : allTests) {
            allLines = allLines.replace(oneTest, "");
        }

        return cleanSEPARTOR(allLines);
    }




    /**
     * @param javaPath path of target java file.
     * @return A list of statements, representing outer dependencies. Organized as "import ..." or "import static ...
     * @throws IOException when target file doesn't exist or read wrongly.
     */
    public static List<String> getImportsFromJavaFile(String javaPath) throws IOException {
        final String PACKAGE = "package";
        final String IMPORT = "import";

        BufferedReader br = new BufferedReader(new FileReader(javaPath));
        List<String> importLines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("") || line.contains(PACKAGE)) {
                continue;
            }
            if(!line.contains(IMPORT)) {
                break;
            }
            importLines.add(line);
        }

        br.close();
        return importLines;
    }

}
