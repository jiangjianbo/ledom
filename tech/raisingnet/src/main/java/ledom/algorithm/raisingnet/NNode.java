package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NNode<TNodeAttach, TLinkAttach> extends NGraphElem<TNodeAttach, TLinkAttach> {

	NGraph<TNodeAttach, TLinkAttach> graph;
	TNodeAttach attachment;
	int nodeId = -1, flag = 0;
	int weight = 0;


	private List<NLink<TNodeAttach, TLinkAttach>> forwards = new ArrayList<NLink<TNodeAttach, TLinkAttach>>();
	private List<NLink<TNodeAttach, TLinkAttach>> backwards = new ArrayList<NLink<TNodeAttach, TLinkAttach>>();

	public NNode(NGraph<TNodeAttach, TLinkAttach> nGraph) {
		this(nGraph, 0, null);
	}

	public NNode(NGraph<TNodeAttach, TLinkAttach> nGraph, TNodeAttach attach) {
		this(nGraph, 0, attach);
	}

	public NNode(NGraph<TNodeAttach, TLinkAttach> nGraph, int weight) {
		this(nGraph, weight, null);
	}
	
	public NNode(NGraph<TNodeAttach, TLinkAttach> nGraph, int weight, TNodeAttach attach) {
		graph = nGraph;
		attachment = attach;
		this.weight = weight;
	}
	
	/**
	 * 返回快速引用索引
	 * @return
	 */
	public int getNodeId(){
		return nodeId;
	}
	
	void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}
	
	public List<NLink<TNodeAttach, TLinkAttach>> getForwardLinks() {
		return Collections.unmodifiableList(forwards);
	}

	public List<NLink<TNodeAttach, TLinkAttach>> getBackwardLinks() {
		return Collections.unmodifiableList(backwards);
	}

	public void addForwardLink(NLink<TNodeAttach, TLinkAttach> link) {
		this.forwards.add(link);
	}

	public void addBackwardLink(NLink<TNodeAttach, TLinkAttach> link) {
		this.backwards.add(link);
	}

	public TNodeAttach getAttachment() {
		return attachment;
	}

	public TNodeAttach setAttachment(TNodeAttach newValue) {
		TNodeAttach old = attachment;
		attachment = newValue;
		return old;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public NGraph<TNodeAttach, TLinkAttach> getGraph() {
		return this.graph;
	}

}
