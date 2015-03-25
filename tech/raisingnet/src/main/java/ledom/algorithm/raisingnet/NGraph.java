package ledom.algorithm.raisingnet;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NGraph<TNodeAttach, TLinkAttach> {

	private final class WrapperList extends
			AbstractList<NNode<TNodeAttach, TLinkAttach>> {
		private List<NNode<TNodeAttach, TLinkAttach>> targetList;

		public WrapperList(List<NNode<TNodeAttach, TLinkAttach>> targetList) {
			this.targetList = targetList;
		}

		@Override
		public void add(int index, NNode<TNodeAttach, TLinkAttach> node) {
			if( node == null )
				throw new IllegalArgumentException("node is null");
			if(!nodes.containsValue(node) )
				throw new IllegalArgumentException("node not include in graph");				
			if(targetList.contains(node) )
				throw new IllegalArgumentException("duplicated node: " + ((NNode<TNodeAttach, TLinkAttach>)node).getNodeId());	
			targetList.add(index, node);
		}

		@Override
		public NNode<TNodeAttach, TLinkAttach> set(int index,
				NNode<TNodeAttach, TLinkAttach> node) {
			if( node == null )
				throw new IllegalArgumentException("node is null");
			if(!nodes.containsValue(node) )
				throw new IllegalArgumentException("node not include in graph");				
			if(targetList.contains(node) )
				throw new IllegalArgumentException("duplicated node: " + ((NNode<TNodeAttach, TLinkAttach>)node).getNodeId());	
			return targetList.set(index, node);
		}

		@Override
		public NNode<TNodeAttach, TLinkAttach> remove(int index) {
			return targetList.remove(index);
		}

		@Override
		public Iterator<NNode<TNodeAttach, TLinkAttach>> iterator() {
			return targetList.iterator();
		}

		@Override
		public int size() {
			return targetList.size();
		}

		@Override
		public NNode<TNodeAttach, TLinkAttach> get(int index) {
			return targetList.get(index);
		}
	}

	int nodeCount = 0;
	int linkCount = 0;
	Map<Integer, NNode<TNodeAttach, TLinkAttach>> nodes = new HashMap<Integer, NNode<TNodeAttach, TLinkAttach>>();
	Map<Integer, NLink<TNodeAttach, TLinkAttach>> links = new HashMap<Integer, NLink<TNodeAttach, TLinkAttach>>();
	
	private List<NNode<TNodeAttach, TLinkAttach>> entries = new ArrayList<NNode<TNodeAttach, TLinkAttach>>();
	private List<NNode<TNodeAttach, TLinkAttach>> exits = new ArrayList<NNode<TNodeAttach, TLinkAttach>>();

	WrapperList entriesWrapper = new WrapperList(entries);
	WrapperList exitsWrapper = new WrapperList(exits);

	public Collection<NNode<TNodeAttach, TLinkAttach>> getAllNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	public Collection<NLink<TNodeAttach, TLinkAttach>> getAllLinks() {
		return Collections.unmodifiableCollection(links.values());
	}

	public NNode<TNodeAttach, TLinkAttach> createNode(TNodeAttach attach) {
		NNode<TNodeAttach, TLinkAttach> node = new NNode<TNodeAttach, TLinkAttach>(this, attach);
		
		int key = nodeCount ++;
		nodes.put(key, node);
		node.setNodeId(key);
		
		return node;
	}

	public NLink<TNodeAttach, TLinkAttach> createLink(
			NNode<TNodeAttach, TLinkAttach> from,
			NNode<TNodeAttach, TLinkAttach> to, Direction dir,
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
		NLink<TNodeAttach, TLinkAttach> link = new NLink<TNodeAttach, TLinkAttach>(from, to, dir, distance);
		// 加入到 graph 的links登记中
		links.put(key, link);
		link.setLinkId(key);
		// 增加到node的forward和backward中
		((NNode<TNodeAttach, TLinkAttach>)from).addForwardLink(link);
		((NNode<TNodeAttach, TLinkAttach>)to).addBackwardLink(link);
		
		return link;
	}

	public void removeNode(NNode<TNodeAttach, TLinkAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");
		
		NNode<TNodeAttach, TLinkAttach> node1 = (NNode<TNodeAttach, TLinkAttach>)node;
		int idx = node1.getNodeId();
		if( nodes.get(idx) != node1 )
			throw new IllegalStateException("incorrect node index");
		
		nodes.remove(idx);
	}

	public void removeLink(NLink<TNodeAttach, TLinkAttach> link) {
		if( link == null )
			throw new IllegalArgumentException("link is null");
		if(!links.containsValue(link) )
			throw new IllegalArgumentException("link not include in graph");
		
		NLink<TNodeAttach, TLinkAttach> link1 = (NLink<TNodeAttach, TLinkAttach>)link;
		int idx = link1.getLinkId();
		if( links.get(idx) != link1 )
			throw new IllegalStateException("incorrect link index");
		
		links.remove(idx);
	}

	public NGraph<TNodeAttach, TLinkAttach> clone(){
		NGraph<TNodeAttach, TLinkAttach> graph = new NGraph<TNodeAttach, TLinkAttach>();
		// 创建 node
		for(NNode<TNodeAttach, TLinkAttach> node : nodes.values()){
			NNode<TNodeAttach, TLinkAttach> node1 = (NNode<TNodeAttach, TLinkAttach>) node;
			NNode<TNodeAttach, TLinkAttach> nc = new NNode<TNodeAttach, TLinkAttach>(graph, node.getAttachment());
			
			int index = node1.getNodeId();
			if( graph.nodes.containsKey(index))
				throw new IllegalStateException("duplicated node index");

			nc.setNodeId(index);
			nc.setFlag(node.getFlag());
			
			graph.nodes.put(index, nc);
		}
		// 创建 link
		for(NLink<TNodeAttach, TLinkAttach> link : links.values()){
			NLink<TNodeAttach, TLinkAttach> link1 = (NLink<TNodeAttach, TLinkAttach>) link;
			// 从 graph 中获取link两端对应的 node
			NNode<TNodeAttach, TLinkAttach> gsource = (NNode<TNodeAttach, TLinkAttach>) graph.nodes.get(((NNode<TNodeAttach, TLinkAttach>)(link.getSource())).getNodeId());
			NNode<TNodeAttach, TLinkAttach> gdest = (NNode<TNodeAttach, TLinkAttach>) graph.nodes.get(((NNode<TNodeAttach, TLinkAttach>)(link.getDestination())).getNodeId());

			NLink<TNodeAttach, TLinkAttach> nlnk = new NLink<TNodeAttach, TLinkAttach>(gsource, gdest, link.getDirection(), link.getDistance(), link.getAttachment());
			
			int index = link1.getLinkId();
			if( graph.links.containsKey(index) )
				throw new IllegalStateException("duplicated link index");

			nlnk.setLinkId(index);
			nlnk.setFlag(link.getFlag());
			graph.links.put(index, nlnk);
			
			// 这里不确保每一个 node 里的 forwards 和 backwords 里的顺序都是一样的
			gsource.addForwardLink(nlnk);
			gdest.addBackwardLink(nlnk);
		}		
		
		// 设置 entries 和 exits
		for(NNode<TNodeAttach, TLinkAttach> node : entries){
			NNode<TNodeAttach, TLinkAttach> node1 = (NNode<TNodeAttach, TLinkAttach>) node;
			int id = node1.getNodeId();
			NNode<TNodeAttach, TLinkAttach> nc = (NNode<TNodeAttach, TLinkAttach>) graph.getNode(id);
			graph.getEntries().add(nc);
		}

		for(NNode<TNodeAttach, TLinkAttach> node : exits){
			NNode<TNodeAttach, TLinkAttach> node1 = (NNode<TNodeAttach, TLinkAttach>) node;
			int id = node1.getNodeId();
			NNode<TNodeAttach, TLinkAttach> nc = (NNode<TNodeAttach, TLinkAttach>) graph.getNode(id);
			graph.getExits().add(nc);
		}		
		graph.linkCount = this.linkCount;
		graph.nodeCount = this.nodeCount;
		
		return graph;
	}

	public void setEntrance(NNode<TNodeAttach, TLinkAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");

		entries.clear();
		entries .add(node);
	}


	public void setExit(NNode<TNodeAttach, TLinkAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");

		exits.clear();
		exits.add(node);
	}

	public List<NNode<TNodeAttach, TLinkAttach>> getEntries() {
		return entriesWrapper;
	}

	public List<NNode<TNodeAttach, TLinkAttach>> getExits() {
		return exitsWrapper;
	}

	public NNode<TNodeAttach, TLinkAttach> getNode(int id) {
		if( !nodes.containsKey(id))
			throw new IllegalArgumentException("node id:" + id + " not exist"); 
		return (NNode<TNodeAttach, TLinkAttach>) nodes.get(id);
	}

	public boolean contains(NNode<TNodeAttach, TLinkAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		return nodes.containsValue(node);
	}

	public void clearAllFlags(int initValue) {
		for (NNode<TNodeAttach, TLinkAttach> graphNode : nodes.values()) {
			graphNode.setFlag(initValue);
		}
		for (NLink<TNodeAttach, TLinkAttach> graphLink : links.values()) {
			graphLink.setFlag(initValue);
		}
	}

	public void clearAll(int initFlag, int initTemp) {
		for (NNode<TNodeAttach, TLinkAttach> graphNode : nodes.values()) {
			graphNode.setFlag(initFlag);
			graphNode.setTemp(initTemp);
		}
		for (NLink<TNodeAttach, TLinkAttach> graphLink : links.values()) {
			graphLink.setFlag(initFlag);
			graphLink.setTemp(initTemp);
		}
	}

}
