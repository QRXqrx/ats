package nju.pa.ats.core.text;

import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public class RefinedTextSlicer implements SliceTextService {

    private String javaPath;
    private List<String> oldTests;
    private List<String> imports;
    private String dependencies;
    private List<String> newTests;

    public RefinedTextSlicer(String javaPath) throws IOException {
        this.javaPath = javaPath;
        this.imports = TextUtil.getImportsFromJavaFile(javaPath);
        this.dependencies = TextUtil.getAllInnerDependenciesFromJavaFile(javaPath);
        this.oldTests = TextUtil.getAllTestsFromJavaFile(javaPath);
        this.newTests = buildNewTests();
    }

    private List<String> makeEndStatements(List<TextSrcBlock> tryCatchs, List<String> asserts) {
        List<String> tryCatchStrs = tryCatchs.stream()
                .map(TextSrcBlock::dumpBlock)
                .collect(Collectors.toList());
        List<String> exclusions = new ArrayList<>();
        // First try-catch, then asserts.
        exclusions.addAll(tryCatchStrs);
        exclusions.addAll(asserts);
        return exclusions;
    }

    private String exclude(String origin, List<String> exclusions) {
        for (String exclusion : exclusions) {
            origin = origin.replace(exclusion, "");
        }
        return origin;
    }

    private List<String> splitOneTest(String test) {
        String tempTest = test;
        List<TextSrcBlock> tryCatchs = TextUtil.drawTryCatchs(tempTest);
        for (TextSrcBlock tryCatch : tryCatchs) {
            tempTest = tempTest.replace(tryCatch.dumpBlock(), "");
        }
        List<String> asserts = TextUtil.drawAsserts(tempTest);

        // generate new tests.
        List<String> endStatements = makeEndStatements(tryCatchs, asserts);
        List<String> newTests = new ArrayList<>();

        for (String endStatement : endStatements) {
            int loc = test.indexOf(endStatement);
            String remain = test.substring(0, loc);
            remain = exclude(remain, endStatements);
            String newTest = remain + "\n" + endStatement + "\n" + "}";
            newTests.add(newTest);
        }

        return newTests;
    }

    private List<String> buildNewTests() {
        List<String> tempTests = new ArrayList<>();
        for (String oldTest : oldTests) {
            tempTests.addAll(splitOneTest(oldTest));
        }
        tempTests = TextUtil.refineTxtTests(tempTests);

        int cnt = 0;
        List<String> newTests = new ArrayList<>();
        for (String tempTest : tempTests) {
            String oldName = TextUtil.parseTestName(tempTest);
            String newName = oldName + "_" + cnt;
            cnt++;
            newTests.add(tempTest.replace(oldName, newName));
        }

        return newTests;
    }

    public String makeNewTestClassHead() {
        String oldClassName = FileUtil.fileSimpleNameExcludeSuffix(javaPath);
        return oldClassName.replace("Test", "NewTest");
    }

    private String wrapClass(String content) {
        String newClassName = makeNewTestClassHead();
        String classHead = "public class " + newClassName + "{\n";
        String classTail = "}";
        return classHead + content + classTail;
    }

    private String makeClassContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(dependencies).append("\n");
        newTests.forEach((newTest) -> contentBuilder.append(newTest).append("\n"));
        return contentBuilder.toString();
    }

    @Override
    public String generateNewTestClassContent() {
        return TextUtil.concatenateImports(imports) + "\n\n" + wrapClass(makeClassContent());
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
        File newClassFile = new File(dir, makeNewTestClassHead() + JAVA_SUFFIX);
        String content = generateNewTestClassContent();
        return FileUtil.writeContentIntoFile(newClassFile.getAbsolutePath(), content);
    }
}
