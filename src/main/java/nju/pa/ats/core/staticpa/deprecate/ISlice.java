package nju.pa.ats.core.staticpa.deprecate;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;

import java.util.List;

/**
 * @author QRX QRXwzx@outlook.com
 * @since 2020/02/11
 */
public interface ISlice {

    /**
     * Find seed statement.
     *
     * @param node A node of call graph.
     * @param methodName Find the call site to a method named <param>methodName</param>
     * @return
     */
    public Statement findCallTo(CGNode node, String methodName);

    public List<Integer> sliceToLineNumbers();
}
