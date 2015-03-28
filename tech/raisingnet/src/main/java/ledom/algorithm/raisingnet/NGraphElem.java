package ledom.algorithm.raisingnet;

public class NGraphElem<TAttach> {

	TAttach attachment = null;
	private int id = -1, weight = 0, temp = 0, flag = 0;

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

	public TAttach getAttachment() {
		return attachment;
	}

	public void setAttachment(TAttach attachment) {
		this.attachment = attachment;
	}

	public int getId() {
		return id;
	}
	
	protected void setId(int id) {
		this.id = id;
	}

	public int getTemp() {
		return temp ;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

}
