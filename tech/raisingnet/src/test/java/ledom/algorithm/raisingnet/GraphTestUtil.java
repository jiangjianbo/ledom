package ledom.algorithm.raisingnet;

public class GraphTestUtil {


	protected static NGraph<Long> genSimpleGraph() {
		NGraph<Long> graph = new NGraph<Long>();
	
		NNode<Long> node1 = (NNode<Long>) graph.createNode(1L);
		NNode<Long> node2 = (NNode<Long>) graph.createNode(2L);
		NNode<Long> node3 = (NNode<Long>) graph.createNode(3L);
		NNode<Long> node4 = (NNode<Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node3, node4, Direction.FORWARD, 30);
	
		graph.setEntrance(node1);
	
		return graph;
	}

	protected static NGraph<Long> genCircleGraph() {
		NGraph<Long> graph = new NGraph<Long>();
	
		NNode<Long> node1 = (NNode<Long>) graph.createNode(1L);
		NNode<Long> node2 = (NNode<Long>) graph.createNode(2L);
		NNode<Long> node3 = (NNode<Long>) graph.createNode(3L);
		NNode<Long> node4 = (NNode<Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node3, node4, Direction.FORWARD, 30);
		graph.createLink(node4, node1, Direction.FORWARD, 40);
	
		graph.setEntrance(node1);
	
		return graph;
	}

	protected static NGraph<Long> gen_h_Graph() {
		NGraph<Long> graph = new NGraph<Long>();
	
		NNode<Long> node1 = (NNode<Long>) graph.createNode(1L);
		NNode<Long> node2 = (NNode<Long>) graph.createNode(2L);
		NNode<Long> node3 = (NNode<Long>) graph.createNode(3L);
		NNode<Long> node4 = (NNode<Long>) graph.createNode(4L);
	
		graph.createLink(node1, node2, Direction.FORWARD, 10);
		graph.createLink(node2, node3, Direction.FORWARD, 20);
		graph.createLink(node2, node4, Direction.FORWARD, 30);
	
		graph.setEntrance(node1);
	
		return graph;
	}

}