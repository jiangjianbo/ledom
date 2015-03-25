package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * �������ڵ�ķ��������������ڵ�֮�����С���硣�㷨������
 * <li>���еĽڵ�������Ϊ0
 * <li>��ѡ�������ӽڵ㿪ʼ��һ���������չ������һ������
 * <li>������������У��������0��ǣ��������չ��<br>
 * �����������0��ǣ����ʾ�����˻�·������·�漰�����нڵ�ֵȡ��<br>
 * �������С��0��ǣ����ʾ�����˱����ɵ�·�ߣ�������Ҫ�������·���жϣ�ѡ��һ����̵�·����
 */
public class RasingNetAlgorithm {

	public <TNodeAttach, TLinkAttach> NGraph<TNodeAttach, TLinkAttach> calculate(
			NGraph<TNodeAttach, TLinkAttach> graph, List<NNode<TNodeAttach, TLinkAttach>> selectedNodes) {
		
		for (NNode<TNodeAttach, TLinkAttach> graphNode : selectedNodes) {
			if( ! graph.contains(graphNode) )
				throw new IllegalArgumentException("selectedNodes " + graphNode.getNodeId() + " not in graph");
		}
		
		graph.clearAll(0, 0);
		int flag = 1;   // ָʾ�ı��ֵ����1��ʼ
		// �����ڵ㣬���ڵ�ı��ֵ����
		for(NNode<TNodeAttach, TLinkAttach> node: selectedNodes){
			node.setFlag(flag);
			node.setTemp(0);// ����tempΪ���ۼ�ֵ
		}
		int firstIndex = 0;	// ָʾ����ѭ���Ŀ�ʼλ��
		walkedList.addAll(selectedNodes);	// �������еĽڵ㣬Ȼ���walkedList[firstIndex .. end()] ��ʼ�ֲ�ݹ����
		while( walkedList.size() < nodes.size() ){
			// ���� flag
			flag ++;
			// ��firstIndex��ʼ�����������нڵ����һ�£���flag���ý�ȥ
			int lastIndex = walkedList.size();
			for(int i = firstIndex; i < lastIndex; ++i ){
				NNode<TNodeAttach, TLinkAttach> wnode = walkedList.get(i);
				// �����ڵ���ܱ��ӽڵ�
				for(NLink<TNodeAttach, TLinkAttach> clink : wnode.getForwardLinks()){
					if( clink.getDirection() == Direction.BACKWORD)
						throw new IllegalStateException("forward link of node " + wnode.getNodeId() + " can not include BACKWORD link");
					NNode<TNodeAttach, TLinkAttach> cnode = clink.getDestination();
					if( cnode == wnode )
						continue;	// ָ������Ĺ�����Ҫ����
					// ֻ����δ����Ľڵ���Ҫ�½�
					if( cnode.getFlag() == 0 ){
						cnode.setFlag(flag);
						cnode.setTemp(calculate(wnode.getTemp(), clink.getDistance(), cnode.getWeight()));
						// ����ڵ㣬׼����һ�����
						walkedList.add(cnode);
					}else if(cnode.getFlag() > 0){
						// ����Ѿ���ֵ����0����ʾ������һ���պ�·�����ҳ�·�����е�
					}else{
						// ����Ѿ���ֵС��0�����ʾ��Ҫ���·���ж�
						
					}
				}
			}
		}

		return graph;
	}

	private int calculate(int prevTotal, int distance, int weight) {
		return prevTotal + distance + weight;
	}

}

	