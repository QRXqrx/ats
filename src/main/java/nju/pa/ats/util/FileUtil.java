package nju.pa.ats.util;

import java.io.*;
import java.util.*;

/**
 *
 * Provide several file operation methods for ATS.
 *
 * @author QRX QRXwzx@outlook.com
 * @date  2020-02-10
 */
public class FileUtil {

    /** Don't permit user construct this class, as this is a util class. */
    private FileUtil() { }

    public static String fileSimpleNameExcludeSuffix(String path) {
        File file = new File(path);
        return file.getName().replace(suffixOf(file), "");
    }

    /**
     * Read content from a txt file, one line for one item.
     *
     * @param path A path of a property file, written in a txt file.
     * @return A List of parsing result.
     */
    public static List<String> readContentsLineByLine(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }

        File file = new File(path);
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(path + ": cannot be read");
        }

        List<String> contents = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while((line = br.readLine()) != null) {
            contents.add(line);
        }

        br.close();
        return contents;
    }


    /**
     *
     * @param path A path of a file.
     * @param content The content needed be written into the file.
     * @return The absolute path of written file.
     * @throws IOException when write wrongly.
     */
    public static String writeContentIntoFile(String path, String content) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }

        File file = new File(path);
        if(!file.exists()) {
            boolean newFile = file.createNewFile();
            if(newFile) {
                System.out.println("Create new file: " + file.getAbsolutePath());
            } else {
                throw new RuntimeException("Create new file failed!");
            }
        }
        if(!file.canWrite()) {
            throw new IllegalArgumentException(path + ": cannot be written");
        }
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }

//        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.newLine();

        writer.close();
        return file.getAbsolutePath();
    }

    public static String writeContentsIntoFile(String path, List<String> contents) throws IOException {
        StringBuilder builder = new StringBuilder(contents.size() * 100);
        contents.forEach((content) -> builder.append(content).append(System.lineSeparator()));
        return writeContentIntoFile(path, builder.toString());
    }



    /**
     * Read contents from a file by line numbers.
     *
     * @param path A path of a file.
     * @param lineNumbers Line number which is corresponding to target contents.
     * @return A list of <code>String</code>s represent target contents
     * @throws IOException Throw IOException when read file wrong
     * @throws IllegalArgumentException When path is null.
     * @throws IllegalArgumentException When path is not a file path
     *
     * @date 2020-02-12
     */
    public static List<String> readContentsByLineNumbers(String path, HashSet<Integer> lineNumbers) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }

        File file = new File(path);
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(path + ": cannot be read");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> contents = new ArrayList<>();
        int cnt = 1;
        for(String line = reader.readLine() ; line != null; line = reader.readLine()) {
            if(lineNumbers.contains(cnt)) {
                contents.add(line);
            }
            cnt++;
        }

        reader.close();
        return contents;
    }

    /**
     * Read content from a file by line number.
     *
     * @param path A path of a file.
     * @param lineNumber Line number which is corresponding to target content.
     * @return A String of Target content.
     * @throws IOException Throw IOException when read file wrong
     * @throws IllegalArgumentException When line number is negative.
     * @throws IllegalArgumentException When path is null.
     * @throws IllegalArgumentException When path is not a file path.
     *
     * @date 2020-02-12
     */
    public static String readContentByLineNumber (String path, int lineNumber) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        if(lineNumber < 0) {
            throw new IllegalArgumentException("Negative line number.");
        }

        File file = new File(path);
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(path + ": cannot be read");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        int cnt = 1;
        for(String line = reader.readLine() ; line != null; line = reader.readLine()) {
            if(cnt == lineNumber) {
                reader.close();
                return line;
            }
            cnt++;
        }

        reader.close();
        return "";
    }

    /**
     * Get a suffix of a file.
     *
     * @param file A file.
     * @return A file's <param>file</param> suffix.
     *
     * @date  2020-02-10
     */
    public static String suffixOf(File file) {
        if(file.isDirectory()) {
            System.err.println("Warning: Directory has no explicit suffix!");
        }
        String absolutePath = file.getAbsolutePath();
        int lastDotPos = absolutePath.lastIndexOf('.');
        String suffix = "";
        if(lastDotPos != -1) {
            suffix = absolutePath.substring(lastDotPos);
        }
        return suffix;
    }

    /**
     *  Get all files that has a suffix as <param>suffix</param>
     *  under directory <param>dir</param> recursively.
     *
     * @param dir An absolute path of a directory.
     * @param suffix The type of target files. suffix should start with a '.'.
     * @return A List of target files.
     * @throws IllegalArgumentException When dir doesn't represent a directory.
     *
     * @date 2020-02-10
     */
    public static List<File> getAllFilesBySuffix(String  dir, String suffix) {
        File directory = new File(dir);
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException(dir + " should be a absolute path of a directory!");
        }
        // Give out warning when suffix doesn't start with '.'.
        if(suffix.charAt(0) != '.') {
            System.err.println(
                    "Warning: suffix \"" + suffix + "\" should start with a \'.\', system have added for you automatically."
            );
            suffix = "." + suffix;
        }
        // Get all files end with suffix recursively.
        List<File> targetFiles = new ArrayList<>();
        File[] allFiles = directory.listFiles();
        if(allFiles != null) {
            for (File file : allFiles) {
                if(file.isDirectory()) {
                    targetFiles.addAll(getAllFilesBySuffix(file.getAbsolutePath(), suffix));
                } else {
                    if(suffixOf(file).equals(suffix)) {
                        targetFiles.add(file);
                    }
                }
            }
        }
        return targetFiles;
    }


    @Deprecated
    public static String compileJavaFile(String outputDir, String ... javaPaths) {
        File outputDirectory = new File(outputDir);

        if(!outputDirectory.exists()) {
            System.err.println("Warning: \'" + outputDir + "\' does not exists!");
            boolean mkdirSuccess = outputDirectory.mkdir();
            if(mkdirSuccess) {
                System.err.println("Create output directory " + outputDirectory.getAbsolutePath() + "successfully!" );
            } else {
                System.err.println("Create output directory " + outputDirectory.getAbsolutePath() + "failed!");
            }

        /*  TODO: Different OS may have different cmd operations.
            else {
                throw new IOException("Create output directory failed!");
            }
        */
        }

        String outputPath = outputDirectory.getAbsolutePath();
        // TODO: Different OS may have different cmd operations.
        String CMD = "cmd.exe /c ";
        for (String javaPath : javaPaths) {
            String cmd = CMD + javaPath + " -d" + outputPath;
            System.out.println("cmd: " + cmd);
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputPath;
    }
}
