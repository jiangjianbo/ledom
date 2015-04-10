package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker;
import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker.WalkOptions;

/**
 * �������ڵ�ķ��������������ڵ�֮�����С���硣�㷨������
 * <li>���еĽڵ�������Ϊ0
 * <li>��ѡ�������ӽڵ㿪ʼ��һ���������չ������һ����������������У�ÿһ���ڵ㶼Ҫ��¼�Լ���Ӱ��ĸ�Դ���Ա������ݡ�
 * <li>������������У��������0��ǣ��������չ�� �����������0��ǣ����ʾ�����˻�·����Ҫ�����ֵȷ����link�ϣ�Ȼ��Ǽ�linkΪ��͵�<br>
 * <li>������ɵ����������п�ʼ�ڵ㶼�Ѿ����Ǽǵ�link���ǡ�
 * <li>��󽫵Ǽǵ���͵�link���򣬻��ݳ����еĹ����ڵ㡣������С��������
 */
public class RasingNetAlgorithm<TAttach> {

	
	/**
	 * �㷨Ϊÿһ��ͼ��Ԫ�����õı������
	 */
	private static final String GRAPH_FLAG = "RasingNetAlgorithm";
	private static final int UNKNOWN_SOURCE_ID = -1;
	private static final int UNKNOWN_DEST_ID = -1;
	private static final int UNKNOWN_LEVEL = -1;
	private static final int UNKNOWN_TOTAL_WEIGHT = Integer.MAX_VALUE;

	static class RasingNetFlag{
		/**
		 * ����ۼƵ�Ȩ��ֵ
		 */
		int totalWeight = UNKNOWN_TOTAL_WEIGHT;	
		/**
		 * ���ڽڵ㣬��¼���ܵ���һ�����ߵ�Ӱ�죻�������ߣ���ʾ��˫�������ʱ���¼�ܵ���һ���ڵ��Ӱ��
		 */
		int sourceId = UNKNOWN_SOURCE_ID;
		/**
		 * ������յ���Դ����һ�����
		 */
		int startId = UNKNOWN_DEST_ID;
		/**
		 * ��¼��ǰ�ڵ����ڵĲ���
		 */
		int level = UNKNOWN_LEVEL;
	}
	
	
	@SuppressWarnings("unchecked")
	public  NGraph<TAttach> calculate(
			NGraph<TAttach> graph, List<NNode<TAttach>> selectedNodes) {
		// ���node�Ƿ���graph��
		for (NNode<TAttach> graphNode : selectedNodes) {
			if( ! graph.contains(graphNode) )
				throw new IllegalArgumentException("selectedNodes " + graphNode.getId() + " not in graph");
		}
		// ͼ��Ԫ�صı�Ƕ���
		graph.createFlags(GRAPH_FLAG, RasingNetFlag.class);
		
		int level = 1;   // ָʾ�ı��ֵ����1��ʼ
		// ������λ�õ�Ԫ�أ�ֻ�������ߣ���Ϊ�ڵ��޷���һ��sourceId��destId��������������ֻ����ͷ
		ArrayList<NGraphElem<TAttach>> lowerElements = new ArrayList<NGraphElem<TAttach>>(selectedNodes.size()*2);
		// ��������Ԫ�أ�ÿ��Ԫ��ֻ��Ҫ�����Լ��ܵ���Ӱ�켴��
		Iterator<NGraphElem<TAttach>> it = new NodeSpreadWalker<TAttach>(graph, selectedNodes, WalkOptions.ALL).iterator();
		while(it.hasNext()){
			NGraphElem<TAttach> elem = it.next();
			// ����Ԫ��ֻ��Ҫ���������ܵ���Ӱ��Ϳ����ˣ���Ҫ���������ڵ㣬��Ϊÿһ���ڵ����ն��ᱻ��������
			if( elem instanceof NNode<?> ){
				processNode(level, lowerElements, (NNode<TAttach>) elem);
			}else if(elem instanceof NLink<?>){
				processLink(level, lowerElements, (NLink<TAttach>) elem);
			}else
				throw new UnsupportedOperationException("unknown element type");
		}
		// ��ʱ lowerElements ���¼�����е���͵㣬��ʱ���ٴ���͵������������ֵ�������ٵ�·�����ݵ�selectNodes
		// �������е�selectNodes��������֮�󣬾͵õ�����С������
		// �����ʱ��ͨ����͵���ݵ�selectNodes������ÿ����node֮�����С���룬Ȼ��ѡ��
		// ��С������prime�㷨�Ļ���˼��
		// 1.�������������ȡһ���������������
		// 2.����Щһ���˵������������һ���˵㲻����������ı��У�ѡȡһ��Ȩ��С�ıߣ���������һ���˵�ӽ�������
		// 3.�ظ�����2��ֱ�����еĶ��㶼������������Ϊֹ����ʱ��������������С������
		int index = 0;
		while( index < lowerElements.size() ){
			NGraphElem<TAttach> elem = lowerElements.get(index);
			
		}

		return graph;
	}

	// ��������
	private void processLink(int level,	ArrayList<NGraphElem<TAttach>> lowers, NLink<TAttach> link) {
		RasingNetFlag linkf = link.getFlag(GRAPH_FLAG);
		// ��ʼ��level
		linkf.level = level;
		// ���𴫵���ֵ
		NNode<TAttach> src = link.getSource();
		RasingNetFlag sflag = src.getFlag(GRAPH_FLAG);	
		NNode<TAttach> dest = link.getDestination();
		RasingNetFlag dflag = dest.getFlag(GRAPH_FLAG);
		// ���ж�src��dest�ķ���
		if( sflag.totalWeight != UNKNOWN_TOTAL_WEIGHT){
			linkf.totalWeight = sflag.totalWeight + link.getWeight();
			linkf.sourceId = src.getId();
			linkf.startId = sflag.startId;
			// �ж��Ƿ��Ѿ����
			if( dflag.totalWeight != UNKNOWN_TOTAL_WEIGHT )
				lowers.add(link);
		}
		else if( link.getDirection() == Direction.BIDIRECTION){
			// ˫�����Ҫ�ж�������Դ
			// �����Ѿ���ʼ������������ͷ��û�г�ʼ��
			assert dflag.totalWeight != UNKNOWN_TOTAL_WEIGHT : "destination must inited: " + dest.getId();
			// ������Դ����
			linkf.totalWeight = dflag.totalWeight + link.getWeight();
			linkf.sourceId = dest.getId();
			linkf.startId = dflag.startId;
		}else
			throw new IllegalStateException("link id " + link.getId()); // ����backward�Ĳ����ܼ���
		// ���ﲻ��Ҫ�ж��Ƿ�����ˣ���Ϊ��һ��if�Ѿ�������
	}

	// ����ڵ㡣�ڵ�ֵ��������С���������ߡ�����ڵ�ĳ���������ֲ�ͬ����㣬����Ҫ�Ǽ�Ϊ��С�ڵ�
	private void processNode(int level,	ArrayList<NGraphElem<TAttach>> lowers, NNode<TAttach> node) {
		// ��ȡ�ܱ�Ԫ����С��ֵ
		NLink<TAttach> minLink = null;
		int minTotal = UNKNOWN_TOTAL_WEIGHT;
		int linkInCount = 0, linkOutCount = 0, linkBiCount = 0;	// ������������������link����
		// �����Ƿ����δ��������ߣ�����������ʾ�ڵ㲻�����
		boolean hasLinkEmpty = false;	
		// ѭ�����еĵ���������Ѱ���ۼӺ���С������
		for(NLink<TAttach> linkf : node.getOutputLinks()){
			// ָ������Ĺ�����Ҫ����
			if(linkf.getSource() == linkf.getDestination() ) 
				continue;
			linkOutCount ++;
			RasingNetFlag lnkFlag = linkf.getFlag(GRAPH_FLAG);					
			// �������ߵķ���ֻ���ܹ�Ӱ���Լ������߲Ŵ���
			if( linkf.getDirection() == Direction.BIDIRECTION){
				linkBiCount++;
				linkOutCount --;
				if( lnkFlag.totalWeight == UNKNOWN_TOTAL_WEIGHT )
					hasLinkEmpty = true;
				if(lnkFlag.totalWeight < minTotal){
					minTotal = lnkFlag.totalWeight;
					minLink = linkf;
				}
			}
		}
		// ѭ�����е��������Ѱ���ۼӺ���С������
		for(NLink<TAttach> linkb : node.getInputLinks()){
			// ָ������Ĺ�����Ҫ����˫���ϵ��Ҫ
			if(linkb.getSource() == linkb.getDestination())
				continue;
			linkInCount++;
			if( linkb.getDirection() == Direction.BIDIRECTION){
				linkBiCount ++;
				linkInCount --;
			}
			RasingNetFlag lnkFlag = linkb.getFlag(GRAPH_FLAG);	
			// ��������ߵ�Ȼ��Ӱ��ڵ��
			if( lnkFlag.totalWeight == UNKNOWN_TOTAL_WEIGHT )
				hasLinkEmpty = true;
			if( lnkFlag.totalWeight < minTotal ){
				minTotal = lnkFlag.totalWeight;
				minLink = linkb;
			}
		}
		// �ҵ���С����֮�󣬿�ʼ����
		RasingNetFlag nflag = node.getFlag(GRAPH_FLAG);
		nflag.level = level;
		if( minLink == null ){
			// ����ܱ�ȫ�գ�����Ҫ�޸�ֵ
			nflag.startId = nflag.sourceId = node.getId();
			nflag.totalWeight = node.getWeight();
		}else{
			// �����totalWeightֵ = min(�ܱ�Weight) + ����weight
			RasingNetFlag mFlag = minLink.getFlag(GRAPH_FLAG);	
			nflag.sourceId = minLink.getId();
			nflag.startId = mFlag.startId;
			nflag.totalWeight = mFlag.totalWeight + node.getWeight();
			// �������һ���ڵ��������ڵ�һ��Ҫ�г�����
			if( !hasLinkEmpty && (linkBiCount >= 2 || (linkOutCount > 0 && linkInCount > 0)))
				// ����Ѿ��������ˣ���ʾ�������������ڵ������͵�λ��						
				lowers.add(node);
		}
	}


}

	