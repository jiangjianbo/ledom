package ledom.algorithm.raisingnet;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NGraphTest extends GraphTestUtil {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimpleGraph() {
		NGraph<Long, Long> graph = genSimpleGraph();

		testSimpleGraph_common(graph);
		graph = (NGraph<Long, Long>) graph.clone();
		testSimpleGraph_common(graph);
	
	}

	private void testSimpleGraph_common(NGraph<Long, Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		Iterator<NNode<Long, Long>> it = graph.getAllNodes().iterator();
		for (int i = 1; i <= 4 && it.hasNext(); i++) {
			NNode<Long, Long> nn = it.next();
			assertEquals(i, nn.getAttachment().longValue());
			assertSame(graph, nn.getGraph());
		}

		Collection<NNode<Long, Long>> entries = graph.getEntries();

		NNode<Long, Long> next, first = entries.iterator().next();

		assertEquals(first.getAttachment().longValue(), 1);
		assertEquals(1, first.getForwardLinks().size());

		next = first.getForwardLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 2);
		assertEquals(1, next.getForwardLinks().size());
		assertEquals(1, next.getBackwardLinks().size());
		assertSame(first.getForwardLinks().get(0), next.getBackwardLinks().get(0));

		first = next;
		next = first.getForwardLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 3);
		assertEquals(1, next.getForwardLinks().size());
		assertEquals(1, next.getBackwardLinks().size());
		assertSame(first.getForwardLinks().get(0), next.getBackwardLinks().get(0));

		first = next;
		next = first.getForwardLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 4);
		assertEquals(0, next.getForwardLinks().size());
		assertEquals(1, next.getBackwardLinks().size());
		assertSame(first.getForwardLinks().get(0), next.getBackwardLinks().get(0));
	}

	
	@Test
	public void testCircleGraph() {
		NGraph<Long, Long> graph = genCircleGraph();

		testCircleGraph_common(graph);
		graph = (NGraph<Long, Long>) graph.clone();
		testCircleGraph_common(graph);
	}

	private void testCircleGraph_common(NGraph<Long, Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		List<NNode<Long, Long>> entries = graph.getEntries();

		NNode<Long, Long> prev, first = entries.get(0);

		assertEquals(1, first.getBackwardLinks().size());

		prev = first.getBackwardLinks().get(0).getSource();
		assertEquals(prev.getAttachment().longValue(), 4);
		assertEquals(1, prev.getForwardLinks().size());
		assertEquals(1, prev.getBackwardLinks().size());
		assertSame(prev.getForwardLinks().get(0), first
				.getBackwardLinks().get(0));
	}
	
	

	@Test
	public void test_h_Graph() {
		NGraph<Long, Long> graph = gen_h_Graph();

		test_h_Graph_common(graph);
		
		graph = (NGraph<Long, Long>) graph.clone();
		test_h_Graph_common(graph);
	}

	private void test_h_Graph_common(NGraph<Long, Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		List<NNode<Long, Long>> entries = graph.getEntries();

		assertEquals(0, graph.getNode(0).getBackwardLinks().size());
		assertEquals(1, graph.getNode(1).getBackwardLinks().size());
		assertEquals(1, graph.getNode(2).getBackwardLinks().size());
		assertEquals(1, graph.getNode(3).getBackwardLinks().size());

		assertEquals(1, graph.getNode(0).getForwardLinks().size());
		assertEquals(2, graph.getNode(1).getForwardLinks().size());
		assertEquals(0, graph.getNode(2).getForwardLinks().size());
		assertEquals(0, graph.getNode(3).getForwardLinks().size());

		assertSame(graph.getNode(0).getForwardLinks().get(0), graph.getNode(1).getBackwardLinks().get(0));
		assertSame(graph.getNode(1).getForwardLinks().get(0), graph.getNode(2).getBackwardLinks().get(0));
		assertSame(graph.getNode(1).getForwardLinks().get(1), graph.getNode(3).getBackwardLinks().get(0));
	}
		
	
}
