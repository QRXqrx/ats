package nju.pa.ats.core.result;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * AtomCodeSnippet represents a slice result. Generally, it should be indivisible.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-12
 */
public abstract class AtomCodeSnippet {

    /**
     * The simple method name of a code snippet.
     */
    private String name;

    /**
     * TODO: May be delete in the future.
     * The absolute path of source file of this unit.
     */
    private String sourcePath;

    /**
     * The source code lines corresponding to the numbers.
     */
    private List<String> sourceCodeLines;

    /**
     * @return A formulated code snippet,
     * composed of the lines from <code>sourceCodeLines</code>
     */
    public abstract String dumpSnippet();

    public AtomCodeSnippet(List<String> sourceCodeLines) {
        this.sourceCodeLines = sourceCodeLines;
    }

    public AtomCodeSnippet(String name) {
        this.name = name;
    }

    public AtomCodeSnippet(String name, List<String> sourceCodeLines) {
        this.name = name;
        this.sourceCodeLines = sourceCodeLines;
    }

    public AtomCodeSnippet(String name, String sourcePath) {
        this.name = name;
        this.sourcePath = sourcePath;
    }

    public AtomCodeSnippet(String name, String sourcePath, List<String> sourceCodeLines) {
        this.name = name;
        this.sourcePath = sourcePath;
        this.sourceCodeLines = sourceCodeLines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public List<String> getSourceCodeLines() {
        return sourceCodeLines;
    }

    public void setSourceCodeLines(List<String> sourceCodeLines) {
        this.sourceCodeLines = sourceCodeLines;
    }


    /**
     *
     * @param o anothor AtomCodeSnippet
     * @return false if,
     *         true if contents are same.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomCodeSnippet that = (AtomCodeSnippet) o;
        /*return dumpSnippet().equals(that.dumpSnippet());*/
        return this.sourceCodeLines.equals(that.getSourceCodeLines());
    }

    @Override
    public int hashCode() {
        /*return Objects.hash(name, sourcePath, sourceCodeLines);*/
        return Objects.hash(sourcePath, sourceCodeLines);
    }

    @Override
    public String toString() {
        return name + "[" + "\n"
                + dumpSnippet()
                + "]";
    }
}
