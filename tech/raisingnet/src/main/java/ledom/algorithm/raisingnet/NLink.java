package ledom.algorithm.raisingnet;

public class NLink<TAttach> extends NGraphElem<TAttach> {

	private NNode<TAttach> source;
	private NNode<TAttach> destination;
	private Direction direction;
	public NLink(NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance, TAttach attach) {
		this.source = from;
		this.destination = to;
		this.direction = dir;
		setWeight(distance);
		setAttachment(attach);
	}

	public NLink(NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance) {
		this(from, to, dir, distance, null);
	}

	public NLink(NNode<TAttach> from,
			NNode<TAttach> to, Direction dir) {
		this(from, to, dir, 0, null);
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
