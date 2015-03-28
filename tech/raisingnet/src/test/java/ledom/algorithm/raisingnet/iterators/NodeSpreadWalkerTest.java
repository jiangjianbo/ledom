package ledom.algorithm.raisingnet.iterators;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ledom.algorithm.raisingnet.GraphTestUtil;
import ledom.algorithm.raisingnet.NGraph;
import ledom.algorithm.raisingnet.NGraphElem;
import ledom.algorithm.raisingnet.NLink;
import ledom.algorithm.raisingnet.NNode;
import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker.WalkOptions;

public class NodeSpreadWalkerTest extends GraphTestUtil {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleAll() {
		NGraph<Long> graph = genSimpleGraph();
		
		NodeSpreadWalker<Long> walk;
		
		walk = new NodeSpreadWalker<Long>(graph, WalkOptions.ALL);
		Iterator<NGraphElem<Long>> it = walk.iterator();
		Iterator<NNode<Long>> nodeIt = graph.getAllNodes().iterator();
		Iterator<NLink<Long>> linkIt = graph.getAllLinks().iterator();
		NNode<Long> node;
		NLink<Long> link, linkprev;
		
		assertEquals(true, it.hasNext());
		assertSame(node= nodeIt.next(),it.next()); // node1
		assertEquals(1L, node.getAttachment().longValue());
		assertSame(link = linkIt.next(), it.next()); // link1
		assertEquals(10L, link.getWeight());
		
		assertSame(node= nodeIt.next(),it.next()); // node2
		assertEquals(2L, node.getAttachment().longValue());
		linkprev = link;
		assertSame(link = linkIt.next(), it.next()); // link2
		assertEquals(20L, link.getWeight());
		assertSame(linkprev, it.next()); // link1
		
		assertSame(node= nodeIt.next(),it.next()); // node3
		assertEquals(3L, node.getAttachment().longValue());
		linkprev = link;
		assertSame(link = linkIt.next(), it.next()); // link3
		assertEquals(30L, link.getWeight());
		assertSame(linkprev, it.next()); // link2
		
		assertSame(node= nodeIt.next(),it.next()); // node4
		assertEquals(4L, node.getAttachment().longValue());		
		linkprev = link;
		assertSame(linkprev, it.next()); // link3
		
		assertEquals(false, it.hasNext());
	}

	@Test
	public void testSimpleNode() {
		NGraph<Long> graph = genSimpleGraph();
		
		NodeSpreadWalker<Long> walk;
		
		walk = new NodeSpreadWalker<Long>(graph, WalkOptions.NODE);
		Iterator<NGraphElem<Long>> it = walk.iterator();
		Iterator<NNode<Long>> nodeIt = graph.getAllNodes().iterator();
		NNode<Long> node;
		
		assertEquals(true, it.hasNext());
		assertSame(node= nodeIt.next(),it.next()); // node1
		assertEquals(1L, node.getAttachment().longValue());
		
		assertSame(node= nodeIt.next(),it.next()); // node2
		assertEquals(2L, node.getAttachment().longValue());
		
		assertSame(node= nodeIt.next(),it.next()); // node3
		assertEquals(3L, node.getAttachment().longValue());
		
		assertSame(node= nodeIt.next(),it.next()); // node4
		assertEquals(4L, node.getAttachment().longValue());		
		
		assertEquals(false, it.hasNext());
	}

	@Test
	public void testSimpleForward() {
		NGraph<Long> graph = genSimpleGraph();
		
		NodeSpreadWalker<Long> walk;
		
		walk = new NodeSpreadWalker<Long>(graph, WalkOptions.FORWARD_LINK);
		Iterator<NGraphElem<Long>> it = walk.iterator();
		Iterator<NLink<Long>> linkIt = graph.getAllLinks().iterator();
		NLink<Long> link;
		
		assertEquals(true, it.hasNext());
		assertSame(link = linkIt.next(), it.next()); // link1
		assertEquals(10L, link.getWeight());
		
		assertSame(link = linkIt.next(), it.next()); // link2
		assertEquals(20L, link.getWeight());
		
		assertSame(link = linkIt.next(), it.next()); // link3
		assertEquals(30L, link.getWeight());
		
		assertEquals(false, it.hasNext());
	}

	@Test
	public void testSimpleBackward() {
		NGraph<Long> graph = genSimpleGraph();
		
		NodeSpreadWalker<Long> walk;
		
		walk = new NodeSpreadWalker<Long>(graph, WalkOptions.BACKWARD_LINK);
		Iterator<NGraphElem<Long>> it = walk.iterator();
		Iterator<NLink<Long>> linkIt = graph.getAllLinks().iterator();
		NLink<Long> link;
		
		assertEquals(true, it.hasNext());
		assertSame(link = linkIt.next(), it.next()); // link1
		assertEquals(10L, link.getWeight());
		
		assertSame(link = linkIt.next(), it.next()); // link2
		assertEquals(20L, link.getWeight());
		
		assertSame(link = linkIt.next(), it.next()); // link3
		assertEquals(30L, link.getWeight());
		
		assertEquals(false, it.hasNext());
	}
	
	@Test
	public void testSimpleLink() {
		NGraph<Long> graph = genSimpleGraph();
		
		NodeSpreadWalker<Long> walk;
		
		walk = new NodeSpreadWalker<Long>(graph, WalkOptions.LINK);
		Iterator<NGraphElem<Long>> it = walk.iterator();
		Iterator<NLink<Long>> linkIt = graph.getAllLinks().iterator();
		NLink<Long> link, linkprev;
		
		assertSame(link = linkIt.next(), it.next()); // link1
		assertEquals(10L, link.getWeight());
		
		linkprev = link;
		assertSame(link = linkIt.next(), it.next()); // link2
		assertEquals(20L, link.getWeight());
		assertSame(linkprev, it.next()); // link1
		
		linkprev = link;
		assertSame(link = linkIt.next(), it.next()); // link3
		assertEquals(30L, link.getWeight());
		assertSame(linkprev, it.next()); // link2
		
		linkprev = link;
		assertSame(linkprev, it.next()); // link3
		
		assertEquals(false, it.hasNext());
	}


	
}
