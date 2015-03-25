package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用提升节点的方法，来计算多个节点之间的最小网络。算法描述：
 * <li>所有的节点标记设置为0
 * <li>从选定的种子节点开始，一层层向外扩展，涟漪一样扩大
 * <li>涟漪扩大过程中，如果遇到0标记，则继续扩展，<br>
 * 如果遇到大于0标记，则表示遇到了回路，将回路涉及的所有节点值取反<br>
 * 如果遇到小于0标记，则表示遇到了标记完成的路线，所以需要进行最短路径判断，选择一个最短的路径。
 */
public class RasingNetAlgorithm {

	public <TNodeAttach, TLinkAttach> NGraph<TNodeAttach, TLinkAttach> calculate(
			NGraph<TNodeAttach, TLinkAttach> graph, List<NNode<TNodeAttach, TLinkAttach>> selectedNodes) {
		
		for (NNode<TNodeAttach, TLinkAttach> graphNode : selectedNodes) {
			if( ! graph.contains(graphNode) )
				throw new IllegalArgumentException("selectedNodes " + graphNode.getNodeId() + " not in graph");
		}
		
		graph.clearAll(0, 0);
		int flag = 1;   // 指示的标记值，从1开始
		// 遍历节点，将节点的标记值降低
		for(NNode<TNodeAttach, TLinkAttach> node: selectedNodes){
			node.setFlag(flag);
			node.setTemp(0);// 设置temp为总累计值
		}
		int firstIndex = 0;	// 指示遍历循环的开始位置
		walkedList.addAll(selectedNodes);	// 加入所有的节点，然后从walkedList[firstIndex .. end()] 开始分层递归遍历
		while( walkedList.size() < nodes.size() ){
			// 增加 flag
			flag ++;
			// 将firstIndex开始到结束的所有节点遍历一下，把flag设置进去
			int lastIndex = walkedList.size();
			for(int i = firstIndex; i < lastIndex; ++i ){
				NNode<TNodeAttach, TLinkAttach> wnode = walkedList.get(i);
				// 遍历节点的周边子节点
				for(NLink<TNodeAttach, TLinkAttach> clink : wnode.getForwardLinks()){
					if( clink.getDirection() == Direction.BACKWORD)
						throw new IllegalStateException("forward link of node " + wnode.getNodeId() + " can not include BACKWORD link");
					NNode<TNodeAttach, TLinkAttach> cnode = clink.getDestination();
					if( cnode == wnode )
						continue;	// 指向自身的关联不要处理
					// 只有尚未处理的节点需要下降
					if( cnode.getFlag() == 0 ){
						cnode.setFlag(flag);
						cnode.setTemp(calculate(wnode.getTemp(), clink.getDistance(), cnode.getWeight()));
						// 加入节点，准备下一层遍历
						walkedList.add(cnode);
					}else if(cnode.getFlag() > 0){
						// 如果已经有值大于0，表示发现了一条闭合路径，找出路径所有点
					}else{
						// 如果已经有值小于0，则表示需要最短路径判断
						
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

	