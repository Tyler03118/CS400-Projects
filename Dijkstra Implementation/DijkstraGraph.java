// --== CS400 File Header Information ==--
// Name: Ziji Li
// Email: zli2296@wisc.edu
// Group and Team: N/A
// Group TA: N/A
// Lecturer: Peyman Morteza
// Notes to Grader: <optional extra notes>

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes.  This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
    extends BaseGraph<NodeType,EdgeType>
    implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph.  The final node in this path is stored in it's node
     * field.  The total cost of this path is stored in its cost field.  And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in it's node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;
        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }
        public int compareTo(SearchNode other) {
            if( cost > other.cost ) return +1;
            if( cost < other.cost ) return -1;
            return 0;
        }
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations.  The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *         or when either start or end data do not correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // Check if start and end nodes exist
        if (!nodes.containsKey(start) || !nodes.containsKey(end)) {
            throw new NoSuchElementException("Start or end node not found in the graph.");
        }

        PriorityQueue<SearchNode> queue = new PriorityQueue<>();
        Hashtable<NodeType, SearchNode> visited = new Hashtable<>();

        // Add the start node to the queue with 0 cost
        Node startNode = nodes.get(start);
        SearchNode startSearchNode = new SearchNode(startNode, 0, null);
        queue.add(startSearchNode);
        visited.put(start, startSearchNode);

        // Start BFS
        while (!queue.isEmpty()) {
            SearchNode current = queue.poll();
            Node currentNode = current.node;

            // If the current node is the end node, return the current SearchNode
            if (currentNode.data.equals(end)) {
                return current;
            }

            // Otherwise, update the cost and predecessor for each neighbor of the current node
            for (Edge edge : currentNode.edgesLeaving) {
                NodeType neighborData = edge.successor.data;
                double newCost = current.cost + edge.data.doubleValue();

                if (!visited.containsKey(neighborData) || visited.get(neighborData).cost > newCost) {
                    SearchNode neighborSearchNode = new SearchNode(edge.successor, newCost, current);
                    queue.add(neighborSearchNode);
                    visited.put(neighborData, neighborSearchNode);
                }
            }
        }

        // If we have visited all nodes and haven't found the end node
        throw new NoSuchElementException("No path found from start to end.");
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value.  This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path.  This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        SearchNode endSearchNode = computeShortestPath(start, end);
        LinkedList<NodeType> path = new LinkedList<>();

        // Trace back the path from the end node to the start node
        SearchNode current = endSearchNode;
        while (current != null) {
            path.addFirst(current.node.data);  // Add to the front of the list
            current = current.predecessor;
        }

        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data.  This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        SearchNode endSearchNode = computeShortestPath(start, end);
        return endSearchNode.cost;
    }


    private DijkstraGraph<String, Double> graph;

    @BeforeEach
    public void setUp() {
        // Create a new graph for each test
        graph = new DijkstraGraph<>();

        // Add nodes and edges to the graph
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        graph.insertEdge("A", "B", 1.0);
        graph.insertEdge("B", "C", 2.0);
        graph.insertEdge("C", "D", 1.0);
        graph.insertEdge("A", "E", 3.0);
    }

    @Test
    public void testShortestPathKnownGraph() {
        // Check if the shortest path from A to D is A-B-C-D, and the total cost is 4.0
        assertEquals(Arrays.asList("A", "B", "C", "D"), graph.shortestPathData("A", "D"));
        assertEquals(4.0, graph.shortestPathCost("A", "D"));
    }

    @Test
    public void testShortestPathDifferentNodes() {
        // Check if the shortest path from A to E is A-E, and the total cost is 3.0
        assertEquals(Arrays.asList("A", "E"), graph.shortestPathData("A", "E"));
        assertEquals(3.0, graph.shortestPathCost("A", "E"));
    }

    @Test
    public void testUnreachableNode() {
        // Confirm that the graph throws a NoSuchElementException when no path exists
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathData("E", "D"));
        assertThrows(NoSuchElementException.class, () -> graph.shortestPathCost("E", "D"));
    }

    @Test
    public void testMultipleShortestPaths() {
        // Add node F and edges from D and E to F
        graph.insertNode("F");
        graph.insertEdge("D", "F", 2.0);
        graph.insertEdge("E", "F", 2.0);

        List<String> pathDF = graph.shortestPathData("D", "F");
        List<String> pathEF = graph.shortestPathData("E", "F");

        // Check if either D-F or E-F is chosen as the shortest path (since both have the same cost)
        boolean correctPathDF = pathDF.equals(Arrays.asList("D", "F"));
        boolean correctPathEF = pathEF.equals(Arrays.asList("E", "F"));

        assertTrue(correctPathDF || correctPathEF, "Expected D-F or E-F but got " + pathDF);

        // Check if the cost of both paths is correctly computed as 2.0
        double costDF = graph.shortestPathCost("D", "F");
        double costEF = graph.shortestPathCost("E", "F");

        assertEquals(2.0, costDF, "Expected cost to be 2.0 for D-F but got " + costDF);
        assertEquals(2.0, costEF, "Expected cost to be 2.0 for E-F but got " + costEF);
    }
}
