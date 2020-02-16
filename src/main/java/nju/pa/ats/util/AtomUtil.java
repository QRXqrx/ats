package nju.pa.ats.util;


import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

import java.util.HashSet;
import java.util.List;

/**
 * Help user to create an atom code snippet.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-12
 */
public class AtomUtil {

    /** Don't permit user construct this class, as this is a util class. */
    private AtomUtil() {}

    /**
     * Use wala API to get the source code in concrete method.
     *
     * @param s A wala statement.
     * @return Source code line number corresponding to the statement.
     *         0 if statement is not normal.
     *         -1 if statement is not a ShrikeBTMethod
     * @see ShrikeBTMethod
     */
    public static int srcLineNumberOf(Statement s) {
        if(s.getKind() != Statement.Kind.NORMAL) {
            return 0;
        }
        NormalStatement statement = (NormalStatement) s;

        int instructionIndex = statement.getInstructionIndex();
        IMethod method = statement.getNode().getMethod();

        if(method instanceof ShrikeBTMethod) {
            try {
                int bcIndex = ((ShrikeBTMethod) method).getBytecodeIndex(instructionIndex);
                return method.getLineNumber(bcIndex);
            } catch (InvalidClassFileException e) {
                System.err.println("Bytecode index no good");
                System.err.println(e.getMessage());
            }
        }

        return -1;
    }


    /**
     * Get a set of line numbers represent the concrete code snippet.
     *
     * @param statements <code>Statement</code>s need transforming.
     * @return A set of line numbers.
     */
    public static HashSet<Integer> toSrcLineNumbers(List<Statement> statements) {
        HashSet<Integer> lineNumbers = new HashSet<>();

        statements.forEach((s) -> {
            int num = srcLineNumberOf(s);
            /* TODO: These error logs look useless
            if(num == 0) {
                System.err.println("A special statement.");
            } else if(num == -1) {
                System.err.println("Cannot find source code line number. Statement probably is not a BT method ");
            }
             */
            if(num > 0) {
                lineNumbers.add(num);
            }
        });

        return lineNumbers;
    }

}
