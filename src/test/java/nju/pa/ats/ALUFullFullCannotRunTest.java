package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Statement;
import nju.pa.ats.core.slicer.Slicer;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 *
 * On linux:
 * When set a wrong java_src_path, slice process can ended(of course no applicable result),
 * When set a correct java_src_path, slice will stuck
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-16
 */
public class ALUFullFullCannotRunTest {
    @Test
    public void test() throws IOException {
        String classDir = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/target/test-classes/net/mooctest";
        String correctJavaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/ALU/src/test/java/net/mooctest/ALUTest.java";

        AnalysisScope scope = SlicerUtil.getDynamicScope(classDir, this.getClass().getClassLoader());
        Slicer slicer = new Slicer(scope, correctJavaPath);
        CallGraph cg = slicer.getConfig().getCallGraph();
        SDG sdg = slicer.getConfig().getSDG();



    }
}
