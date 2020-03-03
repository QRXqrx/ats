package nju.pa.ats;

import nju.pa.ats.util.FileUtil;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test how to get inner dependencies from a test class's source code.
 * Inner Dependencies, ID for short.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-01
 */
public class GetInnerDependencyTest {

    private String path = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";
    private static final String DOT = ".";

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

    private String readAllLinesStr(String javaPath) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(javaPath));
        StringBuilder allLinesBuilder = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("")) {
                continue;
            }
            if(line.contains("@Test") || line.contains("@org.junit.Test")) {
                allLinesBuilder.append(SEPARATOR);
            }
            allLinesBuilder.append(line).append("\n");
        }
        br.close();
        return allLinesBuilder.toString();
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

    private List<String> getAllTestLinesFromJavaFile(String javaPath) throws IOException {
        List<String> allTests = getAllTests(readAllLinesStr(javaPath));
        List<String> allTestLines = new ArrayList<>();

        for (String allTest : allTests) {
            List<String> testLines = Arrays.asList(allTest.split("\n"));
            allTestLines.addAll(testLines);
        }
        return allTestLines;
    }

    public List<String> getInnerDependenciesFromJavaFile(String javaPath) throws IOException {
        List<String> allTestLines = getAllTestLinesFromJavaFile(javaPath);
        List<String> allImports = getImportsFromJavaFile(javaPath);
        List<String> allContents = FileUtil.readContentsLineByLine(javaPath).stream()
                .filter((content) -> !content.equals(""))
                .collect(Collectors.toList());

        List<String> dependencies = new ArrayList<>();
        for (String content : allContents) {
            if(!(allImports.contains(content) || allTestLines.contains(content))) {
                dependencies.add(content);
            }
        }

        return dependencies;
    }

    @Test
    public void testGetAllDependencies() throws IOException {
        List<String> innerDependencies = getInnerDependenciesFromJavaFile(path);
        innerDependencies.forEach(System.out::println);
    }

    @Test
    public void testGetAllTest() throws IOException {
        String allLinesStr = readAllLinesStr(path);
        System.out.println(getAllTests(allLinesStr));
    }


    @Test
    public void testGetIDByFileOperation3() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));


        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("")) {
                continue;
            }
            if(line.contains("@Test") || line.contains("@org.junit.Test")) {
                contentBuilder.append(SEPARATOR);
            }
            contentBuilder.append(line).append("\n");
        }

        List<String> partList = Arrays.asList(contentBuilder.toString().split(SEPARATOR));

        partList = partList.stream()
                .filter((part) -> (part.contains("@Test") || part.contains("@org.junit.Test")))
                .collect(Collectors.toList());

        List<String> newPartList = new ArrayList<>();
        partList.forEach((part) -> {
            boolean flag = false;
            int cnt = 0;
            StringBuilder partBuilder = new StringBuilder(part.length());
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
                partBuilder.append(oneline).append("\n");
            }
            /*System.out.println("*************************");
            System.out.println(partBuilder.toString());*/
            newPartList.add(partBuilder.toString());
        });

        System.out.println(newPartList);
    }

    private final String SEPARATOR = "---------------------\n";

    private <E> void dumpPrintList(List<E> list) {
        final String SEPARATOR_LINE = "------------------------------------";
        System.out.println(SEPARATOR_LINE);
        list.forEach(System.out::println);
        System.out.println(SEPARATOR_LINE);
    }

    private Class loadOuterClass(String classDirPath, String fullClassName) throws MalformedURLException, ClassNotFoundException {
        File classDir = new File(classDirPath);
        URL classDirURL = classDir.toURI().toURL();
        URL[] urls = new URL[]{classDirURL};
        URLClassLoader urlClassLoader = new URLClassLoader(urls);
        return urlClassLoader.loadClass(fullClassName);
    }
    // java.lang.NoClassDefFoundError: org/evosuite/shaded/org/mockito/stubbing/Answer

    @Test
    public void testLoadOuterClass1() throws MalformedURLException, ClassNotFoundException {
        String dirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";
        String className = "net.mooctest.AStarTest";
        Class testClass = loadOuterClass(dirPath, className);
        System.out.println(testClass);

        Field[] declaredFields = testClass.getDeclaredFields();

    }
    // java.lang.NoClassDefFoundError: AStarTest (wrong name: net/mooctest/AStarTest)

    @Test
    public void testLoadOuterClass0() throws MalformedURLException, ClassNotFoundException {
        String dirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes/net/mooctest";
        String className = "AStarTest";
        Class aClass = loadOuterClass(dirPath, className);
        System.out.println(aClass);
    }

    @Test
    public void testURLClassLoader() throws MalformedURLException, ClassNotFoundException {
        String dirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/target/test-classes";
        File classDir = new File(dirPath);
        URL classDirURL = classDir.toURI().toURL();
        URL[] urls = new URL[]{classDirURL};

        URLClassLoader urlClassLoader = new URLClassLoader(urls);
        System.out.println(urlClassLoader);
        Class<?> testClass = urlClassLoader.loadClass("net.mooctest.AStarTest");
        System.out.println(testClass);
    }

    @Test
    public void testURL() throws MalformedURLException {
        String dirPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest";
        File classDir = new File(dirPath);
        System.out.println(classDir.toURL());
        URL url = classDir.toURL();
        URI uri = classDir.toURI();
    }

    @Test
    public void testGetIDByFileOperation2() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));


        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            if(line.equals("")) {
                continue;
            }
            if(line.contains("@Test") || line.contains("@org.junit.Test")) {
                contentBuilder.append(SEPARATOR);
            }
            contentBuilder.append(line).append("\n");
        }

        List<String> partList = Arrays.asList(contentBuilder.toString().split(SEPARATOR));
        partList.forEach((part) -> {
            System.out.println("****************************");
            System.out.print(part);
        });
    }

    // 这种从理论上就不行
    @Test
    public void testGetIDByFileOperation1() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        StringBuilder contentBuilder = new StringBuilder();
        while((line = br.readLine()) != null) {
            if(line.equals("")) {
                contentBuilder.append(SEPARATOR);
                continue;
            }
            contentBuilder.append(line).append("\n");
        }

/*        System.out.println(contentBuilder.toString());*/

        String[] parts = contentBuilder.toString().split(SEPARATOR);
        int cnt = 0;
        for (String part : parts) {
            System.out.println("---------------------------" + cnt++ +"---------------------------");
            System.out.println(part);
        }
    }

    @Test
    public void testGetIDByReflection() throws IOException, ClassNotFoundException {
        String className = FileUtil.fileSimpleNameExcludeSuffix(path);
        // get package name
        String packageLine = FileUtil.readContentByLineNumber(path, 1);

        String packageName = packageLine
                .replace("package", "")
                .replace(";", "")
                .trim();
        String fullClassName = packageName + DOT + className;
        //
        System.out.println(fullClassName);
        //

        Class<?> testClass = Class.forName(fullClassName);
        System.out.println(testClass);


    }

}
