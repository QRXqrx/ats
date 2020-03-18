package nju.pa.ats.core.text;

import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
@Deprecated
public class TextSlicer implements TextSliceService {

    private String javaPath;

    private List<String> oldTests;
    private List<String> imports;
    private String dependencies;

    private List<String> newTests;

    public TextSlicer(String javaPath) throws IOException {
        this.javaPath = javaPath;
        this.imports = TextUtil.getImportsFromJavaFile(javaPath);
        this.dependencies = TextUtil.getAllInnerDependenciesFromJavaFile(javaPath);
        this.oldTests = TextUtil.getAllTestsFromJavaFile(javaPath);
        this.newTests = buildNewTests();
    }

    private <E> void dump(List<E> list) {
        String lineSign = "------------------------------------------------------------------------------------------------";
        list.forEach((test) -> {
            System.out.println(lineSign);
            System.out.print(test);
            System.out.println(lineSign);
        });
    }

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
        return newHead + "\n" + mid + "\n" + "}\n";
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

    private List<String> buildNewTests() {
        List<String> newTests = new ArrayList<>();
        for (String oldTest : oldTests) {
            newTests.addAll(splitOne(oldTest));
        }
        return refineTxtTests(newTests);
    }


    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public List<String> getOldTests() {
        return oldTests;
    }

    public void setOldTests(List<String> oldTests) {
        this.oldTests = oldTests;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getNewTests() {
        return newTests;
    }

    public void dumpOldTest() {
        dump(oldTests);
    }

    public void dumpNewTest() {
        dump(newTests);
    }

    public void dumpImports() {
        imports.forEach(System.out::println);
    }

    public void dumpInnerDepen() {
        System.out.println(dependencies);
    }

    public String getNewTestClassName() {
        String oldClassName = FileUtil.fileBaseName(javaPath);
        return oldClassName.replace("Test", "NewTest");
    }

    private String makeClassContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(dependencies).append("\n");
        newTests.forEach((newTest) -> contentBuilder.append(newTest).append("\n"));
        return contentBuilder.toString();
    }

    private String wrapClass(String content) {
        String newClassName = getNewTestClassName();
        String classHead = "public class " + newClassName + "{\n";
        String classTail = "}";
        return classHead + content + classTail;
    }

    private String concatenateImports() {
        StringBuilder importsBuilder = new StringBuilder(100 * imports.size());
        imports.forEach((anImport) -> importsBuilder.append(anImport).append("\n"));
        return importsBuilder.toString();
    }

    @Override
    public String generateNewTestClassContent() {
        return concatenateImports() + "\n\n" + wrapClass(makeClassContent());
    }

    @Override
    public String outputNewTestClassTo(String outputDir) throws IOException {
        File dir = new File(outputDir);
        if(dir.exists()) {
            if(!dir.isDirectory()) {
                throw new IllegalArgumentException(outputDir + "is not a directroy.");
            }
        } else {
            boolean mkdirs = dir.mkdirs();
            if(mkdirs) {
                System.out.println("Make new directory, path: " + outputDir);
            }
        }

        final String JAVA_SUFFIX = ".java";
        File newClassFile = new File(dir, getNewTestClassName() + JAVA_SUFFIX);
        String content = generateNewTestClassContent();
        return FileUtil.writeContentIntoFile(newClassFile.getAbsolutePath(), content);
    }

    public String outputNewTestClassTo() throws IOException {
        return outputNewTestClassTo("./");
    }
}
