package ledom.algorithm.raisingnet;

public class NLink<TNodeAttach, TLinkAttach> extends NGraphElem<TNodeAttach, TLinkAttach> {

	private NNode<TNodeAttach, TLinkAttach> source;
	private NNode<TNodeAttach, TLinkAttach> destination;
	private Direction direction;
	private TLinkAttach attachment = null;
	private int distance;
	private int flag = 0;
	private int linkId = -1;
	public NLink(NNode<TNodeAttach, TLinkAttach> from,
			NNode<TNodeAttach, TLinkAttach> to, Direction dir,
			int distance, TLinkAttach attach) {
		this.source = from;
		this.destination = to;
		this.direction = dir;
		this.distance = distance;
		this.attachment = attach;
	}

	public NLink(NNode<TNodeAttach, TLinkAttach> from,
			NNode<TNodeAttach, TLinkAttach> to, Direction dir,
			int distance) {
		this(from, to, dir, distance, null);
	}

	public NLink(NNode<TNodeAttach, TLinkAttach> from,
			NNode<TNodeAttach, TLinkAttach> to, Direction dir) {
		this(from, to, dir, 0, null);
	}
	
	public int getLinkId(){
		return linkId;
	}
	
	void setLinkId(int linkId){
		this.linkId = linkId;
	}
	
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public NNode<TNodeAttach, TLinkAttach> getSource() {
		return this.source;
	}

	public NNode<TNodeAttach, TLinkAttach> getDestination() {
		return this.destination;
	}

	public Direction getDirection(){
		return direction;
	}
	
	public TLinkAttach getAttachment() {
		return attachment;
	}

	public TLinkAttach setAttachment(TLinkAttach newValue) {
		TLinkAttach old = this.attachment;
		this.attachment = newValue;
		return old;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
