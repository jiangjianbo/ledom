package ledom.algorithm.raisingnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker;
import ledom.algorithm.raisingnet.iterators.NodeSpreadWalker.WalkOptions;

/**
 * 用提升节点的方法，来计算多个节点之间的最小网络。算法描述：
 * <li>所有的节点标记设置为0
 * <li>从选定的种子节点开始，一层层向外扩展，涟漪一样扩大。在扩大过程中，每一个节点都要记录自己受影响的根源，以便最后回溯。
 * <li>涟漪扩大过程中，如果遇到0标记，则继续扩展。 如果遇到大于0标记，则表示遇到了回路，需要将最大值确保在link上，然后登记link为最低点<br>
 * <li>遍历完成的条件是所有开始节点都已经被登记的link覆盖。
 * <li>最后将登记的最低点link排序，回溯出所有的关联节点。就是最小生成树。
 */
public class RasingNetAlgorithm<TAttach> {

	
	/**
	 * 算法为每一个图形元素设置的标记类型
	 */
	private static final String GRAPH_FLAG = "RasingNetAlgorithm";
	private static final int UNKNOWN_SOURCE_ID = -1;
	private static final int UNKNOWN_DEST_ID = -1;
	private static final int UNKNOWN_LEVEL = -1;
	private static final int UNKNOWN_TOTAL_WEIGHT = Integer.MAX_VALUE;

	static class RasingNetFlag{
		/**
		 * 存放累计的权重值
		 */
		int totalWeight = UNKNOWN_TOTAL_WEIGHT;	
		/**
		 * 用于节点，记录是受到哪一个连线的影响；用于连线，表示在双向关联的时候记录受到哪一个节点的影响
		 */
		int sourceId = UNKNOWN_SOURCE_ID;
		/**
		 * 存放最终的来源是哪一个起点
		 */
		int startId = UNKNOWN_DEST_ID;
		/**
		 * 记录当前节点所在的层数
		 */
		int level = UNKNOWN_LEVEL;
	}
	
	
	@SuppressWarnings("unchecked")
	public  NGraph<TAttach> calculate(
			NGraph<TAttach> graph, List<NNode<TAttach>> selectedNodes) {
		// 检查node是否都在graph中
		for (NNode<TAttach> graphNode : selectedNodes) {
			if( ! graph.contains(graphNode) )
				throw new IllegalArgumentException("selectedNodes " + graphNode.getId() + " not in graph");
		}
		// 图形元素的标记对象
		graph.createFlags(GRAPH_FLAG, RasingNetFlag.class);
		
		int level = 1;   // 指示的标记值，从1开始
		// 存放最低位置的元素，只允许连线，因为节点无法用一个sourceId和destId来描述，而连线只有两头
		ArrayList<NGraphElem<TAttach>> lowerElements = new ArrayList<NGraphElem<TAttach>>(selectedNodes.size()*2);
		// 遍历所有元素，每个元素只需要计算自己受到的影响即可
		Iterator<NGraphElem<TAttach>> it = new NodeSpreadWalker<TAttach>(graph, selectedNodes, WalkOptions.ALL).iterator();
		while(it.hasNext()){
			NGraphElem<TAttach> elem = it.next();
			// 所有元素只需要处理自身受到的影响就可以了，不要考虑其他节点，因为每一个节点最终都会被遍历到。
			if( elem instanceof NNode<?> ){
				processNode(level, lowerElements, (NNode<TAttach>) elem);
			}else if(elem instanceof NLink<?>){
				processLink(level, lowerElements, (NLink<TAttach>) elem);
			}else
				throw new UnsupportedOperationException("unknown element type");
		}
		// 此时 lowerElements 里记录了所有的最低点，这时候，再从最低点出发，沿着数值增加最少的路径上溯到selectNodes
		// 当把所有的selectNodes都包含了之后，就得到了最小生成树
		// 处理的时候，通过最低点回溯到selectNodes，给出每两对node之间的最小距离，然后选择
		// 最小生成树prime算法的基本思想
		// 1.清空生成树，任取一个顶点加入生成树
		// 2.在那些一个端点在生成树里，另一个端点不在生成树里的边中，选取一条权最小的边，将它和另一个端点加进生成树
		// 3.重复步骤2，直到所有的顶点都进入了生成树为止，此时的生成树就是最小生成树
		int index = 0;
		while( index < lowerElements.size() ){
			NGraphElem<TAttach> elem = lowerElements.get(index);
			
		}

		return graph;
	}

	// 处理连接
	private void processLink(int level,	ArrayList<NGraphElem<TAttach>> lowers, NLink<TAttach> link) {
		RasingNetFlag linkf = link.getFlag(GRAPH_FLAG);
		// 初始化level
		linkf.level = level;
		// 负责传导数值
		NNode<TAttach> src = link.getSource();
		RasingNetFlag sflag = src.getFlag(GRAPH_FLAG);	
		NNode<TAttach> dest = link.getDestination();
		RasingNetFlag dflag = dest.getFlag(GRAPH_FLAG);
		// 先判断src到dest的方向
		if( sflag.totalWeight != UNKNOWN_TOTAL_WEIGHT){
			linkf.totalWeight = sflag.totalWeight + link.getWeight();
			linkf.sourceId = src.getId();
			linkf.startId = sflag.startId;
			// 判断是否已经最低
			if( dflag.totalWeight != UNKNOWN_TOTAL_WEIGHT )
				lowers.add(link);
		}
		else if( link.getDirection() == Direction.BIDIRECTION){
			// 双向关联要判断两个来源
			// 必须已经初始化，不可能两头都没有初始化
			assert dflag.totalWeight != UNKNOWN_TOTAL_WEIGHT : "destination must inited: " + dest.getId();
			// 设置来源关联
			linkf.totalWeight = dflag.totalWeight + link.getWeight();
			linkf.sourceId = dest.getId();
			linkf.startId = dflag.startId;
		}else
			throw new IllegalStateException("link id " + link.getId()); // 单向backward的不可能继续
		// 这里不需要判断是否最低了，因为第一个if已经处理了
	}

	// 处理节点。节点值依附于最小的输入连线。如果节点的出入分属两种不同的起点，则需要登记为最小节点
	private void processNode(int level,	ArrayList<NGraphElem<TAttach>> lowers, NNode<TAttach> node) {
		// 获取周边元素最小的值
		NLink<TAttach> minLink = null;
		int minTotal = UNKNOWN_TOTAL_WEIGHT;
		int linkInCount = 0, linkOutCount = 0, linkBiCount = 0;	// 计数符合条件的所有link数量
		// 测试是否存在未处理的连线，如果存在则表示节点不是最低
		boolean hasLinkEmpty = false;	
		// 循环所有的导出关联，寻找累加和最小的连线
		for(NLink<TAttach> linkf : node.getOutputLinks()){
			// 指向自身的关联不要处理
			if(linkf.getSource() == linkf.getDestination() ) 
				continue;
			linkOutCount ++;
			RasingNetFlag lnkFlag = linkf.getFlag(GRAPH_FLAG);					
			// 沿着连线的方向，只有能够影响自己的连线才处理
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
		// 循环所有导入关联，寻找累加和最小的连线
		for(NLink<TAttach> linkb : node.getInputLinks()){
			// 指向自身的关联不要，非双向关系不要
			if(linkb.getSource() == linkb.getDestination())
				continue;
			linkInCount++;
			if( linkb.getDirection() == Direction.BIDIRECTION){
				linkBiCount ++;
				linkInCount --;
			}
			RasingNetFlag lnkFlag = linkb.getFlag(GRAPH_FLAG);	
			// 导入的连线当然会影响节点的
			if( lnkFlag.totalWeight == UNKNOWN_TOTAL_WEIGHT )
				hasLinkEmpty = true;
			if( lnkFlag.totalWeight < minTotal ){
				minTotal = lnkFlag.totalWeight;
				minLink = linkb;
			}
		}
		// 找到最小连线之后，开始处理
		RasingNetFlag nflag = node.getFlag(GRAPH_FLAG);
		nflag.level = level;
		if( minLink == null ){
			// 如果周边全空，则不需要修改值
			nflag.startId = nflag.sourceId = node.getId();
			nflag.totalWeight = node.getWeight();
		}else{
			// 自身的totalWeight值 = min(周边Weight) + 自身weight
			RasingNetFlag mFlag = minLink.getFlag(GRAPH_FLAG);	
			nflag.sourceId = minLink.getId();
			nflag.startId = mFlag.startId;
			nflag.totalWeight = mFlag.totalWeight + node.getWeight();
			// 处理最后一个节点的情况，节点一定要有出有入
			if( !hasLinkEmpty && (linkBiCount >= 2 || (linkOutCount > 0 && linkInCount > 0)))
				// 如果已经有连线了，表示遍历结束，本节点就是最低的位置						
				lowers.add(node);
		}
	}


}

	