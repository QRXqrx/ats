package nju.pa.ats;

import nju.pa.ats.core.text.RefinedTextSlicer;
import nju.pa.ats.util.TextUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public class SliceTxtTest1 {

    private String javaPath = "C:/Users/QRX/Desktop/MyWorkplace/Postgraduate/Tasks/task4_atom_test_generation/ats/target/example-programs/AStar/src/test/java/net/mooctest/AStarTest.java";

    String test =
            "    @Test(timeout = 2000)\n" +
            "    public void testDirected() throws Throwable {\n" +
            "        DirectedGraph directedGraph = new DirectedGraph();\n" +
            "        Graph<Integer> graph = directedGraph.graph;\n" +
            "        assertEquals(0, graph.hashCode());\n" +
            "        assertTrue(graph.equals(graph));\n" +
            "        // 改成一行\n" +
            "        assertEquals(\"Value=1 weight=0\\n\" + \"\\t[ 1(0) ] -> [ 2(0) ] = 7\\n\" + \"\\t[ 1(0) ] -> [ 3(0) ] = 9\\n\" + \"\\t[ 1(0) ] -> [ 6(0) ] = 14\\n\" + \"\\t[ 1(0) ] -> [ 8(0) ] = 30\\n\" + \"Value=2 weight=0\\n\" + \"\\t[ 2(0) ] -> [ 3(0) ] = 10\\n\" + \"\\t[ 2(0) ] -> [ 4(0) ] = 15\\n\" + \"Value=3 weight=0\\n\" + \"\\t[ 3(0) ] -> [ 4(0) ] = 11\\n\" + \"\\t[ 3(0) ] -> [ 6(0) ] = 2\\n\" + \"Value=4 weight=0\\n\" + \"\\t[ 4(0) ] -> [ 5(0) ] = 6\\n\" + \"\\t[ 4(0) ] -> [ 7(0) ] = 16\\n\" + \"Value=5 weight=0\\n\" + \"Value=6 weight=0\\n\" + \"\\t[ 6(0) ] -> [ 5(0) ] = 9\\n\" + \"\\t[ 6(0) ] -> [ 8(0) ] = 14\\n\" + \"Value=7 weight=0\\n\" + \"Value=8 weight=0\\n\", graph.toString());\n" +
            "        Graph<Integer> graph2 = new Graph<Integer>(graph);\n" +
            "        graph2.getVertices().add(new Vertex<>(2333));\n" +
            "        assertFalse(graph2.equals(graph));\n" +
            "        graph2 = new Graph<>(graph);\n" +
            "        graph2.getEdges().add(new Edge<Integer>(233, new Vertex<Integer>(1), new Vertex<Integer>(2)));\n" +
            "        assertFalse(graph2.equals(graph));\n" +
            "        graph2 = new Graph<Integer>(graph);\n" +
            "        graph2.getVertices().remove(0);\n" +
            "        graph2.getVertices().add(new Vertex<>(2333));\n" +
            "        assertFalse(graph2.equals(graph));\n" +
            "        graph2 = new Graph<>(graph);\n" +
            "        graph2.getEdges().remove(0);\n" +
            "        graph2.getEdges().add(new Edge<Integer>(233, new Vertex<Integer>(1), new Vertex<Integer>(2)));\n" +
            "        assertFalse(graph2.equals(graph));\n" +
            "        ArrayList<Edge<Integer>> edges = new ArrayList<>(graph.getEdges());\n" +
            "        for (int i = 0; i < graph.getEdges().size(); i++)\n" +
            "            edges.add(graph.getEdges().get(i));\n" +
            "        graph2 = new Graph<>(TYPE.DIRECTED, graph.getVertices(), edges);\n" +
            "        assertEquals(24, graph2.getEdges().size());\n" +
            "        Vertex<Integer> vertex = new Vertex<>(12);\n" +
            "        Vertex<Integer> vertex1 = new Vertex<>(12);\n" +
            "        Edge<Integer> edge = new Edge<>(1, vertex1, vertex);\n" +
            "        vertex.getEdges().add(edge);\n" +
            "        vertex1.getEdges().add(new Edge<>(2, vertex1, vertex));\n" +
            "        assertEquals(-1, vertex.compareTo(vertex1));\n" +
            "        assertEquals(1, vertex1.compareTo(vertex));\n" +
            "        vertex = new Vertex<>(12);\n" +
            "        vertex1 = new Vertex<>(13);\n" +
            "        assertTrue(edge.equals(edge));\n" +
            "        assertTrue(!edge.equals(null));\n" +
            "        assertTrue(edge.equals(edge));\n" +
            "        Edge<Integer> edge2 = new Edge<>(1, vertex1, vertex);\n" +
            "        assertFalse(edge2.equals(edge));\n" +
            "        edge2 = new Edge<>(1, vertex, vertex1);\n" +
            "        edge = new Edge<>(1, vertex1, vertex);\n" +
            "        assertFalse(edge2.equals(edge));\n" +
            "        edge2 = new Edge<>(1, vertex, vertex1);\n" +
            "        edge = new Edge<>(1, vertex, vertex);\n" +
            "        assertFalse(edge2.equals(edge));\n" +
            "        edge2 = new Edge<>(1, vertex, vertex1);\n" +
            "        edge = new Edge<>(1, vertex, vertex);\n" +
            "        assertEquals(1, edge2.compareTo(edge));\n" +
            "        vertex = new Vertex<>(12);\n" +
            "        vertex1 = new Vertex<>(12);\n" +
            "        edge = new Edge<>(1, vertex1, vertex);\n" +
            "        vertex.getEdges().add(edge);\n" +
            "        vertex1.getEdges().add(new Edge<>(2, vertex1, vertex));\n" +
            "        assertFalse(vertex.equals(vertex1));\n" +
            "        CostPathPair<Integer> pathpair = getIdealPathPair(directedGraph);\n" +
            "        assertEquals(100440, pathpair.hashCode());\n" +
            "        assertTrue(pathpair.equals(pathpair));\n" +
            "        // 换成一行\n" +
            "        assertEquals(\"Cost = 20\\n\" + \"\\t[ 1(0) ] -> [ 3(0) ] = 9\\n\" + \"\\t[ 3(0) ] -> [ 6(0) ] = 2\\n\" + \"\\t[ 6(0) ] -> [ 5(0) ] = 9\\n\", pathpair.toString());\n" +
            "        CostPathPair<Integer> pathpair1 = getIdealPathPair(directedGraph);\n" +
            "        pathpair1.setCost(-10000);\n" +
            "        assertTrue(!pathpair1.equals(pathpair));\n" +
            "        pathpair1 = getIdealPathPair(directedGraph);\n" +
            "        ArrayList<Edge<Integer>> path = new ArrayList<Edge<Integer>>();\n" +
            "        path.add(new Edge<Integer>(1, vertex, vertex));\n" +
            "        ArrayList<Edge<Integer>> path1 = new ArrayList<Edge<Integer>>();\n" +
            "        path1.add(new Edge<Integer>(2, vertex, vertex));\n" +
            "        CostPathPair<Integer> costPair = new CostPathPair<Integer>(123, path);\n" +
            "        CostPathPair<Integer> costPair1 = new CostPathPair<Integer>(123, path1);\n" +
            "        assertFalse(costPair.equals(costPair1));\n" +
            "        CostVertexPair<Integer> costVert = new CostVertexPair<Integer>(2, new Vertex<Integer>(2));\n" +
            "        assertFalse(costVert.equals(null));\n" +
            "        assertFalse(costVert.equals(new CostVertexPair<Integer>(3, new Vertex<Integer>(3))));\n" +
            "        assertFalse(costVert.equals(new CostVertexPair<Integer>(2, new Vertex<Integer>(3))));\n" +
            "        assertTrue(costVert.equals(costVert));\n" +
            "        assertEquals(3844, costVert.hashCode());\n" +
            "        try {\n" +
            "\n" +
            "            costVert = new CostVertexPair<Integer>(2, null);\n" +
            "            assertFalse(costVert.equals(null));\n" +
            "            assertEquals(3844, costVert.hashCode());\n" +
            "            fail();\n" +
            "        } catch (Exception e) {\n" +
            "            assertEquals(\"vertex cannot be NULL.\", e.getMessage());\n" +
            "        }\n" +
            "        Field vertexField = getField(CostVertexPair.class, \"vertex\");\n" +
            "        costVert = new CostVertexPair<Integer>(2, new Vertex<Integer>(3));\n" +
            "        vertexField.set(costVert, null);\n" +
            "        assertEquals(62, costVert.hashCode());\n" +
            "        graph = new Graph<>(TYPE.UNDIRECTED, new ArrayList<>(), directedGraph.edges);\n" +
            "        assertEquals(12, graph.getEdges().size());\n" +
            "        graph = new Graph<>(TYPE.UNDIRECTED, new UndirectedGraph().verticies, new UndirectedGraph().edges);\n" +
            "        assertEquals(14, graph.getEdges().size());\n" +
            "        AStar<Integer> astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v3, new UndirectedGraph().v5);\n" +
            "        assertEquals(2, edges.size());\n" +
            "        assertEquals(2, edges.get(0).getCost());\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new DirectedGraph().v3, new DirectedGraph().v5);\n" +
            "        assertEquals(2, edges.size());\n" +
            "        assertEquals(2, edges.get(0).getCost());\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new DirectedGraph().v1, new DirectedGraph().v7);\n" +
            "        assertEquals(3, edges.size());\n" +
            "        assertEquals(9, edges.get(0).getCost());\n" +
            "        graph = new Graph<>();\n" +
            "        graph.getEdges().add(new Edge<Integer>(3, new Vertex<Integer>(1), new Vertex<Integer>(2)));\n" +
            "        graph.getEdges().add(new Edge<Integer>(4, new Vertex<Integer>(1), new Vertex<Integer>(2)));\n" +
            "        graph.getVertices().add(new Vertex<Integer>(3));\n" +
            "        graph.getVertices().add(new Vertex<Integer>(4));\n" +
            "        vertex = new Vertex<Integer>(2, 3);\n" +
            "        vertex.addEdge(new Edge<Integer>(4, vertex, vertex));\n" +
            "        assertEquals(186, vertex.hashCode());\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new DirectedGraph().v5, new DirectedGraph().v8);\n" +
            "        assertNull(edges);\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new DirectedGraph().v2, new DirectedGraph().v6);\n" +
            "        assertEquals(2, edges.size());\n" +
            "        assertEquals(10, edges.get(0).getCost());\n" +
            "        edge = new Edge<Integer>(3, new Vertex<Integer>(4), new Vertex<>(5));\n" +
            "        assertEquals(1787460, edge.hashCode());\n" +
            "        graph = new Graph<>();\n" +
            "        graph.getEdges().add(new Edge<Integer>(3, new Vertex<Integer>(1), new Vertex<Integer>(2)));\n" +
            "        graph.getVertices().add(new Vertex<Integer>(4));\n" +
            "        assertEquals(0, graph.getEdges().get(0).hashCode() * graph.getVertices().get(0).hashCode() * (graph.getVertices().size() + graph.getEdges().size() + graph.getType().hashCode()) * 31 - graph.hashCode());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new DirectedGraph().v3, new UndirectedGraph().v5);\n" +
            "        assertNull(edges);\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v7, new UndirectedGraph().v5);\n" +
            "        assertEquals(4, edges.size());\n" +
            "        assertEquals(1, edges.get(0).getCost());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v7, new UndirectedGraph().v1);\n" +
            "        assertEquals(1, edges.size());\n" +
            "        assertEquals(1, edges.get(0).getCost());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v3, new UndirectedGraph().v5);\n" +
            "        assertEquals(2, edges.size());\n" +
            "        assertEquals(2, edges.get(0).getCost());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v3, new UndirectedGraph().v7);\n" +
            "        assertEquals(2, edges.size());\n" +
            "        assertEquals(9, edges.get(0).getCost());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new UndirectedGraph().graph, new UndirectedGraph().v3, new UndirectedGraph().v3);\n" +
            "        assertEquals(0, edges.size());\n" +
            "        astar = new AStar<Integer>();\n" +
            "        edges = (ArrayList<Edge<Integer>>) astar.aStar(new DirectedGraph().graph, new UndirectedGraph().v3, new UndirectedGraph().v3);\n" +
            "        assertEquals(0, edges.size());\n" +
            "    }";

    /*@Test
    public void test0() throws IOException {

        RefinedTextSlicer textSlicer = new RefinedTextSlicer(javaPath);
        TextUtil.dump(textSlicer.splitOneTest(test));
    }*/

    /*@Test
    public void test1() throws IOException {
        RefinedTextSlicer textSlicer = new RefinedTextSlicer(javaPath);
        TextUtil.dump(textSlicer.buildNewTests());
    }*/
}
