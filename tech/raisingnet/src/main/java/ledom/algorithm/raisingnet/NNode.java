package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NNode<TAttach> extends NGraphElem<TAttach> {

	List<NLink<TAttach>> outputs = new ArrayList<NLink<TAttach>>();
	List<NLink<TAttach>> inputs = new ArrayList<NLink<TAttach>>();


	public NNode(NGraph<TAttach> nGraph, int id, TAttach attach) {
		super(nGraph, id);
		
		setAttachment(attach);
	}
	
	public List<NLink<TAttach>> getOutputLinks() {
		return Collections.unmodifiableList(outputs);
	}

	public List<NLink<TAttach>> getInputLinks() {
		return Collections.unmodifiableList(inputs);
	}

	public void addOutputLink(NLink<TAttach> link) {
		this.outputs.add(link);
	}

	public void addInputLink(NLink<TAttach> link) {
		this.inputs.add(link);
	}


}
