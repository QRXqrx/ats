package nju.pa.ats;

import com.ibm.wala.ipa.slicer.Slicer;
import nju.pa.ats.util.SettingUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-15
 */
public class ParseSettingTest {
    
    @Test
    public void test1() throws IOException {
        String absPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/src/main/resources/deprecated-settings.properties";
        Properties settings = SettingUtil.loadSettings(absPath);
        String classDir = settings.getProperty("class_dir_path");
        String javaPath = settings.getProperty("java_src_path");
        String outputDir = settings.getProperty("output_dir_path");
        Slicer.DataDependenceOptions ddo = SettingUtil.strToDDO(settings.getProperty("data_dependency_option"));
        Slicer.ControlDependenceOptions cdo = SettingUtil.strToCDO(settings.getProperty("control_dependency_option"));

        System.out.println(classDir);
        System.out.println(javaPath);
        System.out.println(outputDir);
        System.out.println(ddo.getClass().toString() + ": " + ddo);
        System.out.println(cdo.getClass().toString() + ": " + cdo);
    }

    @Test
    public void test0() throws IOException {
        String shortPath = "deprecated-settings.properties";
        Properties settings = SettingUtil.loadSettings(shortPath, this.getClass().getClassLoader());
        String classDir = settings.getProperty("class_dir_path");
        String javaPath = settings.getProperty("java_src_path");
        String outputDir = settings.getProperty("output_dir_path");
        Slicer.DataDependenceOptions ddo = SettingUtil.strToDDO(settings.getProperty("data_dependency_option"));
        Slicer.ControlDependenceOptions cdo = SettingUtil.strToCDO(settings.getProperty("control_dependency_option"));

        System.out.println(classDir);
        System.out.println(javaPath);
        System.out.println(outputDir);
        System.out.println(ddo.getClass().toString() + ": " + ddo);
        System.out.println(cdo.getClass().toString() + ": " + cdo);
    }


}
