package nju.pa.ats.core.staticpa;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.CancelException;
import nju.pa.ats.core.result.AtomTestCase;
import nju.pa.ats.exception.NullTargetMethodException;
import nju.pa.ats.util.AtomUtil;
import nju.pa.ats.util.FileUtil;
import nju.pa.ats.util.SlicerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Add a new **StaticSlicer extends this, which provides methods for generating new test
 * TODO: class after slice.
 *
 * First StaticSlicer, implements <interface>StaticSliceService</interface>
 * You could construct a StaticSlicer by passing an AnalysisScope or SlicerConfig,
 * which also contains an AnalysisScope. So AnalysisScope is indisepensable for
 * a StaticSlicer. Please make sure your AnalysisScope is correct.
 *
 * For construct a slice, AnalysisScope and source path are indispensable.
 *
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public class StaticSlicer implements StaticSliceService {

    private SlicerConfig config;
    private String javaPath;
    private List<String> targetMethods;
    private boolean isDistinct = false;

    /**
     *
     * @param scope AnalysisScope for slice
     * @param javaPath From which we get readable source code.
     */
    public StaticSlicer(AnalysisScope scope, String javaPath) {
        this.config = new SlicerConfig(scope);
        this.javaPath = javaPath;
    }

    public StaticSlicer(AnalysisScope scope, String javaPath, boolean isDistinct) {
        this.config = new SlicerConfig(scope);
        this.javaPath = javaPath;
        this.isDistinct = isDistinct;
    }


    public StaticSlicer(AnalysisScope scope, String javaPath, List<String> targetMethods) {
        this.config = new SlicerConfig(scope);
        this.javaPath = javaPath;
        this.targetMethods = targetMethods;
    }

    public StaticSlicer(AnalysisScope scope, String javaPath, List<String> targetMethods, boolean isDistinct) {
        this.config = new SlicerConfig(scope);
        this.javaPath = javaPath;
        this.targetMethods = targetMethods;
        this.isDistinct = isDistinct;
    }

    public StaticSlicer(SlicerConfig config, String javaPath) {
        this.config = config;
        this.javaPath = javaPath;
    }

    public StaticSlicer(SlicerConfig config, String javaPath, boolean isDistinct) {
        this.config = config;
        this.javaPath = javaPath;
        this.isDistinct = isDistinct;
    }

    public StaticSlicer(SlicerConfig config, String javaPath, List<String> targetMethods) {
        this.config = config;
        this.javaPath = javaPath;
        this.targetMethods = targetMethods;
    }

    public StaticSlicer(SlicerConfig config, String javaPath, List<String> targetMethods, boolean isDistinct) {
        this.config = config;
        this.javaPath = javaPath;
        this.targetMethods = targetMethods;
        this.isDistinct = isDistinct;
    }

    // TODO: To private
    public AtomTestCase backwardSliceOne(Statement seed) {
        Collection slice = null;
        try {
            slice = com.ibm.wala.ipa.slicer.Slicer.computeBackwardSlice(
                    config.getSDG(),
                    seed
            );
        } catch (CancelException e) {
            e.printStackTrace();
        }

        List<Statement> statements;
        if(slice == null) {
            System.out.println("Warning: In backwardSliceOne, null slice result");
            return null;
        }
        statements = new ArrayList<>(slice);

        HashSet<Integer> lineNums = AtomUtil.toSrcLineNumbers(statements);
        List<String> srcLines = null;
        try {
            srcLines = FileUtil.readContentsByLineNumbers(javaPath, lineNums);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(srcLines == null) {
            System.out.println("Warning: In backwardSliceOne, null source lines.");
            return null;
        }

        AtomTestCase atc = new AtomTestCase("", srcLines);
        SlicerUtil.excludeExtraAssert(atc);
        return atc;
        /*List<String> toTrim = srcLines.stream().map(String::trim).collect(Collectors.toList());
        return new AtomTestCase(atsName, toTrim);*/
    }

    private String getAtcName() {
//        int loc = this.javaPath.lastIndexOf(File.separator);
//        return this.javaPath.substring(loc + 1).replace(".java", "");
        return FileUtil.fileSimpleNameExcludeSuffix(this.javaPath);
    }

    @Override
    public List<AtomTestCase> slice() {
        List<AtomTestCase> atomTestCases = new ArrayList<>();

        if(this.targetMethods == null) {
            throw new NullTargetMethodException("You should set target methods first!");
        }
        List<Statement> seeds = SlicerUtil.findSeedsBySrc(
                this.javaPath,
                this.config.getCallGraph(),
                this.targetMethods
        );

        for (Statement seed : seeds) {
            atomTestCases.add(backwardSliceOne(seed));
        }

        // distinct
        int cnt = 0;
        if(isDistinct) {
            atomTestCases = atomTestCases.stream().distinct().collect(Collectors.toList());
        }
        for (AtomTestCase atomTestCase : atomTestCases) {
            atomTestCase.setName(getAtcName() + String.valueOf(cnt));
            cnt++;
        }
        return atomTestCases;
    }



    public List<String> getTargetMethods() {
        return targetMethods;
    }

    public void setTargetMethods(List<String> targetMethods) {
        this.targetMethods = targetMethods;
    }

    public void addTargetMethod(String targetMethod) {
        if(this.targetMethods == null) {
            this.targetMethods = new ArrayList<>();
        }
        this.targetMethods.add(targetMethod);
    }

    public SlicerConfig getConfig() {
        return config;
    }

    public void setConfig(SlicerConfig config) {
        this.config = config;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    public void setDistinct(boolean distinct) {
        isDistinct = distinct;
    }
}
