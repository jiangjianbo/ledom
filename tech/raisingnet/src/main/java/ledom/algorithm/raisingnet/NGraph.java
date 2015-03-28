package ledom.algorithm.raisingnet;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NGraph<TAttach> {

	private final class WrapperList extends
			AbstractList<NNode<TAttach>> {
		private List<NNode<TAttach>> targetList;

		public WrapperList(List<NNode<TAttach>> targetList) {
			this.targetList = targetList;
		}

		@Override
		public void add(int index, NNode<TAttach> node) {
			if( node == null )
				throw new IllegalArgumentException("node is null");
			if(!nodes.containsValue(node) )
				throw new IllegalArgumentException("node not include in graph");				
			if(targetList.contains(node) )
				throw new IllegalArgumentException("duplicated node: " + ((NNode<TAttach>)node).getId());	
			targetList.add(index, node);
		}

		@Override
		public NNode<TAttach> set(int index,
				NNode<TAttach> node) {
			if( node == null )
				throw new IllegalArgumentException("node is null");
			if(!nodes.containsValue(node) )
				throw new IllegalArgumentException("node not include in graph");				
			if(targetList.contains(node) )
				throw new IllegalArgumentException("duplicated node: " + ((NNode<TAttach>)node).getId());	
			return targetList.set(index, node);
		}

		@Override
		public NNode<TAttach> remove(int index) {
			return targetList.remove(index);
		}

		@Override
		public Iterator<NNode<TAttach>> iterator() {
			return targetList.iterator();
		}

		@Override
		public int size() {
			return targetList.size();
		}

		@Override
		public NNode<TAttach> get(int index) {
			return targetList.get(index);
		}
	}

	int nodeCount = 0;
	int linkCount = 0;
	Map<Integer, NNode<TAttach>> nodes = new HashMap<Integer, NNode<TAttach>>();
	Map<Integer, NLink<TAttach>> links = new HashMap<Integer, NLink<TAttach>>();
	
	private List<NNode<TAttach>> entries = new ArrayList<NNode<TAttach>>();
	private List<NNode<TAttach>> exits = new ArrayList<NNode<TAttach>>();

	WrapperList entriesWrapper = new WrapperList(entries);
	WrapperList exitsWrapper = new WrapperList(exits);

	public Collection<NNode<TAttach>> getAllNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	public Collection<NLink<TAttach>> getAllLinks() {
		return Collections.unmodifiableCollection(links.values());
	}

	public NNode<TAttach> createNode(TAttach attach) {
		NNode<TAttach> node = new NNode<TAttach>(this, attach);
		
		int key = nodeCount ++;
		nodes.put(key, node);
		node.setId(key);
		
		return node;
	}

	public NLink<TAttach> createLink(
			NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance) {
		if( from == null )
			throw new IllegalArgumentException("from can not be null");
		if( to == null  )
			throw new IllegalArgumentException("to can not be null");
		if( from.getGraph() != this )
			throw new IllegalArgumentException("from node must contains in graph");
		if( to.getGraph() != this )
			throw new IllegalArgumentException("to node must contains in graph");
		
		int key = linkCount++;
		NLink<TAttach> link = new NLink<TAttach>(from, to, dir, distance);
		// 加入到 graph 的links登记中
		links.put(key, link);
		link.setId(key);
		// 增加到node的forward和backward中
		((NNode<TAttach>)from).addForwardLink(link);
		((NNode<TAttach>)to).addBackwardLink(link);
		
		return link;
	}

	public void removeNode(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");
		
		NNode<TAttach> node1 = (NNode<TAttach>)node;
		int idx = node1.getId();
		if( nodes.get(idx) != node1 )
			throw new IllegalStateException("incorrect node index");
		
		nodes.remove(idx);
	}

	public void removeLink(NLink<TAttach> link) {
		if( link == null )
			throw new IllegalArgumentException("link is null");
		if(!links.containsValue(link) )
			throw new IllegalArgumentException("link not include in graph");
		
		NLink<TAttach> link1 = (NLink<TAttach>)link;
		int idx = link1.getId();
		if( links.get(idx) != link1 )
			throw new IllegalStateException("incorrect link index");
		
		links.remove(idx);
	}

	public NGraph<TAttach> clone(){
		NGraph<TAttach> graph = new NGraph<TAttach>();
		// 创建 node
		for(NNode<TAttach> node : nodes.values()){
			NNode<TAttach> node1 = (NNode<TAttach>) node;
			NNode<TAttach> nc = new NNode<TAttach>(graph, node.getAttachment());
			
			int index = node1.getId();
			if( graph.nodes.containsKey(index))
				throw new IllegalStateException("duplicated node index");

			nc.setId(index);
			nc.setFlag(node.getFlag());
			
			graph.nodes.put(index, nc);
		}
		// 创建 link
		for(NLink<TAttach> link : links.values()){
			NLink<TAttach> link1 = (NLink<TAttach>) link;
			// 从 graph 中获取link两端对应的 node
			NNode<TAttach> gsource = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getSource())).getId());
			NNode<TAttach> gdest = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getDestination())).getId());

			NLink<TAttach> nlnk = new NLink<TAttach>(gsource, gdest, link.getDirection(), link.getWeight(), link.getAttachment());
			
			int index = link1.getId();
			if( graph.links.containsKey(index) )
				throw new IllegalStateException("duplicated link index");

			nlnk.setId(index);
			nlnk.setFlag(link.getFlag());
			graph.links.put(index, nlnk);
			
			// 这里不确保每一个 node 里的 forwards 和 backwords 里的顺序都是一样的
			gsource.addForwardLink(nlnk);
			gdest.addBackwardLink(nlnk);
		}		
		
		// 设置 entries 和 exits
		for(NNode<TAttach> node : entries){
			NNode<TAttach> node1 = (NNode<TAttach>) node;
			int id = node1.getId();
			NNode<TAttach> nc = (NNode<TAttach>) graph.getNode(id);
			graph.getEntries().add(nc);
		}

		for(NNode<TAttach> node : exits){
			NNode<TAttach> node1 = (NNode<TAttach>) node;
			int id = node1.getId();
			NNode<TAttach> nc = (NNode<TAttach>) graph.getNode(id);
			graph.getExits().add(nc);
		}		
		graph.linkCount = this.linkCount;
		graph.nodeCount = this.nodeCount;
		
		return graph;
	}

	public void setEntrance(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");

		entries.clear();
		entries .add(node);
	}


	public void setExit(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");

		exits.clear();
		exits.add(node);
	}

	public List<NNode<TAttach>> getEntries() {
		return entriesWrapper;
	}

	public List<NNode<TAttach>> getExits() {
		return exitsWrapper;
	}

	public NNode<TAttach> getNode(int id) {
		if( !nodes.containsKey(id))
			throw new IllegalArgumentException("node id:" + id + " not exist"); 
		return (NNode<TAttach>) nodes.get(id);
	}

	public boolean contains(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		return nodes.containsValue(node);
	}

	public void clearAllFlags(int initValue) {
		for (NNode<TAttach> graphNode : nodes.values()) {
			graphNode.setFlag(initValue);
		}
		for (NLink<TAttach> graphLink : links.values()) {
			graphLink.setFlag(initValue);
		}
	}

	public void clearAll(int initFlag, int initTemp) {
		for (NNode<TAttach> graphNode : nodes.values()) {
			graphNode.setFlag(initFlag);
			graphNode.setTemp(initTemp);
		}
		for (NLink<TAttach> graphLink : links.values()) {
			graphLink.setFlag(initFlag);
			graphLink.setTemp(initTemp);
		}
	}

}
