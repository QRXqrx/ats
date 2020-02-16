package nju.pa.ats.util;

import com.ibm.wala.ipa.slicer.Slicer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-15
 */
public class SettingUtil {

    /** Don't permit user construct this class, as this is a util class. */
    private SettingUtil() { }

    /**
     *
     * @param name The reference name of setting file. The file should under this directory
     * @param classLoader Provide a classLoader to load the properties into memory
     * @return Properties settings
     * @throws IOException when read setting file wrongly.
     */
    public static Properties loadSettings(String name, ClassLoader classLoader) throws IOException {
        InputStream is = classLoader.getResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }

    /**
     *
     * @param path Absolute path or relative path.
     * @return Properties settings
     * @throws IOException when read setting file wrongly.
     */
    public static Properties loadSettings(String path) throws IOException {
        /*InputStream is = classLoader.getResourceAsStream(path);*/
        BufferedReader br = new BufferedReader(new FileReader(path));
        Properties properties = new Properties();
        properties.load(br);
        return properties;
    }


    @Deprecated
    public static Slicer.DataDependenceOptions strToDDO(String ddoStr) {
        switch (ddoStr) {
            case "FULL":
                return Slicer.DataDependenceOptions.FULL;
            case "NO_BASE_PTRS":
                return Slicer.DataDependenceOptions.NO_BASE_PTRS;
            case "NO_BASE_NO_HEAP":
                return Slicer.DataDependenceOptions.NO_BASE_NO_HEAP;
            case "NO_BASE_NO_EXCEPTIONS":
                return Slicer.DataDependenceOptions.NO_BASE_NO_EXCEPTIONS;
            case "NO_BASE_NO_HEAP_NO_EXCEPTIONS":
                return Slicer.DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS;
            case "NO_HEAP":
                return Slicer.DataDependenceOptions.NO_HEAP;
            case "NO_HEAP_NO_EXCEPTIONS":
                return Slicer.DataDependenceOptions.NO_HEAP_NO_EXCEPTIONS;
            case "NO_EXCEPTIONS":
                return Slicer.DataDependenceOptions.NO_EXCEPTIONS;
            case "NONE":
                return Slicer.DataDependenceOptions.NONE;
            case "REFLECTION":
                return Slicer.DataDependenceOptions.REFLECTION;
            default:
                return Slicer.DataDependenceOptions.FULL;
        }
    }

    @Deprecated
    public static Slicer.ControlDependenceOptions strToCDO(String cdoStr) {
        switch (cdoStr) {
            case "FULL":
                return Slicer.ControlDependenceOptions.FULL;
            case "NONE":
                return Slicer.ControlDependenceOptions.NONE;
            case "NO_EXCEPTIONAL_EDGES":
                return Slicer.ControlDependenceOptions.NO_EXCEPTIONAL_EDGES;
            case "NO_INTERPROC_EDGES":
                return Slicer.ControlDependenceOptions.NO_INTERPROC_EDGES;
            case "NO_INTERPROC_NO_EXCEPTION":
                return Slicer.ControlDependenceOptions.NO_INTERPROC_NO_EXCEPTION;
            default:
                return Slicer.ControlDependenceOptions.FULL;
        }
    }

}
