package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker;
import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker.WalkOptions;

/**
 * �������ڵ�ķ��������������ڵ�֮�����С���硣�㷨������
 * <li>���еĽڵ�������Ϊ0
 * <li>��ѡ�������ӽڵ㿪ʼ��һ���������չ������һ������
 * <li>������������У��������0��ǣ��������չ��<br>
 * �����������0��ǣ����ʾ�����˻�·������·�漰�����нڵ�ֵȡ��<br>
 * �������С��0��ǣ����ʾ�����˱����ɵ�·�ߣ�������Ҫ�������·���жϣ�ѡ��һ����̵�·����
 */
public class RasingNetAlgorithm {

	@SuppressWarnings("unchecked")
	public <TAttach> NGraph<TAttach> calculate(
			NGraph<TAttach> graph, List<NNode<TAttach>> selectedNodes) {
		
		for (NNode<TAttach> graphNode : selectedNodes) {
			if( ! graph.contains(graphNode) )
				throw new IllegalArgumentException("selectedNodes " + graphNode.getId() + " not in graph");
		}
		
		graph.clearAll(0, 0);
		int flag = 1;   // ָʾ�ı��ֵ����1��ʼ
		// �����ڵ㣬���ڵ�ı��ֵ����
		for(NNode<TAttach> node: selectedNodes){
			node.setFlag(flag);
			node.setTemp(0);// ����tempΪ���ۼ�ֵ
		}
		// ������λ�õĽڵ��Ԫ��
		ArrayList<NGraphElem<TAttach>> lowerElements = new ArrayList<NGraphElem<TAttach>>(selectedNodes.size()*2);
		// ��������Ԫ��
		Iterator<NGraphElem<TAttach>> it = new NodeSpreadWalker(graph, selectedNodes, WalkOptions.ALL).iterator();
		while(it.hasNext()){
			NGraphElem<TAttach> elem = it.next();
			
			if( elem instanceof NNode<?> ){
				NNode<TAttach> node = (NNode<TAttach>) elem;
				// ��ȡ�ܱ�Ԫ����С��ֵ
				NLink<TAttach> minLink = null;
				int minValue = Integer.MAX_VALUE, minNo0 = Integer.MAX_VALUE;
				for(NLink<TAttach> lnk1 : node.getForwardLinks()){
					// ָ������Ĺ�����Ҫ
					if(lnk1.getSource() == lnk1.getDestination() ) 
						continue;
					int weight = lnk1.getWeight();
					// �������ϵҪ����
					if( weight < minValue && lnk1.getDirection() == Direction.BIDIRECTION){
						minValue = weight;
						if( weight != 0 && weight < minNo0 ){
							minLink = lnk1;
							minNo0 = weight;
						}
					}
				}
				for(NLink<TAttach> lnk1 : node.getBackwardLinks()){
					// ָ������Ĺ�����Ҫ����˫���ϵ��Ҫ
					if(lnk1.getSource() == lnk1.getDestination())
						continue;
					int weight = lnk1.getWeight();
					// �����˫���ϵҪ��ע
					if( weight < minValue ){
						minValue = weight;
						if( weight != 0 && weight < minNo0 ){
							minLink = lnk1;
							minNo0 = weight;
						}
					}
				}
				if( minValue == 0 && minNo0 == 0)
					node.setTemp(node.getWeight()); // ����ܱ�ȫ�գ�����Ҫ�޸�ֵ
				else{
					// �����tempֵ = min(�ܱ�Weight) + ����weight
					node.setTemp(minNo0+node.getWeight());
					if( minValue > 0 )
						lowerElements.add(node);
				}
			}else if(elem instanceof NLink<?>){
				NLink<TAttach> link = (NLink<TAttach>) elem;
				// ���𴫵���ֵ
				NNode<TAttach> src = link.getSource();
				if( src.getTemp() == 0 )
					throw new IllegalStateException("source temp is 0");
				// �ۼ�tempֵ
				link.setTemp(src.getTemp()+link.getWeight());
				// ���˫�߶���ֵ�ˣ������
				if( link.getDestination().getTemp() != 0 )
					lowerElements.add(link);
			}else
				throw new UnsupportedOperationException("unknown element type");
		}
		// ��ʱ lowerElements ���¼�����е���͵㣬��ʱ���ٴ���͵������������ֵ�������ٵ�·�����ݵ�selectNodes
		// �������е�selectNodes��������֮�󣬾͵õ�����С������
		int index = 0;
		while( index < lowerElements.size() ){
			NGraphElem<TAttach> elem = lowerElements.get(index);
			
		}

		return graph;
	}

	private int calculate(int prevTotal, int distance, int weight) {
		return prevTotal + distance + weight;
	}

}

	