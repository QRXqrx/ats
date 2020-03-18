import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.core.staticpa.StaticSlicer;
import nju.pa.ats.core.staticpa.SlicerConfig;
import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SettingUtil;
import nju.pa.ats.util.SlicerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-29
 */
public class SliceProcess {

    public static void main(String[] args) {
        run(args);
    }

    private SliceProcess() { }

    public static void run(String[] args) {

        System.out.println("Start to load settings...");
        long time1 = System.currentTimeMillis();
        // Load settings
        String settingPath = args[0];
        Properties settings = null;
        try {
            settings = SettingUtil.loadSettings(settingPath);
        } catch (IOException e) {
            System.out.println("In SliceProcess@run(), load settings failed: " + settingPath);
            e.printStackTrace();
            System.exit(-1);
        }

        String classDir = settings.getProperty("class_dir_path");
        String javaPath = settings.getProperty("java_src_path");
        String outputDir = settings.getProperty("output_dir_path");
        String targetMethodsPath = settings.getProperty("target_methods_path");

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
        List<AtomTestCase> atomTestCases = makeSlice(classDir, javaPath, ddo, cdo, isDistinct, targetMethodsPath);
        long time4 = System.currentTimeMillis();
        System.out.println("Slice done, time(ms): " + (time4 - time3));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");


        System.out.println("Start to write into file...");
        long time5 = System.currentTimeMillis();

        // Output to file
//        String outputFileName = settings.getProperty("output_file_name");
        String outputFileName = makeOutputName(
                javaPath,
                settings.getProperty("data_dependency_option"),
                settings.getProperty("control_dependency_option"),
                settings.getProperty("is_distinct")
        );

        System.out.println("OutputFileName: " + outputFileName);

        //
        outputToFile(outputDir, outputFileName, atomTestCases);
        long time6 = System.currentTimeMillis();
        System.out.println("Writing into file done, time(ms): " + (time6 - time5));
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
    }

    private static List<AtomTestCase> makeSlice(
            String classDir,
            String javaPath,
            com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions ddo,
            com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions cdo,
            boolean isDistinct,
            String targetMethodsPath
    ) {
        // Slice
        SlicerConfig config = null;
        try {
            config = new SlicerConfig(
                    SlicerUtil.getDynamicScope(classDir, Main.class.getClassLoader()),
                    ddo,
                    cdo
            );
        } catch (IOException e) {
            System.out.println("In SliceProcess@makeSlice, getDynamicScope failed: " + classDir);
            e.printStackTrace();
            System.exit(-1);
        }

        List<String> targetMethods = null;
        try {
            targetMethods = SlicerUtil.parseTargets(targetMethodsPath);
        } catch (IOException e) {
            System.out.println("In SliceProcess@makeSlice, parse target methods failed: " + classDir);
            e.printStackTrace();
            System.exit(-1);
        }
        StaticSlicer staticSlicer = new StaticSlicer(config, javaPath, targetMethods, isDistinct);
        return staticSlicer.slice();
    }



    private static String makeOutputName(String javaPath, String ddoStr, String cdoStr, String distinctStr) {
        final String SEPARATOR = "-";
        return FileUtil.fileSimpleNameExcludeSuffix(javaPath) + SEPARATOR +
                ddoStr + SEPARATOR +
                cdoStr + SEPARATOR +
                distinctStr;
    }


    private static void outputToFile(String outputDir, String outputFileName , List<AtomTestCase> atomTestCases) {
        // Output to file
        final String TXT_SUFFIX = ".txt";
        if(!outputFileName.contains(TXT_SUFFIX)) {
            outputFileName += TXT_SUFFIX;
        }

        String absFilePath = outputDir + System.getProperty("file.separator") + outputFileName;
        StringBuilder contentBuilder = new StringBuilder();
        atomTestCases.forEach((atc) ->
                contentBuilder.append(atc.dumpSnippet()).append("\n\n")
        );
        try {
            FileUtil.writeContentIntoFile(absFilePath, contentBuilder.toString());
        } catch (IOException e) {
            System.out.println("In SliceProcess@outputToFile, write to file failed: " + absFilePath);
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
