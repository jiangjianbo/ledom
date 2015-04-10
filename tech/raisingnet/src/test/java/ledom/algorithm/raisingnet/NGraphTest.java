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
		NGraph<Long> graph = genSimpleGraph();

		testSimpleGraph_common(graph);
		graph = (NGraph<Long>) graph.clone();
		testSimpleGraph_common(graph);
	
	}

	private void testSimpleGraph_common(NGraph<Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		Iterator<NNode<Long>> it = graph.getAllNodes().iterator();
		for (int i = 1; i <= 4 && it.hasNext(); i++) {
			NNode<Long> nn = it.next();
			assertEquals(i, nn.getAttachment().longValue());
			assertSame(graph, nn.getGraph());
		}

		Collection<NNode<Long>> entries = graph.getEntries();

		NNode<Long> next, first = entries.iterator().next();

		assertEquals(first.getAttachment().longValue(), 1);
		assertEquals(1, first.getOutputLinks().size());

		next = first.getOutputLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 2);
		assertEquals(1, next.getOutputLinks().size());
		assertEquals(1, next.getInputLinks().size());
		assertSame(first.getOutputLinks().get(0), next.getInputLinks().get(0));

		first = next;
		next = first.getOutputLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 3);
		assertEquals(1, next.getOutputLinks().size());
		assertEquals(1, next.getInputLinks().size());
		assertSame(first.getOutputLinks().get(0), next.getInputLinks().get(0));

		first = next;
		next = first.getOutputLinks().get(0).getDestination();
		assertEquals(next.getAttachment().longValue(), 4);
		assertEquals(0, next.getOutputLinks().size());
		assertEquals(1, next.getInputLinks().size());
		assertSame(first.getOutputLinks().get(0), next.getInputLinks().get(0));
	}

	
	@Test
	public void testCircleGraph() {
		NGraph<Long> graph = genCircleGraph();

		testCircleGraph_common(graph);
		graph = (NGraph<Long>) graph.clone();
		testCircleGraph_common(graph);
	}

	private void testCircleGraph_common(NGraph<Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		List<NNode<Long>> entries = graph.getEntries();

		NNode<Long> prev, first = entries.get(0);

		assertEquals(1, first.getInputLinks().size());

		prev = first.getInputLinks().get(0).getSource();
		assertEquals(prev.getAttachment().longValue(), 4);
		assertEquals(1, prev.getOutputLinks().size());
		assertEquals(1, prev.getInputLinks().size());
		assertSame(prev.getOutputLinks().get(0), first
				.getInputLinks().get(0));
	}
	
	

	@Test
	public void test_h_Graph() {
		NGraph<Long> graph = gen_h_Graph();

		test_h_Graph_common(graph);
		
		graph = (NGraph<Long>) graph.clone();
		test_h_Graph_common(graph);
	}

	private void test_h_Graph_common(NGraph<Long> graph) {
		assertEquals(4, graph.getAllNodes().size());

		List<NNode<Long>> entries = graph.getEntries();

		assertEquals(0, graph.getNode(0).getInputLinks().size());
		assertEquals(1, graph.getNode(1).getInputLinks().size());
		assertEquals(1, graph.getNode(2).getInputLinks().size());
		assertEquals(1, graph.getNode(3).getInputLinks().size());

		assertEquals(1, graph.getNode(0).getOutputLinks().size());
		assertEquals(2, graph.getNode(1).getOutputLinks().size());
		assertEquals(0, graph.getNode(2).getOutputLinks().size());
		assertEquals(0, graph.getNode(3).getOutputLinks().size());

		assertSame(graph.getNode(0).getOutputLinks().get(0), graph.getNode(1).getInputLinks().get(0));
		assertSame(graph.getNode(1).getOutputLinks().get(0), graph.getNode(2).getInputLinks().get(0));
		assertSame(graph.getNode(1).getOutputLinks().get(1), graph.getNode(3).getInputLinks().get(0));
	}
		
	
}
