package ledom.algorithm.raisingnet;

public class GraphTestUtil {


	protected static NGraph<Long, Long> genSimpleGraph() {
		NGraph<Long, Long> graph = new NGraph<Long, Long>();
	
		NNode<Long, Long> node1 = (NNode<Long, Long>) graph.createNode(1L);
		NNode<Long, Long> node2 = (NNode<Long, Long>) graph.createNode(2L);
		NNode<Long, Long> node3 = (NNode<Long, Long>) graph.createNode(3L);
		NNode<Long, Long> node4 = (NNode<Long, Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node3, node4, Direction.FORWARD, 30);
	
		graph.setEntrance(node1);
	
		return graph;
	}

	protected static NGraph<Long, Long> genCircleGraph() {
		NGraph<Long, Long> graph = new NGraph<Long, Long>();
	
		NNode<Long, Long> node1 = (NNode<Long, Long>) graph.createNode(1L);
		NNode<Long, Long> node2 = (NNode<Long, Long>) graph.createNode(2L);
		NNode<Long, Long> node3 = (NNode<Long, Long>) graph.createNode(3L);
		NNode<Long, Long> node4 = (NNode<Long, Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node3, node4, Direction.FORWARD, 30);
		graph.createLink(node4, node1, Direction.FORWARD, 40);
	
		graph.setEntrance(node1);
	
		return graph;
	}

	protected static NGraph<Long, Long> gen_h_Graph() {
		NGraph<Long, Long> graph = new NGraph<Long, Long>();
	
		NNode<Long, Long> node1 = (NNode<Long, Long>) graph.createNode(1L);
		NNode<Long, Long> node2 = (NNode<Long, Long>) graph.createNode(2L);
		NNode<Long, Long> node3 = (NNode<Long, Long>) graph.createNode(3L);
		NNode<Long, Long> node4 = (NNode<Long, Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node2, node4, Direction.FORWARD, 30);
	
		graph.setEntrance(node1);
	
		return graph;
	}

}