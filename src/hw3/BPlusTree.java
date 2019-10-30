package hw3;

import java.util.ArrayList;
import java.util.Stack;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	private int pInner;
	private int pLeaf;

	private Node root;

	private boolean op; // borrowed or merged
	boolean isBalanced = false;

	public BPlusTree(int pInner, int pLeaf) {
		// your code here
		this.pInner = pInner;
		this.pLeaf = pLeaf;
		this.root = new LeafNode(pLeaf); // init to a leaf node

		this.op = false;
	}

	// >>> search
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

	// >>> search FINISH

	// >>> insert
	public void insert(Entry e) {
		// your code here

		tree_insert(this.root, e);

		// if the root needs to be split
		if (this.root.isFull())
			splitHelper(this.root, null);
		
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

			this.root = in;

			return;
		}

		parent.addChild(newNode);

	}
	// >>> insert FINISH

	// >>> delete
	public void delete(Entry e) {
		ArrayList<Field> oldKeys =  this.root.getKeys();
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
		LeafNode searchNode = this.search(e.getField());

		if (searchNode == null) {
			return;
		}
		if (searchNode.overHalf() > 0) {
			searchNode.removeEntry(e);
			return;
		}
		Stack<Node> st = new Stack<>(); // FILO: store path
		st.push(this.root);
		System.out.println("root"+this.root.getKeys().toString());
		tree_delete(st, e);
		
		if (this.root.isFull()) {
			splitHelper(this.root, null);
		}
		
		ArrayList<Field> newKeys =  this.root.getKeys();

		if (this.op && !this.root.isLeafNode() && newKeys.size() == oldKeys.size()) {
			ArrayList<Node> children = ((InnerNode) this.root).getChildren();
			System.out.println(newKeys.toString());
			for (int i=0; i<oldKeys.size(); i++) {
				if(oldKeys.get(i).compare(RelationalOperator.LT, children.get(i+1).getKeys().get(0))) {
					newKeys.set(i, oldKeys.get(i));
				}
				
			}
			((InnerNode)this.root).setKeys(newKeys);
		}

		
	}

	private void tree_delete(Stack<Node> st, Entry e) {

		Node node = st.peek();

		if (node.isLeafNode()) {
			((LeafNode) node).removeEntry(e);
		} else {
			Node child = ((InnerNode) node).getChild(e.getField());
			st.push(child);
			tree_delete(st, e);
		}

		handleBalance(st);
		st.pop();
		System.out.println("root"+this.root.getKeys().toString());

		if (this.op) {
			if (!this.root.isLeafNode()) {
				InnerNode parent = (InnerNode) this.root;
				parent.updateKeys();
				
			}
		}
	}

	private void handleBalance(Stack<Node> st) {

		// current node
		Node node = st.peek();
		System.out.print("");
		if (this.root == node) {
			
			if (node.isLeafNode()) {
				// corner case: delete root
				if (((LeafNode) this.root).getEntries().size() < 1)
					this.root = null;
				return;
			} else {
				InnerNode in = (InnerNode) this.root;
				if (in.getChildren().size() <= 1)
					this.root = in.getFirstChild();
				return;
			}

		}
		// System.out.println(((InnerNode) ((InnerNode)
		// this.getRoot()).getChildren().get(0)).getKeys().toString());
		// System.out.println(((InnerNode) this.root).getKeys().toString());
		// we don't need to handle if it is overHalf
		if (node.overHalf() < 0) {
			this.op = true;

			st.pop();

			// get the parent node
			InnerNode parent = (InnerNode) st.peek();
			st.push(node);
			// node with the same parent
			Node sibling = this.getLeftSibling(st); // left priority
			sibling = sibling == null ? this.getRightSibling(st) : sibling;

			if (sibling.isLeafNode()) { // handle leaf node
				// borrow

				if (sibling.overHalf() > 0) {

					Entry entryFromLeft = ((LeafNode) sibling).getLastEntry();
					System.out.println("borrow:"+entryFromLeft.getField().toString());
					((LeafNode) node).addEntry(entryFromLeft);
					((LeafNode) sibling).removeEntry(entryFromLeft);

				} else { // merge

					ArrayList<Entry> entries = ((LeafNode) node).getEntries();

					for (Entry entry : entries) {
						((LeafNode) sibling).addEntry(entry);
					}

					parent.removeChild(node);
				}
			} else { // handle inner node
				// borrow
				// System.out
				// .println(((InnerNode) ((InnerNode)
				// this.getRoot()).getChildren().get(0)).getKeys().toString());
				// System.out.println(((InnerNode) this.root).getKeys().toString());
				if (sibling.overHalf() > 0) {

					Node nodeFromLeft = ((InnerNode) sibling).getLastChild();
					((InnerNode) node).addChild(nodeFromLeft);
					((InnerNode) sibling).removeChild(nodeFromLeft);

				} else{ // merge

					ArrayList<Node> children = ((InnerNode) node).getChildren();

					for (Node child : children) {
						((InnerNode) sibling).addChild(child);
					}

					parent.removeChild(node);
				}
	    	}
	    	
    	}else {
    		this.op = false;
    	}
    	
    }
   

	private Node getLeftSibling(Stack<Node> st) {
		@SuppressWarnings("unchecked")
		Stack<Node> path = (Stack<Node>) st.clone();
		Node child = null, sibling = null, temp = null;
		InnerNode parent = null;

		int steps = 0; // represent the steps go up from the curNode
		while (path.size() > 1) {
			child = path.pop();
			parent = (InnerNode) path.peek();
			sibling = parent.getLeftSibling(child); // InnerNode method
			if (sibling != null) {
				temp = sibling;
				break;
			}

			steps++; // go up
		}

		if (temp == null) { // Cannot find the left sibling
			return null;
		}

		sibling = temp;
		for (int i = 0; i < steps; i++) {
			sibling = ((InnerNode) sibling).getLastChild();
		}

		return sibling;
	}

	private Node getRightSibling(Stack<Node> st) {
		@SuppressWarnings("unchecked")
		Stack<Node> path = (Stack<Node>) st.clone();
		Node child = null, sibling = null, temp = null;
		InnerNode parent = null;

		int steps = 0; // represent the steps go up from the curNode
		while (path.size() > 1) {
			child = path.pop();
			parent = (InnerNode) path.peek();
			sibling = parent.getRightSibling(child); // InnerNode method
			if (sibling != null) {
				temp = sibling;
				break;
			}

			steps++; // go up
		}

		if (temp == null) { // Cannot find the left sibling
			return null;
		}

		sibling = temp;
		for (int i = 0; i < steps; i++) {
			sibling = ((InnerNode) sibling).getFirstChild();
		}

		return sibling;
	}

	// >>> delete FINISH

	public Node getRoot() {
		return this.root;
	}
}
