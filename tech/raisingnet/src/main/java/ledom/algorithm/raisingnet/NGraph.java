package ledom.algorithm.raisingnet;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 图结构定义，包含节点{@link NNode}和连线{@link NLink}。职责如下：
 * <ul>
 * <li>创建节点和连线实例
 * <li>维护节点和连线的附加值和标记
 * <li>为图的每一个元素赋予唯一的编号
 * </ul>
 * @author jjb
 *
 * @param <TAttach> 节点的附加参数
 */
public class NGraph<TAttach> {

	/**
	 * 提供外部可以自由操作的列表结构，并且保证各种约束成立
	 * @author jjb
	 *
	 */
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

	int elemCount = 0;
	Map<Integer, NNode<TAttach>> nodes = new HashMap<Integer, NNode<TAttach>>();
	Map<Integer, NLink<TAttach>> links = new HashMap<Integer, NLink<TAttach>>();
	
	private List<NNode<TAttach>> entries = new ArrayList<NNode<TAttach>>();
	private List<NNode<TAttach>> exits = new ArrayList<NNode<TAttach>>();

	WrapperList entriesWrapper = new WrapperList(entries);
	WrapperList exitsWrapper = new WrapperList(exits);

	/**
	 * 存放和节点id对应的自定义标记值
	 */
	Map<String, Map<?,?>> flags = new HashMap<String,Map<?,?>>();
	Map<String, Class<?>> flagClass = new HashMap<String,Class<?>>();
	
	/**
	 * 获取所有节点的快照
	 * @return
	 */
	public Collection<NNode<TAttach>> getAllNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	/**
	 * 获取所有关联的快照
	 * @return
	 */
	public Collection<NLink<TAttach>> getAllLinks() {
		return Collections.unmodifiableCollection(links.values());
	}

	/**
	 * 创建隶属于图的节点
	 * @param attach
	 * @return
	 */
	public NNode<TAttach> createNode(TAttach attach) {
		int key = elemCount ++;
		NNode<TAttach> node = createNodeInternal(this, key, attach);
		
		nodes.put(key, node);
		
		return node;
	}

	protected NNode<TAttach> createNodeInternal(NGraph<TAttach> graph, int key, TAttach attach) {
		return new NNode<TAttach>(graph, key, attach);
	}

	/**
	 * 创建两个节点之间的连线
	 * @param from 起点
	 * @param to   终点
	 * @param dir  连线方向
	 * @param distance 连线的距离（权重）
	 * @return 创建好的连线，已经自动加入到图中
	 */
	public NLink<TAttach> createLink(
			NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance){
		return createLink(from, to, dir, distance, null);
	}
	
	/**
	 * 创建两个节点之间的连线
	 * @param from 起点
	 * @param to   终点
	 * @param dir  连线方向
	 * @param distance 连线的距离（权重）
	 * @return 创建好的连线，已经自动加入到图中
	 */
	public NLink<TAttach> createLink(
			NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance, TAttach attach) {
		if( from == null )
			throw new IllegalArgumentException("from can not be null");
		if( to == null  )
			throw new IllegalArgumentException("to can not be null");
		if( from.getGraph() != this )
			throw new IllegalArgumentException("from node must contains in graph");
		if( to.getGraph() != this )
			throw new IllegalArgumentException("to node must contains in graph");
		
		int key = elemCount++;
		NLink<TAttach> link = createLinkInternal(this, key, from, to, dir, distance, attach);
		// 加入到 graph 的links登记中
		links.put(key, link);
		// 增加到node的forward和backward中
		((NNode<TAttach>)from).addOutputLink(link);
		((NNode<TAttach>)to).addInputLink(link);
		
		return link;
	}

	protected NLink<TAttach> createLinkInternal(NGraph<TAttach> graph, int key, NNode<TAttach> from,
			NNode<TAttach> to, Direction dir, int distance, TAttach attach) {
		return new NLink<TAttach>(this, key, from, to, dir, distance);
	}

	protected NGraph<TAttach> createGraphInternal() {
		return new NGraph<TAttach>();
	}

	/**
	 * 从图中移除节点，同时删除所有关联的连线
	 * @param node
	 */
	public void removeNode(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if(!nodes.containsValue(node) )
			throw new IllegalArgumentException("node not include in graph");
		
		int idx = node.getId();
		if( nodes.get(idx) != node )
			throw new IllegalStateException("incorrect node index");
		// 移除节点相关的所有连线
		for( NLink<TAttach> flink : node.outputs){
			flink.getDestination().inputs.remove(flink);
			links.remove(flink);
		}
		node.outputs.clear();
		for( NLink<TAttach> blink : node.inputs){
			blink.getSource().outputs.remove(blink);
			links.remove(blink);
		}
		node.inputs.clear();
		// 移除节点
		nodes.remove(idx);
	}

	/**
	 * 从图中移除连线
	 * @param link
	 */
	public void removeLink(NLink<TAttach> link) {
		if( link == null )
			throw new IllegalArgumentException("link is null");
		if(!links.containsValue(link) )
			throw new IllegalArgumentException("link not include in graph");
		
		int idx = link.getId();
		if( links.get(idx) != link )
			throw new IllegalStateException("incorrect link index");
		// 移除关联节点中的登记
		link.getSource().outputs.remove(link);
		link.getDestination().inputs.remove(link);
		// 移除连线本身
		links.remove(idx);
	}

	/**
	 * 完全复制一个图结构实例
	 */
	public NGraph<TAttach> clone(){
		NGraph<TAttach> graph = createGraphInternal();
		// 创建 node
		for(NNode<TAttach> node : nodes.values()){
			NNode<TAttach> nc = createNodeInternal(graph, node.getId(), node.getAttachment());
			
			int index = node.getId();
			if( graph.nodes.containsKey(index))
				throw new IllegalStateException("duplicated node index");

			nc.setFlag(node.getFlag());
			
			graph.nodes.put(index, nc);
		}
		// 创建 link
		for(NLink<TAttach> link : links.values()){
			// 从 graph 中获取link两端对应的 node
			NNode<TAttach> gsource = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getSource())).getId());
			NNode<TAttach> gdest = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getDestination())).getId());

			NLink<TAttach> nlnk = createLinkInternal(graph, link.getId(), gsource, gdest, link.getDirection(), link.getWeight(), link.getAttachment());
			
			int index = link.getId();
			if( graph.links.containsKey(index) )
				throw new IllegalStateException("duplicated link index");

			nlnk.setFlag(link.getFlag());
			graph.links.put(index, nlnk);
			
			// 这里不确保每一个 node 里的 forwards 和 backwords 里的顺序都是一样的
			gsource.addOutputLink(nlnk);
			gdest.addInputLink(nlnk);
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
		graph.elemCount = this.elemCount;
		
		return graph;
	}

	/**
	 * 获取图的入口节点
	 * @return
	 */
	public List<NNode<TAttach>> getEntries() {
		return entriesWrapper;
	}

	/**
	 * 获取图的出口节点
	 * @return
	 */
	public List<NNode<TAttach>> getExits() {
		return exitsWrapper;
	}

	/**
	 * 通过id获取节点
	 * @param id 节点的id
	 * @return
	 */
	public NNode<TAttach> getNode(int id) {
		if( !nodes.containsKey(id))
			throw new IllegalArgumentException("node id:" + id + " not exist"); 
		return (NNode<TAttach>) nodes.get(id);
	}

	/**
	 * 通过id获取连线
	 * @param id
	 * @return
	 */
	public NLink<TAttach> getLink(int id) {
		if( !links.containsKey(id))
			throw new IllegalArgumentException("link id:" + id + " not exist"); 
		return (NLink<TAttach>) links.get(id);
	}

	/**
	 * 通过id获取元素
	 * @param id
	 * @return
	 */
	public NGraphElem<TAttach> getElement(int id){
		if( nodes.containsKey(id))
			return nodes.get(id);
		if( links.containsKey(id))
			return links.get(id);
		throw new IllegalArgumentException("invalid element id:" + id);
	}
	
	/**
	 * 判断是否包含节点
	 * @param node 要判断的节点
	 * @return
	 */
	public boolean contains(NNode<TAttach> node) {
		if( node == null )
			throw new IllegalArgumentException("node is null");
		if( node.getGraph() != this )
			return false;
		
		int id = node.getId();
		return nodes.containsKey(id) && nodes.get(id) == node;
	}

	/**
	 * 判断是否包含连线
	 * @param link 要判断的连线
	 * @return
	 */
	public boolean contains(NLink<TAttach> link) {
		if( link == null )
			throw new IllegalArgumentException("link is null");
		
		int id = link.getId();
		return links.containsKey(id) && links.get(id) == link;
	}

	/**
	 * 判断元素是否包含在图中
	 * @param elem
	 * @return
	 */
	public boolean contains(NGraphElem<TAttach> elem){
		return (elem instanceof NNode && contains((NNode<TAttach>)elem))
				|| ((elem instanceof NLink) && contains((NLink<TAttach>)elem));
	}
	
	/**
	 * 判断id对应的元素是否包含在图中
	 * @param id
	 * @return
	 */
	public boolean contains(int id){
		return nodes.containsKey(id) || links.containsKey(id);
	}
	
	/**
	 * 创建一个新的标记类型
	 * @param flagType 要创建的标记名称
	 * @param valueType 该标记的默认类型
	 * @return
	 */
	public <T> Map<Integer, T> createFlags(String flagType, Class<T> valueType){
		if( flags.containsKey(flagType) )
			throw new IllegalArgumentException("flag type exists: " + flagType);
		
		Map<Integer, T> flagMap = new HashMap<Integer, T>();
		flags.put(flagType, flagMap);
		flagClass.put(flagType, valueType);
		return flagMap;
	}

	/**
	 * 获取某个类型的所有标记
	 * @param flagType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public<T> Map<Integer, T> getFlags(String flagType){
		if( !flags.containsKey(flagType) )
			throw new IllegalArgumentException("invalid flag type: " + flagType);	
		return (Map<Integer, T>) flags.get(flagType);
	}
	
	/**
	 * 获取元素的某个类型的标记值
	 * @param flagType
	 * @param elem
	 * @return
	 */
	public<T> T getFlag(String flagType, NGraphElem<TAttach> elem){
		if( elem == null )
			throw new IllegalArgumentException("null argument elem");
		return getFlag(flagType, elem.getId());
	}

	@SuppressWarnings("unchecked")
	public<T> T getFlag(String flagType, int id){
		if( !flags.containsKey(flagType) )
			throw new IllegalArgumentException("invalid flag type: " + flagType);
		
		Map<Integer, T> flagMap = (Map<Integer, T>) flags.get(flagType);
		if( flagMap.containsKey(id) )
			return flagMap.get(id);
		else{
			Class<?> clazz = flagClass.get(flagType);
			T value;
			try {
				value = (T) clazz.newInstance();
				flagMap.put(id, value);
				return value;
			} catch (Exception e) {
				throw new RuntimeException("new flag class error:" + clazz);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public<T> void setFlag(String flagType, int id, T value){
		if( !flags.containsKey(flagType) )
			throw new IllegalArgumentException("invalid flag type: " + flagType);
		
//		if( !contains(id) )
//			throw new IllegalArgumentException("invalid element:" + id);
		
		Map<Integer, T> flagMap = (Map<Integer, T>) flags.get(flagType);
		flagMap.put(id, value);
	}

	/**
	 * 清理指定类型的所有标记，保持默认值不变
	 * @param flagType
	 */
	public<T> void clearFlags(String flagType) {
		Map<Integer, T> flagMap = getFlags(flagType);
		flagMap.clear();
	}

	/**
	 * 清理掉所有的标记和默认值。如果需要重新使用，则需要重新调用 {@link #createFlags(String, Object)}
	 */
	public void clearAllFlags() {
		for(Map<?, ?> flagMap : flags.values())
			flagMap.clear();
		flags.clear();
		flagClass.clear();
	}

}
