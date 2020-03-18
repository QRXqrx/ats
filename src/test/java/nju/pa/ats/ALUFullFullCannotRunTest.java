package nju.pa.ats;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.SDG;
import nju.pa.ats.core.staticpa.StaticSlicer;
import nju.pa.ats.util.SlicerUtil;
import org.junit.Test;

import java.io.IOException;

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
        StaticSlicer staticSlicer = new StaticSlicer(scope, correctJavaPath);
        CallGraph cg = staticSlicer.getConfig().getCallGraph();
        SDG sdg = staticSlicer.getConfig().getSDG();



    }
}
