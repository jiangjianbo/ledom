package ledom.algorithm.raisingnet;

public class NGraphElem<TAttach> {

	private NGraph<TAttach> graph;
	TAttach attachment = null;
	private int id = -1, weight = 0, flag = 0;

	public NGraphElem(NGraph<TAttach> graph, int id) {
		this.graph = graph;
		this.id = id;
	}

	public NGraph<TAttach> getGraph() {
		return graph;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public<T> T getFlag(String flagType) {
		return graph.getFlag(flagType, id);
	}

	public<T> void setFlag(String flagType, T flag) {
		graph.setFlag(flagType, id, flag);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public TAttach getAttachment() {
		return attachment;
	}

	public void setAttachment(TAttach attachment) {
		this.attachment = attachment;
	}

	public int getId() {
		return id;
	}

}
