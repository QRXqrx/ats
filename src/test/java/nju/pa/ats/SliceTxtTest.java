package nju.pa.ats;

import nju.pa.ats.core.text.TextSrcBlock;
import nju.pa.ats.util.TextUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public class SliceTxtTest {

    private String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    private <E> void dump(List<E> list) {
        String lineSign = "------------------------------------------------------------------------------------------------";
        list.forEach((test) -> {
            System.out.println(lineSign);
            System.out.print(test);
            System.out.println(lineSign);
        });
    }


    
    // 先假设try-catch的写法都是统一的，不存在所有部分挤在一行的情况
    private List<TextSrcBlock> drawTryCatchs(String test) {
        List<TextSrcBlock> tryCatchBlocks = new ArrayList<>();
        final String TRY = "try";
        final String CATCH = "catch";

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

    
    // 假设所有的assert语句写的也是规范的
    private List<String> drawAsserts(String test) {
        final String ASSERT = "assert";
        List<String> assertStrs = new ArrayList<>();
        String[] lines = test.split("\n");
        for (String line : lines) {
            if(line.contains(ASSERT)) {
                assertStrs.add(line);
            }
        }
        return assertStrs;
    }

    
    private String exclude(String origin, String exPart) {
        return origin.replace(exPart, "");
    }


    // 假设所有的测试命名都是标准的，均为public void
    private String parseTestName(String test) {
        final String PUBLIC_VOID = "public void ";
        int loc1 = test.indexOf(PUBLIC_VOID);
        String sub1 = test.substring(loc1 + PUBLIC_VOID.length());
        int loc2 = sub1.indexOf("(");
        return sub1.substring(0, loc2);
    }


    private String buildNewTest(String head, String mid, String oldName, int cnt) {
        String newName = oldName + "_" + String.valueOf(cnt);
        String newHead = head.replace(oldName, newName);
        return newHead + "\n" + mid + "}\n";
    }


    private List<String> splitOne(String test) {
        /*//
        System.out.println(test);
        //*/
        String oldName = parseTestName(test);
        List<TextSrcBlock> tryCatchs = drawTryCatchs(test);
        for (TextSrcBlock tryCatch : tryCatchs) {
            test = exclude(test, tryCatch.dumpBlock());
        }
        List<String> assertStrs = drawAsserts(test);
        for (String assertStr : assertStrs) {
            test = exclude(test, assertStr);
        }

        String remain = TextUtil.trimTail(test);

        List<String> newTests = new ArrayList<>();
        int cnt = 0;
        for (TextSrcBlock tryCatch : tryCatchs) {
            newTests.add(buildNewTest(remain, tryCatch.dumpBlock(), oldName, cnt));
            cnt++;
        }
        for (String assertStr : assertStrs) {
            newTests.add(buildNewTest(remain, assertStr, oldName, cnt));
            cnt++;
        }
        return newTests;
    }


    private List<String> refineTxtTests(List<String> tests) {
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


    private List<String> buildNewTests(List<String> oldTests) {
        List<String> newTests = new ArrayList<>();
        for (String oldTest : oldTests) {
            newTests.addAll(splitOne(oldTest));
        }
        return refineTxtTests(newTests);
    }


    private List<String> buildNewTestsFromJavaFile(String javaPath) throws IOException {
        List<String> oldTests = TextUtil.getAllTestsFromJavaFile(javaPath);
        return buildNewTests(oldTests);
    }

    @Test
    public void testBuildNewTestsFromJavaFile0() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        List<String> tests = buildNewTestsFromJavaFile(javaPath);
        dump(tests);
    }

    @Test
    public void test() throws IOException {
        String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
        List<String> tests = TextUtil.getAllTestsFromJavaFile(javaPath);
        dump(tests);
    }

    @Test
    public void testSplitOne() {
        String test =
                                "    @org.junit.Test(timeout = 4000)\n" +
                                "    public void Graph_ESTesttest11tokenhaha() throws java.lang.Throwable {\n" +
                                "        net.mooctest.Graph.Vertex<java.lang.String> graph_Vertex0 = new net.mooctest.Graph.Vertex<java.lang.String>(\"\", (-27));\n" +
                                "        net.mooctest.Graph.Vertex<java.lang.String> graph_Vertex1 = new net.mooctest.Graph.Vertex<java.lang.String>(graph_Vertex0);\n" +
                                "        net.mooctest.Graph.Edge<java.lang.String> graph_Edge0 = new net.mooctest.Graph.Edge<java.lang.String>((-27), graph_Vertex0, graph_Vertex0);\n" +
                                "        graph_Vertex0.addEdge(graph_Edge0);\n" +
                                "        org.junit.Assert.assertEquals((-27), graph_Edge0.getCost());\n" +
                                "        boolean boolean0 = graph_Vertex0.equals(graph_Vertex1);\n" +
                                "        org.junit.Assert.assertEquals((-27), graph_Vertex0.getWeight());\n" +
                                "        org.junit.Assert.assertFalse(boolean0);\n" +
                                "    }";
        List<String> newTests = splitOne(test);
        dump(newTests);
    }


    @Test
    public void testExclude() {
        String test =
                "    @org.junit.Test(timeout = 4000)\n" +
                        "    public void Graph_ESTesttest27tokenhaha() throws java.lang.Throwable {\n" +
                        "        java.lang.Integer integer0 = new java.lang.Integer(0);\n" +
                        "        net.mooctest.Graph.Vertex<java.lang.Integer> graph_Vertex0 = new net.mooctest.Graph.Vertex<java.lang.Integer>(integer0, 2363);\n" +
                        "        net.mooctest.Graph.Edge<java.lang.Integer> graph_Edge0 = null;\n" +
                        "        try {\n" +
                        "            graph_Edge0 = new net.mooctest.Graph.Edge<java.lang.Integer>(2363, graph_Vertex0, ((net.mooctest.Graph.Vertex<java.lang.Integer>) (null)));\n" +
                        "            org.junit.Assert.fail(\"Expecting exception: NullPointerException\");\n" +
                        "        } catch (java.lang.NullPointerException e) {\n" +
                        "            //\n" +
                        "            // Both 'to' and 'from' vertices need to be non-NULL.\n" +
                        "            //\n" +
                        "            org.evosuite.runtime.EvoAssertions.verifyException(\"net.mooctest.Graph$Edge\", e);\n" +
                        "        }\n" +
                        "    }";
        List<TextSrcBlock> tryCatchs = drawTryCatchs(test);


        for (TextSrcBlock tryCatch : tryCatchs) {
            test = exclude(test, tryCatch.dumpBlock());
        }
        System.out.println(test);
        System.out.println("------------------------------------");

        List<String> asserts = drawAsserts(test);
        for (String anAssert : asserts) {
            test = exclude(test, anAssert);
        }
        System.out.println(test);
        System.out.println("------------------------------------");
    }

    @Test
    public void testDrawAsserts2() {
        String test =
                "assertFalse(costVert.equals(null));\n" +
                        "        assertFalse(costVert.equals(new CostVertexPair<Integer>(3, new Vertex<Integer>(3))));\n" +
                        "        assertFalse(costVert.equals(new CostVertexPair<Integer>(2, new Vertex<Integer>(3))));\n" +
                        "        assertTrue(costVert.equals(costVert));\n" +
                        "        assertEquals(3844, costVert.hashCode());\n" +
                        "        try {\n" +
                        "\n" +
                        "            costVert = new CostVertexPair<Integer>(2, null);\n" +
                        "            assertFalse(costVert.equals(null));\n" +
                        "            assertEquals(3844, costVert.hashCode());\n" +
                        "            fail();\n" +
                        "        } catch (Exception e) {\n" +
                        "            assertEquals(\"vertex cannot be NULL.\", e.getMessage());\n" +
                        "        }";
        dump(drawAsserts(test));
    }

    @Test
    public void testDrawAsserts1() {
        String test =
                        "        net.mooctest.Graph.Vertex<java.lang.String> graph_Vertex0 = new net.mooctest.Graph.Vertex<java.lang.String>(\"\");\n" +
                        "        net.mooctest.Graph.Vertex<java.lang.String> graph_Vertex1 = new net.mooctest.Graph.Vertex<java.lang.String>(\"Mrx/nS3M^=F7#ei\", (-1032));\n" +
                        "        int int0 = graph_Vertex0.compareTo(graph_Vertex1);\n" +
                        "        org.junit.Assert.assertEquals((-14), int0);\n" +
                        "        org.junit.Assert.assertEquals((-1032), graph_Vertex1.getWeight());\n" +
                        "        org.junit.Assert.assertEquals(0, graph_Vertex0.getWeight());";
        dump(drawAsserts(test));
    }


    @Test
    public void testDrawAsserts0() {
        String test =
                "private static List<AtomTestCase> makeSlice(\n" +
                        "            String classDir,\n" +
                        "            String javaPath,\n" +
                        "            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions ddo,\n" +
                        "            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions cdo,\n" +
                        "            boolean isDistinct,\n" +
                        "            String targetMethodsPath\n" +
                        "    ) {\n" +
                        "        // Slice\n" +
                        "        SlicerConfig config = null;\n" +
                        "        try {\n" +
                        "            config = new SlicerConfig(\n" +
                        "                    SlicerUtil.getDynamicScope(classDir, Main.class.getClassLoader()),\n" +
                        "                    ddo,\n" +
                        "                    cdo\n" +
                        "            );\n" +
                        "        } catch (IOException e) {\n" +
                        "            System.out.println(\"In SliceProcess@makeSlice, getDynamicScope failed: \" + classDir);\n" +
                        "            e.printStackTrace();\n" +
                        "            System.exit(-1);\n" +
                        "        }\n" +
                        "\n" +
                        "        List<String> targetMethods = null;\n" +
                        "        try {\n" +
                        "            targetMethods = SlicerUtil.parseTargetMethods(targetMethodsPath);\n" +
                        "        } catch (IOException e) {\n" +
                        "            System.out.println(\"In SliceProcess@makeSlice, parse target methods failed: \" + classDir);\n" +
                        "            e.printStackTrace();\n" +
                        "            System.exit(-1);\n" +
                        "        }\n" +
                        "        Slicer slicer = new Slicer(config, javaPath, targetMethods, isDistinct);\n" +
                        "        return slicer.slice();\n" +
                        "    }";
        dump(drawAsserts(test));
    }

    @Test
    public void testDrawTryCatchs2() {
        String test =
                "private static List<AtomTestCase> makeSlice(\n" +
                        "            String classDir,\n" +
                        "            String javaPath,\n" +
                        "            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions ddo,\n" +
                        "            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions cdo,\n" +
                        "            boolean isDistinct,\n" +
                        "            String targetMethodsPath\n" +
                        "    ) {\n" +
                        "        // Slice\n" +
                        "        SlicerConfig config = null;\n" +
                        "        try {\n" +
                        "            config = new SlicerConfig(\n" +
                        "                    SlicerUtil.getDynamicScope(classDir, Main.class.getClassLoader()),\n" +
                        "                    ddo,\n" +
                        "                    cdo\n" +
                        "            );\n" +
                        "        } catch (IOException e) {\n" +
                        "            System.out.println(\"In SliceProcess@makeSlice, getDynamicScope failed: \" + classDir);\n" +
                        "            e.printStackTrace();\n" +
                        "            System.exit(-1);\n" +
                        "        }\n" +
                        "\n" +
                        "        List<String> targetMethods = null;\n" +
                        "        try {\n" +
                        "            targetMethods = SlicerUtil.parseTargetMethods(targetMethodsPath);\n" +
                        "        } catch (IOException e) {\n" +
                        "            System.out.println(\"In SliceProcess@makeSlice, parse target methods failed: \" + classDir);\n" +
                        "            e.printStackTrace();\n" +
                        "            System.exit(-1);\n" +
                        "        }\n" +
                        "        Slicer slicer = new Slicer(config, javaPath, targetMethods, isDistinct);\n" +
                        "        return slicer.slice();\n" +
                        "    }";
        List<TextSrcBlock> tryCatchBlocks = drawTryCatchs(test);
        tryCatchBlocks.forEach((block) -> System.out.println(block.dumpBlock()));
    }

    @Test
    public void testDrawTryCatchs1() {
        String test =
                "\t@Test\n" +
                        "\tpublic void testgetType1() {\n" +
                        "\t\tTriangle test = new Triangle(4,1,3);\n" +
                        "\t\tString strType =\"Illegal\";\n" +
                        "\t\tassertEquals(strType, test.getType(test));//isTriangle(triangle)=false\n" +
                        "\t}";
        List<TextSrcBlock> tryCatchBlocks = drawTryCatchs(test);
        tryCatchBlocks.forEach((block) -> System.out.println(block.dumpBlock()));
    }

    @Test
    public void testDrawTryCatchs0() {
        String test =
                "    @org.junit.Test(timeout = 4000)\n" +
                "    public void Graph_ESTesttest27tokenhaha() throws java.lang.Throwable {\n" +
                "        java.lang.Integer integer0 = new java.lang.Integer(0);\n" +
                "        net.mooctest.Graph.Vertex<java.lang.Integer> graph_Vertex0 = new net.mooctest.Graph.Vertex<java.lang.Integer>(integer0, 2363);\n" +
                "        net.mooctest.Graph.Edge<java.lang.Integer> graph_Edge0 = null;\n" +
                "        try {\n" +
                "            graph_Edge0 = new net.mooctest.Graph.Edge<java.lang.Integer>(2363, graph_Vertex0, ((net.mooctest.Graph.Vertex<java.lang.Integer>) (null)));\n" +
                "            org.junit.Assert.fail(\"Expecting exception: NullPointerException\");\n" +
                "        } catch (java.lang.NullPointerException e) {\n" +
                "            //\n" +
                "            // Both 'to' and 'from' vertices need to be non-NULL.\n" +
                "            //\n" +
                "            org.evosuite.runtime.EvoAssertions.verifyException(\"net.mooctest.Graph$Edge\", e);\n" +
                "        }\n" +
                "    }";
        List<TextSrcBlock> tryCatchBlocks = drawTryCatchs(test);
        tryCatchBlocks.forEach((block) -> System.out.println(block.dumpBlock()));
    }

    @Test
    public void testString1() {
        String str = "\n1\n1\n\n\n\n123";
        String[] ss = str.split("\n");
        System.out.println(Arrays.toString(ss));
        for (String s : ss) {
            if("".equals(s)) {
                System.out.println("Empty String");
            } else {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testString0() {
        String test =
                "    @org.junit.Test(timeout = 4000)\n" +
                        "    public void Graph_ESTesttest27tokenhaha() throws java.lang.Throwable {\n" +
                        "        java.lang.Integer integer0 = new java.lang.Integer(0);\n" +
                        "        net.mooctest.Graph.Vertex<java.lang.Integer> graph_Vertex0 = new net.mooctest.Graph.Vertex<java.lang.Integer>(integer0, 2363);\n" +
                        "        net.mooctest.Graph.Edge<java.lang.Integer> graph_Edge0 = null;\n" +
                        "        try {\n" +
                        "            graph_Edge0 = new net.mooctest.Graph.Edge<java.lang.Integer>(2363, graph_Vertex0, ((net.mooctest.Graph.Vertex<java.lang.Integer>) (null)));\n" +
                        "            org.junit.Assert.fail(\"Expecting exception: NullPointerException\");\n" +
                        "        } catch (java.lang.NullPointerException e) {\n" +
                        "            //\n" +
                        "            // Both 'to' and 'from' vertices need to be non-NULL.\n" +
                        "            //\n" +
                        "            org.evosuite.runtime.EvoAssertions.verifyException(\"net.mooctest.Graph$Edge\", e);\n" +
                        "        }\n" +
                        "    }";
        final String PUBLIC_VOID = "public void ";
        int loc1 = test.indexOf(PUBLIC_VOID);
//        System.out.println(loc1);
        String sub1 = test.substring(loc1 + PUBLIC_VOID.length());
        System.out.println(sub1);
        int loc2 = sub1.indexOf("(");
        System.out.println(loc2);
        String sub2 = sub1.substring(0, loc2);
        System.out.println(sub2);
    }

}
