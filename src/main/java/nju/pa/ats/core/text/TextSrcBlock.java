package nju.pa.ats.core.text;

import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public class TextSrcBlock {

    private List<String> srcLines;

    public TextSrcBlock(List<String> srcLines) {
        this.srcLines = srcLines;
    }

    public String dumpBlock() {
        StringBuilder tryCatchBuilder = new StringBuilder();
        srcLines.forEach((line) -> tryCatchBuilder.append(line).append("\n"));
        return tryCatchBuilder.toString();
    }

    public List<String> getSrcLines() {
        return srcLines;
    }

    public void setSrcLines(List<String> srcLines) {
        this.srcLines = srcLines;
    }

    public boolean addSrcLine(String line) {
        return this.srcLines.add(line);
    }

    @Override
    public String toString() {
        return "TextSrcBlock[\n" +
                dumpBlock() +
                ']';
    }
}
