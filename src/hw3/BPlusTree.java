package hw3;

import java.lang.annotation.Target;
import java.util.Comparator;

import hw1.Field;

public class BPlusTree {
	private int pInner;
	private int pLeaf;

	private Node root;

	public BPlusTree(int pInner, int pLeaf) {
		// your code here
		this.pInner = pInner;
		this.pLeaf = pLeaf;
		this.root = new LeafNode(pLeaf); // init to a leaf node

	}

	public LeafNode search(Field f) {
		// your code here
		return tree_search(this.root, f);

	}

	private LeafNode tree_search(Node node, Field f) {

		while (!node.isLeafNode()) {
			node = ((InnerNode) node).getChild(f);
		}

		return ((LeafNode) node).containsKey(f) ? ((LeafNode) node) : null;

	}

	public void insert(Entry e) {
		// your code here

		tree_insert(this.root, e);

		// if the root needs to be split
		if (this.root.isFull()) {
			splitHelper(this.root, null);
		}
		if (!this.root.isLeafNode()) {
			System.out.println(((InnerNode) this.root).getKeys().toString());
		}

	}

	private void tree_insert(Node node, Entry e) {

		// node is a leaf node
		if (node.isLeafNode()) {
			((LeafNode) node).addEntry(e);

		} else { // node is inner node or leaf node without space

			InnerNode in = (InnerNode) node;
			Node child = in.getChild(e.getField());

			tree_insert(child, e);

			// split
			if (child.isFull())
				splitHelper(child, in);
			// update keys
			in.updateKeys();
		}

	}

	private void splitHelper(Node node, InnerNode parent) {

		Node newNode = node.isLeafNode() ? ((LeafNode) node).split() : ((InnerNode) node).split();

		if (parent == null) {

			InnerNode in = new InnerNode(this.pInner);
			in.addChild(node);
			in.addChild(newNode);
			node.setParent(in);
			newNode.setParent(in);
			this.root = in;

		} else {
			parent.addChild(newNode);
			newNode.setParent(parent);
		}

	}

	public void delete(Entry e) {
		if (root.isLeafNode()) {
			LeafNode rootL = (LeafNode) this.root;
			if (rootL.containsKey(e.getField())) {
				rootL.removeEntry(e);
				if (rootL.getEntries().size() == 0) {
					this.root = null;
				}
			}
			return;
		}
		LeafNode targetNode = search(e.getField());
		if (targetNode == null) {
			return;
		}
		if (targetNode.overHalf() == 1) {
			targetNode.removeEntry(e);
		} else {
			targetNode.removeEntry(e);
			InnerNode parent = targetNode.getParent();
			Node leftAttempt = parent.getLeftSibling(targetNode);
			if (leftAttempt != null) {
				if (leftAttempt.overHalf() == 1) {
					LeafNode leftLeaf = (LeafNode) leftAttempt;
					targetNode.addEntry(leftLeaf.getLastEntry());
					leftLeaf.removeEntry(leftLeaf.getLastEntry());
					parent.updateKeys();
					return;
				}
			}
			Node rightAttemp = parent.getRightSibling(targetNode);
			if (rightAttemp != null) {
				if (rightAttemp.overHalf() == 1) {
					System.out.println(((LeafNode) rightAttemp).getParent().getKeys().toString());
					LeafNode rightLeaf = (LeafNode) rightAttemp;
					targetNode.addEntry(rightLeaf.getLastEntry());
					rightLeaf.removeEntry(rightLeaf.getLastEntry());
					parent.updateKeys();
					return;
				}
			}
			if (leftAttempt != null) {

				LeafNode leftLeaf = (LeafNode) leftAttempt;
				for (Entry var : targetNode.getEntries()) {
					leftLeaf.addEntry(var);
				}
				parent.removeChild(targetNode);
				parent.updateKeys();
				if (parent.getKeys().size() == 0 && this.root == parent) {
					this.root = leftLeaf;
					return;
				}
				while (parent != null) {
					mergeWithSibling(parent);
					parent = parent.getParent();
				}
				return;
			}
			if (rightAttemp != null) {
				LeafNode rightLeaf = (LeafNode) rightAttemp;
				for (Entry var : rightLeaf.getEntries()) {
					targetNode.addEntry(var);
				}
				parent.removeChild(rightLeaf);
				parent.updateKeys();
				if (parent.getKeys().size() == 0 && this.root == parent) {
					this.root = rightLeaf;
					return;
				}
				while (parent != null) {
					mergeWithSibling(parent);
					parent = parent.getParent();
				}
				return;
			}

		}
	}

	public void mergeWithSibling(InnerNode targetNode) {
		if (targetNode == this.root) {
			return;
		}
		InnerNode parent = targetNode.getParent();
		Node leftAttempt = parent.getLeftSibling(targetNode);
		if (leftAttempt != null) {
			if (leftAttempt.overHalf() == -1) {
				InnerNode leftLeaf = (InnerNode) leftAttempt;
				for (Node var : targetNode.getChildren())
					leftLeaf.addChild(var);
				leftLeaf.updateKeys();
				parent.removeChild(targetNode);
				parent.updateKeys();
				return;
			}
		}
		Node rightAttemp = parent.getRightSibling(targetNode);
		if (rightAttemp != null) {
			if (rightAttemp.overHalf() == -1) {
				InnerNode rightLeaf = (InnerNode) rightAttemp;
				for (Node var : rightLeaf.getChildren())
					targetNode.addChild(var);
				rightLeaf.updateKeys();
				parent.removeChild(rightLeaf);
				parent.updateKeys();
				return;
			}
		}
		return;
	}

	public Node getRoot() {
		return this.root;
	}
}
