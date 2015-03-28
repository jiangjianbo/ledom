package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker;
import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker.WalkOptions;

/**
 * 用提升节点的方法，来计算多个节点之间的最小网络。算法描述：
 * <li>所有的节点标记设置为0
 * <li>从选定的种子节点开始，一层层向外扩展，涟漪一样扩大
 * <li>涟漪扩大过程中，如果遇到0标记，则继续扩展，<br>
 * 如果遇到大于0标记，则表示遇到了回路，将回路涉及的所有节点值取反<br>
 * 如果遇到小于0标记，则表示遇到了标记完成的路线，所以需要进行最短路径判断，选择一个最短的路径。
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
		int flag = 1;   // 指示的标记值，从1开始
		// 遍历节点，将节点的标记值降低
		for(NNode<TAttach> node: selectedNodes){
			node.setFlag(flag);
			node.setTemp(0);// 设置temp为总累计值
		}
		// 存放最低位置的节点和元素
		ArrayList<NGraphElem<TAttach>> lowerElements = new ArrayList<NGraphElem<TAttach>>(selectedNodes.size()*2);
		// 遍历所有元素
		Iterator<NGraphElem<TAttach>> it = new NodeSpreadWalker(graph, selectedNodes, WalkOptions.ALL).iterator();
		while(it.hasNext()){
			NGraphElem<TAttach> elem = it.next();
			
			if( elem instanceof NNode<?> ){
				NNode<TAttach> node = (NNode<TAttach>) elem;
				// 获取周边元素最小的值
				NLink<TAttach> minLink = null;
				int minValue = Integer.MAX_VALUE, minNo0 = Integer.MAX_VALUE;
				for(NLink<TAttach> lnk1 : node.getForwardLinks()){
					// 指向自身的关联不要
					if(lnk1.getSource() == lnk1.getDestination() ) 
						continue;
					int weight = lnk1.getWeight();
					// 纯输出关系要忽略
					if( weight < minValue && lnk1.getDirection() == Direction.BIDIRECTION){
						minValue = weight;
						if( weight != 0 && weight < minNo0 ){
							minLink = lnk1;
							minNo0 = weight;
						}
					}
				}
				for(NLink<TAttach> lnk1 : node.getBackwardLinks()){
					// 指向自身的关联不要，非双向关系不要
					if(lnk1.getSource() == lnk1.getDestination())
						continue;
					int weight = lnk1.getWeight();
					// 输入和双向关系要关注
					if( weight < minValue ){
						minValue = weight;
						if( weight != 0 && weight < minNo0 ){
							minLink = lnk1;
							minNo0 = weight;
						}
					}
				}
				if( minValue == 0 && minNo0 == 0)
					node.setTemp(node.getWeight()); // 如果周边全空，则不需要修改值
				else{
					// 自身的temp值 = min(周边Weight) + 自身weight
					node.setTemp(minNo0+node.getWeight());
					if( minValue > 0 )
						lowerElements.add(node);
				}
			}else if(elem instanceof NLink<?>){
				NLink<TAttach> link = (NLink<TAttach>) elem;
				// 负责传导数值
				NNode<TAttach> src = link.getSource();
				if( src.getTemp() == 0 )
					throw new IllegalStateException("source temp is 0");
				// 累加temp值
				link.setTemp(src.getTemp()+link.getWeight());
				// 如果双边都有值了，则结束
				if( link.getDestination().getTemp() != 0 )
					lowerElements.add(link);
			}else
				throw new UnsupportedOperationException("unknown element type");
		}
		// 此时 lowerElements 里记录了所有的最低点，这时候，再从最低点出发，沿着数值增加最少的路径上溯到selectNodes
		// 当把所有的selectNodes都包含了之后，就得到了最小生成树
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

	