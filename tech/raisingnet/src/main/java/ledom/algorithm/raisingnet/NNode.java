package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NNode<TAttach> extends NGraphElem<TAttach> {

	NGraph<TAttach> graph;

	private List<NLink<TAttach>> forwards = new ArrayList<NLink<TAttach>>();
	private List<NLink<TAttach>> backwards = new ArrayList<NLink<TAttach>>();

	public NNode(NGraph<TAttach> nGraph) {
		this(nGraph, 0, null);
	}

	public NNode(NGraph<TAttach> nGraph, TAttach attach) {
		this(nGraph, 0, attach);
	}

	public NNode(NGraph<TAttach> nGraph, int weight) {
		this(nGraph, weight, null);
	}
	
	public NNode(NGraph<TAttach> nGraph, int weight, TAttach attach) {
		graph = nGraph;
		setAttachment(attach);
		setWeight(weight);
	}
	
	public List<NLink<TAttach>> getForwardLinks() {
		return Collections.unmodifiableList(forwards);
	}

	public List<NLink<TAttach>> getBackwardLinks() {
		return Collections.unmodifiableList(backwards);
	}

	public void addForwardLink(NLink<TAttach> link) {
		this.forwards.add(link);
	}

	public void addBackwardLink(NLink<TAttach> link) {
		this.backwards.add(link);
	}

	public NGraph<TAttach> getGraph() {
		return this.graph;
	}

}
