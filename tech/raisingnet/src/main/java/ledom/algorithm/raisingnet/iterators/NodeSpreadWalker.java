package ledom.algorithm.raisingnet.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ledom.algorithm.raisingnet.NGraph;
import ledom.algorithm.raisingnet.NGraphElem;
import ledom.algorithm.raisingnet.NLink;
import ledom.algorithm.raisingnet.NNode;

public class NodeSpreadWalker<TAttach> implements
		Iterable<NGraphElem<TAttach>> {

	public static enum WalkOptions {
		NODE(0), FORWARD_LINK(1), BACKWARD_LINK(2), LINK(FORWARD_LINK,
				BACKWARD_LINK), ALL(NODE, LINK);

		private int m;

		WalkOptions(int bit) {
			m = (1 << bit);
		}

		WalkOptions(WalkOptions... es) {
			m = 0;
			for (WalkOptions e : es) {
				m |= e.getMask();
			}
		}

		int getMask() {
			return m;
		}

		boolean match(WalkOptions opt) {
			return opt != null && (m & opt.m) != 0;
		}

		boolean equals(WalkOptions opt) {
			return opt != null && opt.m == m;

		}

		public boolean matchAny(WalkOptions... opt) {
			if (opt != null)
				for (WalkOptions o : opt)
					if (match(o))
						return true;
			return false;
		}
	}

	NGraph<TAttach> graph;
	private Collection<NNode<TAttach>> startNodes;
	private WalkOptions options;

	public NodeSpreadWalker(NGraph<TAttach> graph,
			Collection<NNode<TAttach>> startNodes,
			WalkOptions options) {
		if (graph == null)
			throw new IllegalArgumentException("graph is null");
		if (startNodes == null)
			throw new IllegalArgumentException("startNodes is null");

		this.graph = graph;
		this.startNodes = startNodes;
		this.options = options;
	}

	public NodeSpreadWalker(NGraph<TAttach> graph,
			WalkOptions options) {
		this(graph, graph.getEntries(), options);
	}

	private static class EmptyNodeSpreadIterator<TAttach>
			implements Iterator<NGraphElem<TAttach>> {

		public boolean hasNext() {
			return false;
		}

		public NGraphElem<TAttach> next() {
			throw new UnsupportedOperationException("empty iterator");
		}

	}

	/**
	 * 不管任何条件，只管完成遍历，如果要设置过滤条件，用外部包装的Iterator去实现。
	 * 
	 * @author jjb
	 *
	 */
	private class NodeSpreadIterator implements
			Iterator<NGraphElem<TAttach>> {

		/**
		 * 存放等待遍历自身和子元素的所有元素
		 */
		List<NNode<TAttach>> walked;
		// 当前遍历到的元素
		int currentWalk = 0;
		// 指示当前元素本身是否已经被遍历，null表示没有
		NNode<TAttach> currentNode = null;
		/**
		 * 当前currentNode出入连接的遍历器
		 */
		private Iterator<NLink<TAttach>> linkIter = null;
		boolean isForward = false; // 是否对forward遍历？

		private NodeSpreadIterator() {
			Collection<NNode<TAttach>> allNodes = NodeSpreadWalker.this.graph
					.getAllNodes();
			walked = new ArrayList<NNode<TAttach>>(allNodes.size());
			walked.addAll(startNodes);
		}

		public boolean hasNext() {
			if (currentNode != null && currentWalk + 1 == walked.size()
					&& !isForward && linkIter != null && !linkIter.hasNext())
				return false;
			return (currentNode != null) || currentWalk < walked.size()
					|| (linkIter != null && linkIter.hasNext());
		}

		public boolean isForwardLink(){return isForward;}
		
		/**
		 * 算法： 先遍历所有的startNodes，每遍历一个start
		 * node，就把这个node加入到walked列表中，直到startNodes遍历完成。
		 * 所有自身遍历完成的节点都放到walked中，然后进行二次遍历
		 * ，这次遍历的是已有节点的子节点，遍历完毕之后也将节点放入walked，如此循环，直到全部完成。
		 */
		public NGraphElem<TAttach> next() {
			while (currentWalk < walked.size()) {
				// 当前节点为null，表示尚未遍历currentWalk位置的节点
				if (currentNode == null) {
					currentNode = walked.get(currentWalk);
					linkIter = null;
					isForward = false;
					return currentNode;
				}
				// 遍历currentNode节点的link，先遍历forward link
				if (linkIter == null) {
					linkIter = currentNode.getForwardLinks().iterator();
					isForward = true;
				}

				while (linkIter != null) {
					if (linkIter.hasNext()) { // 如果没有遍历完，先将另外一头的node加入到walked中
						NLink<TAttach> link = linkIter.next();
						NNode<TAttach> node = isForward ? link
								.getDestination() : link.getSource();
						if (!walked.contains(node)) // 不会重复添加
							walked.add(node);
						return link;
					}
					// 如果link遍历完毕了
					if (isForward) { // 如果backward还没完成的话
						linkIter = currentNode.getBackwardLinks().iterator();
						isForward = false;
					} else
						linkIter = null; // 重置linkIter为null，表示link全部遍历完成
				}
				// 如果到这里还没有完成一个有效的遍历，则开始下一个遍历
				currentWalk++;
				currentNode = null;
			}

			throw new NoSuchElementException();
		}

	}

	/**
	 * 带过滤条件的
	 * @author jjb
	 *
	 */
	private class NodeSpreadFiltedIterator implements Iterator<NGraphElem<TAttach>> {

		private NodeSpreadWalker<TAttach>.NodeSpreadIterator it;
		boolean foundNext = false;
		NGraphElem<TAttach> currentValue = null;

		public NodeSpreadFiltedIterator() {
			it = new NodeSpreadIterator();
		}

		public boolean hasNext() {
			while(!foundNext && it.hasNext()){
				NGraphElem<TAttach> value = it.next();
				foundNext = match(value, it.isForwardLink());
				if( foundNext ){
					currentValue = value;
					return true;
				}
			}
			return foundNext;
		}

		private boolean match(NGraphElem<TAttach> value, boolean isForward) {
			if( value instanceof NNode && NodeSpreadWalker.this.options.match(WalkOptions.NODE))
				return true;
			if( value instanceof NLink && isForward && NodeSpreadWalker.this.options.match(WalkOptions.FORWARD_LINK))
				return true;
			if( value instanceof NLink && !isForward && NodeSpreadWalker.this.options.match(WalkOptions.BACKWARD_LINK))
				return true;
			
			return false;
		}

		public NGraphElem<TAttach> next() {
			if( !foundNext )
				hasNext();
			if( foundNext ){
				foundNext = false;
				return currentValue;
			}
			throw new NoSuchElementException();
		}
		
	}
	
	public Iterator<NGraphElem<TAttach>> iterator() {
		return this.startNodes.size() == 0 ? new EmptyNodeSpreadIterator<TAttach>()
				: new NodeSpreadFiltedIterator();
	}

}
