/// SUBMITTED BY
/// MUBEEN KHAN - 2141867
/// DANAH ABU QRAIS - 2252167
/// TRISHAN KUMARESWARAN - 2111737

package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Assume;

/**
 * Property-based tests for the Graph<V> class.
 * Each property verifies a single cohesive behavior to help isolate faults.
 *
 * Submitted by:
 * FIRSTNAME0 LASTNAME0 - 123456789
 * FIRSTNAME1 LASTNAME1 - 213456789
 * FIRSTNAME2 LASTNAME2 - 321456789
 */
public class GraphProperties {

    // -------------------- Vertex Properties --------------------

    /**
     * Test that a newly created graph is empty.
     * @param g An empty graph.
     * @return true if the graph has no vertices.
     */
    @Property
    public boolean newGraphIsEmpty(@ForAll("emptyGraph") Graph<Integer> g) {
        return g.getVertices().isEmpty();
    }

    /**
     * Test that adding a new vertex increases the vertex count.
     * @param g A graph generated from the default generator.
     * @param v A random integer vertex.
     * @return true if the vertex is added and the size increases.
     */
    @Property
    public boolean addNewVertexIncreasesSize(@ForAll("graph") Graph<Integer> g, @ForAll Integer v) {
        Assume.that(!g.getVertices().contains(v));
        int sizeBefore = g.getVertices().size();
        g.addVertex(v);
        return g.getVertices().size() == sizeBefore + 1 && g.getVertices().contains(v);
    }

    /**
     * Test that adding an existing vertex does not change the graph.
     * @param g A non-empty graph.
     * @return true if the graph remains unchanged.
     */
    @Property
    public boolean addExistingVertexDoesNothing(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> before = new ArrayList<>(g.getVertices());
        Assume.that(!before.isEmpty());
        Integer v = before.get(0);
        g.addVertex(v);
        List<Integer> after = g.getVertices();
        return after.size() == before.size() && new HashSet<>(before).equals(new HashSet<>(after));
    }

    /**
     * Test that a null vertex cannot be added.
     * The graph may either ignore null or throw a NullPointerException.
     * @param g A graph from the default generator.
     * @return true if null is not added.
     */
    @Property
    public boolean nullCannotBeVertex(@ForAll("graph") Graph<Integer> g) {
        try {
            g.addVertex(null);
            return !g.getVertices().contains(null);
        } catch (NullPointerException e) {
            return true;
        }
    }

    /**
     * Test that removing an existing vertex decreases the vertex count.
     * @param g A non-empty graph.
     * @return true if the vertex is removed and the count decreases.
     */
    @Property
    public boolean removeExistingVertexDecreasesSize(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(!verts.isEmpty());
        Integer v = verts.get(0);
        int sizeBefore = verts.size();
        g.removeVertex(v);
        return g.getVertices().size() == sizeBefore - 1 && !g.getVertices().contains(v);
    }

    /**
     * Test that removing a non-existent vertex does nothing.
     * @param g A graph from the default generator.
     * @param v A vertex (not) present in the graph.
     * @return true if the graph remains unchanged.
     */
    @Property
    public boolean removeNonExistentVertexDoesNothing(@ForAll("graph") Graph<Integer> g, @ForAll Integer v) {
        Assume.that(!g.getVertices().contains(v));
        List<Integer> before = new ArrayList<>(g.getVertices());
        g.removeVertex(v);
        List<Integer> after = g.getVertices();
        return before.size() == after.size() && new HashSet<>(before).equals(new HashSet<>(after));
    }

    /**
     * Test that removing a vertex also removes all its incident edges.
     * @param g A non-empty graph with at least 2 vertices.
     * @return true if no vertex remains adjacent to the removed vertex.
     */
    @Property
    public boolean removingVertexRemovesEdges(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(verts.size() >= 2);
        Integer v = verts.get(0);
        g.removeVertex(v);
        for (Integer other : g.getVertices()) {
            if (g.adjacent(other, v)) return false;
        }
        return true;
    }

    /**
     * Test that getVertices returns each vertex exactly once.
     * @param g A graph from the default generator.
     * @return true if there are no duplicate vertices.
     */
    @Property
    public boolean getVerticesReturnsUniqueVertices(@ForAll("graph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        return verts.size() == new HashSet<>(verts).size();
    }

    // -------------------- Edge (Toggle) Properties --------------------

    /**
     * Test that toggling an edge between two distinct vertices changes their adjacency.
     * @param g A non-empty graph with at least 2 vertices.
     * @return true if the adjacency state is toggled.
     */
    @Property
    public boolean toggleEdgeChangesAdjacency(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(verts.size() >= 2);
        Integer v1 = verts.get(0), v2 = verts.get(1);
        Assume.that(!v1.equals(v2));
        boolean before = g.adjacent(v1, v2);
        g.toggleEdge(v1, v2);
        return g.adjacent(v1, v2) != before;
    }

    /**
     * Test that toggling an edge is symmetric.
     * @param g A non-empty graph with at least 2 vertices.
     * @return true if g.adjacent(v1, v2) equals g.adjacent(v2, v1).
     */
    @Property
    public boolean toggleEdgeIsSymmetric(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(verts.size() >= 2);
        Integer v1 = verts.get(0), v2 = verts.get(1);
        Assume.that(!v1.equals(v2));
        g.toggleEdge(v1, v2);
        return g.adjacent(v1, v2) == g.adjacent(v2, v1);
    }

    /**
     * Test that toggling an edge twice restores the original state.
     * @param g A non-empty graph with at least 2 vertices.
     * @return true if the adjacency state is restored after two toggles.
     */
    @Property
    public boolean toggleEdgeTwiceRestoresState(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(verts.size() >= 2);
        Integer v1 = verts.get(0), v2 = verts.get(1);
        Assume.that(!v1.equals(v2));
        boolean original = g.adjacent(v1, v2);
        g.toggleEdge(v1, v2);
        g.toggleEdge(v1, v2);
        return g.adjacent(v1, v2) == original;
    }

    /**
     * Test that self-loops are not allowed.
     * @param g A non-empty graph.
     * @return true if toggling a self-loop does not create an edge.
     */
    @Property
    public boolean toggleEdgeSelfLoopNotAllowed(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(!verts.isEmpty());
        Integer v = verts.get(0);
        g.toggleEdge(v, v);
        return !g.adjacent(v, v);
    }

    /**
     * Test that toggling an edge with a non-existent vertex does nothing.
     * @param g A non-empty graph.
     * @param nonVertex A vertex not present in the graph.
     * @return true if the graph remains unchanged regarding that edge.
     */
    @Property
    public boolean toggleEdgeWithNonExistentVertexDoesNothing(@ForAll("nonEmptyGraph") Graph<Integer> g,
                                                              @ForAll Integer nonVertex) {
        List<Integer> verts = g.getVertices();
        Assume.that(!verts.isEmpty());
        Assume.that(!verts.contains(nonVertex));
        Integer existing = verts.get(0);
        boolean before = g.adjacent(existing, nonVertex);
        g.toggleEdge(existing, nonVertex);
        return before == g.adjacent(existing, nonVertex) && !g.adjacent(existing, nonVertex);
    }

    /**
     * Test that adjacent returns false for non-existent vertices.
     * @param g A graph from the default generator.
     * @param nonVertex A vertex not present in the graph.
     * @return true if adjacent returns false.
     */
    @Property
    public boolean adjacentFalseForNonExistentVertices(@ForAll("graph") Graph<Integer> g, @ForAll Integer nonVertex) {
        Assume.that(!g.getVertices().contains(nonVertex));
        return !g.adjacent(nonVertex, nonVertex);
    }

    // -------------------- Neighbours Properties --------------------

    /**
     * Test that neighbours returns exactly the adjacent vertices.
     * @param g A non-empty graph.
     * @return true if each neighbour is adjacent and vice versa.
     */
    @Property
    public boolean neighboursReturnsAdjacentVertices(@ForAll("nonEmptyGraph") Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        Assume.that(!verts.isEmpty());
        Integer v = verts.get(0);
        List<Integer> nbrs = g.neighbours(v);
        for (Integer n : nbrs) {
            if (!g.adjacent(v, n)) return false;
        }
        for (Integer u : verts) {
            if (g.adjacent(v, u) && !nbrs.contains(u)) return false;
        }
        return true;
    }

    /**
     * Test that neighbours returns an empty list for a non-existent vertex.
     * @param g A graph from the default generator.
     * @param nonVertex A vertex not present in the graph.
     * @return true if the neighbours list is empty.
     */
    @Property
    public boolean neighboursEmptyForNonExistentVertex(@ForAll("graph") Graph<Integer> g, @ForAll Integer nonVertex) {
        Assume.that(!g.getVertices().contains(nonVertex));
        return g.neighbours(nonVertex).isEmpty();
    }

    // -------------------- Complex Operations Property --------------------

    /**
     * Performs a sequence of random operations and checks graph consistency.
     * This property tests that after a series of operations, graph invariants hold.
     * @param g A graph from the default generator.
     * @return true if the graph remains consistent.
     */
    @Property
    public boolean graphMaintainsConsistencyAfterOperations(@ForAll("graph") Graph<Integer> g) {
        Random rng = new Random();
        for (int i = 0; i < 10; i++) {
            int op = rng.nextInt(4);
            switch (op) {
                case 0: // add a random vertex
                    g.addVertex(rng.nextInt(100));
                    break;
                case 1: // remove a random vertex
                    if (!g.getVertices().isEmpty())
                        g.removeVertex(g.getVertices().get(rng.nextInt(g.getVertices().size())));
                    break;
                case 2: // toggle edge between two vertices (if possible)
                    if (g.getVertices().size() >= 2) {
                        int idx1 = rng.nextInt(g.getVertices().size());
                        int idx2 = rng.nextInt(g.getVertices().size());
                        if (idx1 != idx2) {
                            Integer v1 = g.getVertices().get(idx1);
                            Integer v2 = g.getVertices().get(idx2);
                            g.toggleEdge(v1, v2);
                        }
                    }
                    break;
                case 3: // toggle edge with a non-existent vertex
                    if (!g.getVertices().isEmpty()) {
                        Integer v = g.getVertices().get(rng.nextInt(g.getVertices().size()));
                        int nonVertex = 101 + rng.nextInt(100);
                        g.toggleEdge(v, nonVertex);
                    }
                    break;
            }
        }
        return isGraphConsistent(g);
    }

    // -------------------- Auxilliary Method --------------------

    /**
     * Checks that the graph is internally consistent.
     * Invariants include unique vertices, symmetric edges, and matching neighbours.
     * @param g The graph to check.
     * @return true if the graph passes all consistency checks.
     */
    private boolean isGraphConsistent(Graph<Integer> g) {
        List<Integer> verts = g.getVertices();
        if (verts.size() != new HashSet<>(verts).size()) return false;
        for (Integer v : verts) {
            List<Integer> nbrs = g.neighbours(v);
            for (Integer u : nbrs) {
                if (!g.adjacent(v, u)) return false;
            }
            for (Integer u : verts) {
                if (g.adjacent(v, u) && !nbrs.contains(u)) return false;
                if (g.adjacent(v, u) != g.adjacent(u, v)) return false;
            }
        }
        return true;
    }

    // -------------------- Generators --------------------

    /**
     * Provides an empty graph for testing.
     */
    @Provide
    public Arbitrary<Graph<Integer>> emptyGraph() {
        return Arbitraries.just(new Graph<Integer>());
    }

    /**
     * Provides a non-empty graph for testing.
     * Ensures the graph has at least one vertex.
     */
    @Provide
    public Arbitrary<Graph<Integer>> nonEmptyGraph() {
        @SuppressWarnings("rawtypes")
        Arbitrary<List> arbList = Arbitraries.defaultFor(List.class, Integer.class);
        Arbitrary<Integer> arbInt = Arbitraries.integers();
        return Combinators.combine(arbList, arbInt)
            .as((xs, seed) -> {
                xs = xs.subList(0, Math.min(50, xs.size()));
                if (xs.isEmpty()) {
                    xs.add(42); // Ensure at least one vertex
                }
                Graph<Integer> g = new Graph<Integer>();
                for (Object x : xs)
                    g.addVertex((Integer) x);
                Random rng = new Random(seed);
                for (Integer v : g.getVertices())
                    for (Integer w : g.getVertices())
                        if (rng.nextBoolean() && !v.equals(w))
                            g.toggleEdge(v, w);
                return g;
            });
    }

    /**
     * Generator for a general graph.
     * @return An arbitrary Graph<Integer> with randomly toggled edges.
     */
    /**
     * Provides a general graph for testing.
     */
    @Provide
    public Arbitrary<Graph<Integer>> graph() {
        @SuppressWarnings("rawtypes")
        Arbitrary<List> arbList = Arbitraries.defaultFor(List.class, Integer.class);
        Arbitrary<Integer> arbInt = Arbitraries.integers();
        return Combinators.combine(arbList, arbInt)
            .as((xs, seed) -> {
                xs = xs.subList(0, Math.min(50, xs.size()));
                Graph<Integer> g = new Graph<Integer>();
                for (Object x : xs)
                    g.addVertex((Integer) x);
                Random rng = new Random(seed);
                for (Integer v : g.getVertices())
                    for (Integer w : g.getVertices())
                        if (rng.nextBoolean())
                            g.toggleEdge(v, w);
                return g;
            });
    }
}
