import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.slicer.Slicer;
import nju.pa.ats.core.slicer.SlicerConfig;
import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SettingUtil;
import nju.pa.ats.util.SlicerUtil;
import nju.pa.ats.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-06
 */
public class SliceProcessMultiJavaFiles {

    public static void main(String[] args) {
        run(args);
    }

    private SliceProcessMultiJavaFiles() { }

    public static void run(String args[]) {
        System.out.println("Start to load settings...");
        long time1 = System.currentTimeMillis();
        // load settings
        String settingsPath = args[0];
        Properties settings = null;
        try {
            settings = SettingUtil.loadSettings(settingsPath);
        } catch (IOException e) {
            System.out.println("In SliceProcess@run(), load settings failed: " + settingsPath);
            e.printStackTrace();
            System.exit(-1);
        }

        String classDirPath = settings.getProperty("class_dir_path");
        String javaDirPath = settings.getProperty("java_dir_path");
        String outputDir = settings.getProperty("output_dir_path");
        String targetsPath = settings.getProperty("targets_path");
        String exclusionPath = settings.getProperty("exclusions_path");
        exclusionPath = "default".equals(exclusionPath) ? "" : exclusionPath;

        com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions ddo
                = com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions.valueOf(
                settings.getProperty("data_dependency_option")
        );
        com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions cdo
                = com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions.valueOf(
                settings.getProperty("control_dependency_option")
        );

        boolean isDistinct = settings.getProperty("is_distinct").equalsIgnoreCase("true");
        long time2 = System.currentTimeMillis();
        System.out.println("Load settings done, time(ms): " + (time2 - time1));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        System.out.println("Start to make slices...");
        long time3 = System.currentTimeMillis();


        // Make slice
        Map<String, List<AtomTestCase>> slicesMap
                = makeSlice(javaDirPath, classDirPath, ddo, cdo, isDistinct, targetsPath, exclusionPath);

        long time4 = System.currentTimeMillis();
        System.out.println("Slice done, time(ms): " + (time4 - time3));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        // output
        System.out.println("Start to write into file...");
        long time5 = System.currentTimeMillis();

        List<String> outputFileList = outputSliceResults(
                settings.getProperty("data_dependency_option"),
                settings.getProperty("control_dependency_option"),
                settings.getProperty("is_distinct"),
                slicesMap,
                outputDir
        );
        System.out.println("Here are the output files: ");
        TextUtil.dump(outputFileList, "");

        long time6 = System.currentTimeMillis();
        System.out.println("Writing into file done, time(ms): " + (time6 - time5));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

    }

    private static String makeOutputName(String javaPath, String ddoStr, String cdoStr, String distinctStr) {
        final String SEPARATOR = "-";
        return FileUtil.fileSimpleNameExcludeSuffix(javaPath) + SEPARATOR +
                ddoStr + SEPARATOR +
                cdoStr + SEPARATOR +
                distinctStr;
    }

    private static String outputOne(
            String ddoStr,
            String cdoStr,
            String distinctStr,
            String javaPath,
            List<AtomTestCase> testCases,
            String outputDir
    ) {
        File dir = new File(outputDir);
        if(!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if(mkdirs) {
                System.out.println("Make directory: " + dir.getAbsolutePath());
            }
        }
        String outputFileName = makeOutputName(javaPath, ddoStr, cdoStr, distinctStr) + FileUtil.TXT_SUFFIX;
        File file = new File(dir, outputFileName);

        StringBuilder contentBuilder = new StringBuilder();
        testCases.forEach((testCase) -> contentBuilder.append(testCase.dumpSnippet()).append("\n\n"));

        try {
            FileUtil.writeContentIntoFile(file.getAbsolutePath(), contentBuilder.toString());
        } catch (IOException e) {
            System.out.println("In SliceProcessMultiJavaFiles@outputOne");
            e.printStackTrace();
            System.exit(-1);
        }

        return file.getAbsolutePath();
    }

    private static List<String> outputSliceResults(
            String ddoStr,
            String cdoStr,
            String distinctStr,
            Map<String, List<AtomTestCase>> slicesMap,
            String outputDir
    ) {
        List<String> outputFilePaths = new ArrayList<>();
        slicesMap.forEach((javaPath, testCases) -> {
            String outputFilePath = outputOne(ddoStr, cdoStr, distinctStr, javaPath, testCases, outputDir);
            outputFilePaths.add(outputFilePath);
        });

        return outputFilePaths;
    }

    //<javaAbsPath, List<AtomTestCase>>
    private static Map<String, List<AtomTestCase>> makeSlice(
            String javaDir,
            String classDir,
            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions ddo,
            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions cdo,
            boolean isDistinct,
            String targetsPath,
            String exPath
    ) {

        Map<String, List<File>> javaToClassesMap = SlicerUtil.relateSrcFileWithClasses(
                FileUtil.getAllFilesBySuffix(javaDir, FileUtil.JAVA_SUFFIX),
                FileUtil.getAllFilesBySuffix(classDir, FileUtil.CLASS_SUFFIX)
        );

        Map<String, List<AtomTestCase>> resultMap = new HashMap<>();
        javaToClassesMap.forEach((javaPath, classFiles) -> {

            System.out.println("Now Slice: " + javaPath);
            SlicerConfig config = null;
            try {
                config = new SlicerConfig(
                        SlicerUtil.getDynamicScope(classFiles, exPath, Main.class.getClassLoader()),
                        ddo,
                        cdo
                );
            } catch (IOException e) {
                System.out.println("In SliceProcessMultiJavaFiles@makeSlice, getDynamicScope failed: " + classDir);
                e.printStackTrace();
                System.exit(-1);
            }

            List<String> sliceTargets = null;
            try {
                sliceTargets = SlicerUtil.parseTargets(targetsPath);
            } catch (IOException e) {
                System.out.println("In SliceProcessMultiJavaFiles@makeSlice, parse targets failed: " + targetsPath);
                e.printStackTrace();
                System.exit(-1);
            }

            Slicer slicer = new Slicer(config, javaPath, sliceTargets, isDistinct);
            resultMap.put(javaPath, slicer.slice());
        });

        return resultMap;
    }
}
