package nju.pa.ats.core.staticpa;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.util.MonitorUtil;

/**
 * The basic configurations of a StaticSlicer.
 * AnalysisScope, ClassHierarchy, AnalysisOptions, ProgressMonitor,
 * EntryPoint, CallGraph, PointerAnalysis are configurations for creating an SDG,
 * while Data & Control DependenceOptions are for slice algorithm.
 * You can construct a configuration by passing an single AnalysisScope, or you
 * can choose to pass all needed objects, too. For missing parts, SlicerConfig will
 * make default configurations automatically. The all-filed constructor misses several
 * fields, that's because they are closely associated to other fields
 * (e.g. AnalysisOptions is determined by Entrypoint and AnalysisScope), so it is
 * meaningless to expose these fields.
 *
 * @see AnalysisScope
 * @see ClassHierarchy
 * @see AnalysisOptions
 * @see MonitorUtil.IProgressMonitor
 * @see Entrypoint
 * @see CallGraph
 * @see PointerAnalysis
 * @see com.ibm.wala.ipa.callgraph.impl.Util
 * @see com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions
 * @see com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public class SlicerConfig {

    private final AnalysisCacheImpl cache = new AnalysisCacheImpl();

    private MonitorUtil.IProgressMonitor monitor = null;
    private AnalysisScope scope;
    private ClassHierarchy cha;
    private Iterable<Entrypoint> eps;
    private AnalysisOptions options;
    private CallGraphBuilder builder;
    private CallGraph cg;
    private PointerAnalysis pa;
    private SDG sdg;

    /**
     * You can choose other options, wala has defined several options for slicing.
     *
     * @see com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions
     * @see com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions
     * @date 2020-02-13
     */
    Slicer.DataDependenceOptions ddo = Slicer.DataDependenceOptions.FULL;
    Slicer.ControlDependenceOptions cdo = Slicer.ControlDependenceOptions.FULL;

    public SlicerConfig(
            MonitorUtil.IProgressMonitor monitor,
            AnalysisScope scope,
            ClassHierarchy cha,
            Iterable<Entrypoint> eps,
            CallGraphBuilder builder,
            Slicer.DataDependenceOptions ddo,
            Slicer.ControlDependenceOptions cdo) {
        this.monitor = monitor;
        this.scope = scope;
        this.cha = cha;
        this.eps = eps;
        this.builder = builder;
        this.ddo = ddo;
        this.cdo = cdo;
        this.options = new AnalysisOptions(scope, eps);

        try {
            this.cg = builder.makeCallGraph(options, monitor);
        } catch (CallGraphBuilderCancelException e) {
            e.printStackTrace();
        }

        this.pa = builder.getPointerAnalysis();
        this.sdg = new SDG(this.cg, this.pa, this.ddo, this.cdo);
    }

    public SlicerConfig(
            MonitorUtil.IProgressMonitor monitor,
            AnalysisScope scope,
            ClassHierarchy cha,
            Iterable<Entrypoint> eps,
            CallGraphBuilder builder) {
        new SlicerConfig(
                monitor,
                scope,
                cha,
                eps,
                builder,
                Slicer.DataDependenceOptions.FULL,
                Slicer.ControlDependenceOptions.FULL
        );
    }

    public SlicerConfig(AnalysisScope scope, Slicer.DataDependenceOptions ddo, Slicer.ControlDependenceOptions cdo) {
        this.scope = scope;
        this.ddo = ddo;
        this.cdo = cdo;

        try {
            this.cha = ClassHierarchyFactory.makeWithRoot(scope);
        } catch (ClassHierarchyException e) {
            e.printStackTrace();
        }
        this.eps = new AllApplicationEntrypoints(scope, this.cha);
        this.options = new AnalysisOptions(scope, eps);
        this.builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, cache, cha, scope
        );

        try {
            this.cg = this.builder.makeCallGraph(this.options, this.monitor);
        } catch (CallGraphBuilderCancelException e) {
            e.printStackTrace();
        }

        this.pa = this.builder.getPointerAnalysis();
        this.sdg = new SDG(this.cg, this.pa, this.ddo, this.cdo);
    }

    public SlicerConfig(AnalysisScope scope) {
        new SlicerConfig(scope, Slicer.DataDependenceOptions.FULL, Slicer.ControlDependenceOptions.FULL);
    }

    public AnalysisCacheImpl getCache() {
        return cache;
    }

    public MonitorUtil.IProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorUtil.IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public AnalysisScope getScope() {
        return scope;
    }

    public void setScope(AnalysisScope scope) {
        this.scope = scope;
    }

    public ClassHierarchy getClassHierarchy() {
        return cha;
    }

    public void setClassHierarchy(ClassHierarchy cha) {
        this.cha = cha;
    }

    public Iterable<Entrypoint> getEntryPoints() {
        return eps;
    }

    public void setEntryPoints(Iterable<Entrypoint> eps) {
        this.eps = eps;
    }

    public AnalysisOptions getOptions() {
        return options;
    }

    public void setOptions(AnalysisOptions options) {
        this.options = options;
    }

    public CallGraphBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(CallGraphBuilder builder) {
        this.builder = builder;
    }

    public CallGraph getCallGraph() {
        return cg;
    }

    public void setgetCallGraph(CallGraph cg) {
        this.cg = cg;
    }

    public PointerAnalysis getPointerAnalysis() {
        return pa;
    }

    public void setPointerAnalysis(PointerAnalysis pa) {
        this.pa = pa;
    }

    public Slicer.DataDependenceOptions getDataDependenceOptions() {
        return ddo;
    }

    public void setDataDependenceOptions(Slicer.DataDependenceOptions ddo) {
        this.ddo = ddo;
    }

    public Slicer.ControlDependenceOptions getControlDependenceOptions() {
        return cdo;
    }

    public void setControlDependenceOptions(Slicer.ControlDependenceOptions cdo) {
        this.cdo = cdo;
    }

    public SDG getSDG() {
        return sdg;
    }

    public void setSDG(SDG sdg) {
        this.sdg = sdg;
    }
}
