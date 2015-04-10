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
 * ͼ�ṹ���壬�����ڵ�{@link NNode}������{@link NLink}��ְ�����£�
 * <ul>
 * <li>�����ڵ������ʵ��
 * <li>ά���ڵ�����ߵĸ���ֵ�ͱ��
 * <li>Ϊͼ��ÿһ��Ԫ�ظ���Ψһ�ı��
 * </ul>
 * @author jjb
 *
 * @param <TAttach> �ڵ�ĸ��Ӳ���
 */
public class NGraph<TAttach> {

	/**
	 * �ṩ�ⲿ�������ɲ������б�ṹ�����ұ�֤����Լ������
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
	 * ��źͽڵ�id��Ӧ���Զ�����ֵ
	 */
	Map<String, Map<?,?>> flags = new HashMap<String,Map<?,?>>();
	Map<String, Class<?>> flagClass = new HashMap<String,Class<?>>();
	
	/**
	 * ��ȡ���нڵ�Ŀ���
	 * @return
	 */
	public Collection<NNode<TAttach>> getAllNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}

	/**
	 * ��ȡ���й����Ŀ���
	 * @return
	 */
	public Collection<NLink<TAttach>> getAllLinks() {
		return Collections.unmodifiableCollection(links.values());
	}

	/**
	 * ����������ͼ�Ľڵ�
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
	 * ���������ڵ�֮�������
	 * @param from ���
	 * @param to   �յ�
	 * @param dir  ���߷���
	 * @param distance ���ߵľ��루Ȩ�أ�
	 * @return �����õ����ߣ��Ѿ��Զ����뵽ͼ��
	 */
	public NLink<TAttach> createLink(
			NNode<TAttach> from,
			NNode<TAttach> to, Direction dir,
			int distance){
		return createLink(from, to, dir, distance, null);
	}
	
	/**
	 * ���������ڵ�֮�������
	 * @param from ���
	 * @param to   �յ�
	 * @param dir  ���߷���
	 * @param distance ���ߵľ��루Ȩ�أ�
	 * @return �����õ����ߣ��Ѿ��Զ����뵽ͼ��
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
		// ���뵽 graph ��links�Ǽ���
		links.put(key, link);
		// ���ӵ�node��forward��backward��
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
	 * ��ͼ���Ƴ��ڵ㣬ͬʱɾ�����й���������
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
		// �Ƴ��ڵ���ص���������
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
		// �Ƴ��ڵ�
		nodes.remove(idx);
	}

	/**
	 * ��ͼ���Ƴ�����
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
		// �Ƴ������ڵ��еĵǼ�
		link.getSource().outputs.remove(link);
		link.getDestination().inputs.remove(link);
		// �Ƴ����߱���
		links.remove(idx);
	}

	/**
	 * ��ȫ����һ��ͼ�ṹʵ��
	 */
	public NGraph<TAttach> clone(){
		NGraph<TAttach> graph = createGraphInternal();
		// ���� node
		for(NNode<TAttach> node : nodes.values()){
			NNode<TAttach> nc = createNodeInternal(graph, node.getId(), node.getAttachment());
			
			int index = node.getId();
			if( graph.nodes.containsKey(index))
				throw new IllegalStateException("duplicated node index");

			nc.setFlag(node.getFlag());
			
			graph.nodes.put(index, nc);
		}
		// ���� link
		for(NLink<TAttach> link : links.values()){
			// �� graph �л�ȡlink���˶�Ӧ�� node
			NNode<TAttach> gsource = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getSource())).getId());
			NNode<TAttach> gdest = (NNode<TAttach>) graph.nodes.get(((NNode<TAttach>)(link.getDestination())).getId());

			NLink<TAttach> nlnk = createLinkInternal(graph, link.getId(), gsource, gdest, link.getDirection(), link.getWeight(), link.getAttachment());
			
			int index = link.getId();
			if( graph.links.containsKey(index) )
				throw new IllegalStateException("duplicated link index");

			nlnk.setFlag(link.getFlag());
			graph.links.put(index, nlnk);
			
			// ���ﲻȷ��ÿһ�� node ��� forwards �� backwords ���˳����һ����
			gsource.addOutputLink(nlnk);
			gdest.addInputLink(nlnk);
		}		
		
		// ���� entries �� exits
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
	 * ��ȡͼ����ڽڵ�
	 * @return
	 */
	public List<NNode<TAttach>> getEntries() {
		return entriesWrapper;
	}

	/**
	 * ��ȡͼ�ĳ��ڽڵ�
	 * @return
	 */
	public List<NNode<TAttach>> getExits() {
		return exitsWrapper;
	}

	/**
	 * ͨ��id��ȡ�ڵ�
	 * @param id �ڵ��id
	 * @return
	 */
	public NNode<TAttach> getNode(int id) {
		if( !nodes.containsKey(id))
			throw new IllegalArgumentException("node id:" + id + " not exist"); 
		return (NNode<TAttach>) nodes.get(id);
	}

	/**
	 * ͨ��id��ȡ����
	 * @param id
	 * @return
	 */
	public NLink<TAttach> getLink(int id) {
		if( !links.containsKey(id))
			throw new IllegalArgumentException("link id:" + id + " not exist"); 
		return (NLink<TAttach>) links.get(id);
	}

	/**
	 * ͨ��id��ȡԪ��
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
	 * �ж��Ƿ�����ڵ�
	 * @param node Ҫ�жϵĽڵ�
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
	 * �ж��Ƿ��������
	 * @param link Ҫ�жϵ�����
	 * @return
	 */
	public boolean contains(NLink<TAttach> link) {
		if( link == null )
			throw new IllegalArgumentException("link is null");
		
		int id = link.getId();
		return links.containsKey(id) && links.get(id) == link;
	}

	/**
	 * �ж�Ԫ���Ƿ������ͼ��
	 * @param elem
	 * @return
	 */
	public boolean contains(NGraphElem<TAttach> elem){
		return (elem instanceof NNode && contains((NNode<TAttach>)elem))
				|| ((elem instanceof NLink) && contains((NLink<TAttach>)elem));
	}
	
	/**
	 * �ж�id��Ӧ��Ԫ���Ƿ������ͼ��
	 * @param id
	 * @return
	 */
	public boolean contains(int id){
		return nodes.containsKey(id) || links.containsKey(id);
	}
	
	/**
	 * ����һ���µı������
	 * @param flagType Ҫ�����ı������
	 * @param valueType �ñ�ǵ�Ĭ������
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
	 * ��ȡĳ�����͵����б��
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
	 * ��ȡԪ�ص�ĳ�����͵ı��ֵ
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
	 * ����ָ�����͵����б�ǣ�����Ĭ��ֵ����
	 * @param flagType
	 */
	public<T> void clearFlags(String flagType) {
		Map<Integer, T> flagMap = getFlags(flagType);
		flagMap.clear();
	}

	/**
	 * ��������еı�Ǻ�Ĭ��ֵ�������Ҫ����ʹ�ã�����Ҫ���µ��� {@link #createFlags(String, Object)}
	 */
	public void clearAllFlags() {
		for(Map<?, ?> flagMap : flags.values())
			flagMap.clear();
		flags.clear();
		flagClass.clear();
	}

}
