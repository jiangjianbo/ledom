package ledom.algorithm.raisingnet;

public class NLink<TAttach> extends NGraphElem<TAttach> {

	private NNode<TAttach> source;
	private NNode<TAttach> destination;
	private Direction direction;
	
	NLink(NGraph<TAttach> graph, int id, NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance) {
		super(graph, id);
		
		this.source = from;
		this.destination = to;
		this.direction = dir;
		setWeight(distance);
	}

	NLink(NGraph<TAttach> graph, int id, NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance, TAttach attachment) {
		this(graph, id, from, to, dir, distance);
		
		setAttachment(attachment);
	}

	public NNode<TAttach> getSource() {
		return this.source;
	}

	public NNode<TAttach> getDestination() {
		return this.destination;
	}

	public Direction getDirection(){
		return direction;
	}
	
}
