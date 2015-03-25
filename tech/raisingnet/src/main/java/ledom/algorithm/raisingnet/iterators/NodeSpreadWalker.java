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

public class NodeSpreadWalker<TNodeAttach, TLinkAttach> implements
		Iterable<NGraphElem<TNodeAttach, TLinkAttach>> {

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

	NGraph<TNodeAttach, TLinkAttach> graph;
	private Collection<NNode<TNodeAttach, TLinkAttach>> startNodes;
	private WalkOptions options;

	public NodeSpreadWalker(NGraph<TNodeAttach, TLinkAttach> graph,
			Collection<NNode<TNodeAttach, TLinkAttach>> startNodes,
			WalkOptions options) {
		if (graph == null)
			throw new IllegalArgumentException("graph is null");
		if (startNodes == null)
			throw new IllegalArgumentException("startNodes is null");

		this.graph = graph;
		this.startNodes = startNodes;
		this.options = options;
	}

	public NodeSpreadWalker(NGraph<TNodeAttach, TLinkAttach> graph,
			WalkOptions options) {
		this(graph, graph.getEntries(), options);
	}

	private static class EmptyNodeSpreadIterator<TNodeAttach, TLinkAttach>
			implements Iterator<NGraphElem<TNodeAttach, TLinkAttach>> {

		public boolean hasNext() {
			return false;
		}

		public NGraphElem<TNodeAttach, TLinkAttach> next() {
			throw new UnsupportedOperationException("empty iterator");
		}

	}

	/**
	 * �����κ�������ֻ����ɱ��������Ҫ���ù������������ⲿ��װ��Iteratorȥʵ�֡�
	 * 
	 * @author jjb
	 *
	 */
	private class NodeSpreadIterator implements
			Iterator<NGraphElem<TNodeAttach, TLinkAttach>> {

		/**
		 * ��ŵȴ������������Ԫ�ص�����Ԫ��
		 */
		List<NNode<TNodeAttach, TLinkAttach>> walked;
		// ��ǰ��������Ԫ��
		int currentWalk = 0;
		// ָʾ��ǰԪ�ر����Ƿ��Ѿ���������null��ʾû��
		NNode<TNodeAttach, TLinkAttach> currentNode = null;
		/**
		 * ��ǰcurrentNode�������ӵı�����
		 */
		private Iterator<NLink<TNodeAttach, TLinkAttach>> linkIter = null;
		boolean isForward = false; // �Ƿ��forward������

		private NodeSpreadIterator() {
			Collection<NNode<TNodeAttach, TLinkAttach>> allNodes = NodeSpreadWalker.this.graph
					.getAllNodes();
			walked = new ArrayList<NNode<TNodeAttach, TLinkAttach>>(allNodes.size());
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
		 * �㷨�� �ȱ������е�startNodes��ÿ����һ��start
		 * node���Ͱ����node���뵽walked�б��У�ֱ��startNodes������ɡ�
		 * �������������ɵĽڵ㶼�ŵ�walked�У�Ȼ����ж��α���
		 * ����α����������нڵ���ӽڵ㣬�������֮��Ҳ���ڵ����walked�����ѭ����ֱ��ȫ����ɡ�
		 */
		public NGraphElem<TNodeAttach, TLinkAttach> next() {
			while (currentWalk < walked.size()) {
				// ��ǰ�ڵ�Ϊnull����ʾ��δ����currentWalkλ�õĽڵ�
				if (currentNode == null) {
					currentNode = walked.get(currentWalk);
					linkIter = null;
					isForward = false;
					return currentNode;
				}
				// ����currentNode�ڵ��link���ȱ���forward link
				if (linkIter == null) {
					linkIter = currentNode.getForwardLinks().iterator();
					isForward = true;
				}

				while (linkIter != null) {
					if (linkIter.hasNext()) { // ���û�б����꣬�Ƚ�����һͷ��node���뵽walked��
						NLink<TNodeAttach, TLinkAttach> link = linkIter.next();
						NNode<TNodeAttach, TLinkAttach> node = isForward ? link
								.getDestination() : link.getSource();
						if (!walked.contains(node)) // �����ظ����
							walked.add(node);
						return link;
					}
					// ���link���������
					if (isForward) { // ���backward��û��ɵĻ�
						linkIter = currentNode.getBackwardLinks().iterator();
						isForward = false;
					} else
						linkIter = null; // ����linkIterΪnull����ʾlinkȫ���������
				}
				// ��������ﻹû�����һ����Ч�ı�������ʼ��һ������
				currentWalk++;
				currentNode = null;
			}

			throw new NoSuchElementException();
		}

	}

	/**
	 * ������������
	 * @author jjb
	 *
	 */
	private class NodeSpreadFiltedIterator implements Iterator<NGraphElem<TNodeAttach, TLinkAttach>> {

		private NodeSpreadWalker<TNodeAttach, TLinkAttach>.NodeSpreadIterator it;
		boolean foundNext = false;
		NGraphElem<TNodeAttach,TLinkAttach> currentValue = null;

		public NodeSpreadFiltedIterator() {
			it = new NodeSpreadIterator();
		}

		public boolean hasNext() {
			while(!foundNext && it.hasNext()){
				NGraphElem<TNodeAttach,TLinkAttach> value = it.next();
				foundNext = match(value, it.isForwardLink());
				if( foundNext ){
					currentValue = value;
					return true;
				}
			}
			return foundNext;
		}

		private boolean match(NGraphElem<TNodeAttach, TLinkAttach> value, boolean isForward) {
			if( value instanceof NNode && NodeSpreadWalker.this.options.match(WalkOptions.NODE))
				return true;
			if( value instanceof NLink && isForward && NodeSpreadWalker.this.options.match(WalkOptions.FORWARD_LINK))
				return true;
			if( value instanceof NLink && !isForward && NodeSpreadWalker.this.options.match(WalkOptions.BACKWARD_LINK))
				return true;
			
			return false;
		}

		public NGraphElem<TNodeAttach, TLinkAttach> next() {
			if( !foundNext )
				hasNext();
			if( foundNext ){
				foundNext = false;
				return currentValue;
			}
			throw new NoSuchElementException();
		}
		
	}
	
	public Iterator<NGraphElem<TNodeAttach, TLinkAttach>> iterator() {
		return this.startNodes.size() == 0 ? new EmptyNodeSpreadIterator<TNodeAttach, TLinkAttach>()
				: new NodeSpreadFiltedIterator();
	}

}
