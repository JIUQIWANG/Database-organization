package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	// private boolean isRoot;
	private int degree;
	private ArrayList<Field> keys;
	private ArrayList<Node> children;
	private InnerNode parent;

	public InnerNode(int degree) {
		// your code here
		this.degree = degree; // max num of children
		// this.isRoot = false;

		keys = new ArrayList<>();
		children = new ArrayList<>();

	}

	public void setParent(InnerNode p) {
		parent = p;
	}

	public InnerNode getParent() {
		return parent;
	}

	public ArrayList<Field> getKeys() {
		// your code here
		return this.keys;
	}

	public ArrayList<Node> getChildren() {
		// your code here
		return this.children;
	}

	public int getDegree() {
		// your code here
		return this.degree;
	}

	public boolean isLeafNode() {
		return false;
	}

	/**
	 * your code here
	 */
	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public void addKey(Field f) {
		this.keys.add(f);
	}

	public void removeChild(Node node) {
		this.children.remove(this.children.indexOf(node));
		this.updateKeys();
	}

	public boolean isFull() {
		return !(this.keys.size() < this.degree);
	}

	public int overHalf() {
		int mid = (this.degree % 2 == 0) ? (this.degree / 2) : (this.degree / 2 + 1);
		int len = this.keys.size();

		if (len < mid) {
			return -1;
		} else if (len == mid) {
			return 0;
		} else {
			return 1;
		}
	}

	public boolean belowHalfSize() {
		int mid = (this.degree % 2 == 0) ? (this.degree / 2) : (this.degree / 2 + 1);

		return !(this.keys.size() >= mid);
	}
	// public Capacity checkCapacity() {
	// int halfSize = (int)Math.ceil(degree/2.0);
	// int currentSize = keys.size();
	//
	// if(currentSize < halfSize) {
	// return Capacity.UNDER_HALF;
	// }else if(currentSize == halfSize) {
	// return Capacity.HALF;
	// }else if(currentSize <= degree) {
	// return Capacity.ABOVE_HALF;
	// }else {
	// return Capacity.OVERSIZE;
	// }
	// }

	// public void setRoot(boolean isRoot) {
	// this.isRoot = isRoot;
	// }
	//
	// public boolean isRoot() {
	// return this.isRoot;
	// }

	public Field getKey() {
		return this.children.get(this.children.size() - 1).getKey();
	}

	public Node getChild(Field f) {

		for (int i = 0; i < this.keys.size(); i++) {
			if (this.keys.get(i).compare(RelationalOperator.GTE, f)) {
				return this.children.get(i);
			}
		}

		// else return the last child
		return this.children.get(this.children.size() - 1);
	}

	public void updateKeys() {
		ArrayList<Field> newKeys = new ArrayList<>();

		for (int i = 0; i < this.children.size() - 1; i++) {
			newKeys.add(this.children.get(i).getKey());
		}

		this.keys = newKeys;
	}

	public void updateChildren(ArrayList<Node> children) {
		this.children = children;

		updateKeys();
	}

	public void addChild(Node node) {
		Field key = node.getKey();

		for (int i = 0; i < this.children.size(); i++) {
			if (this.children.get(i).getKey().compare(RelationalOperator.GTE, key)) {
				this.children.add(i, node);
				updateKeys();
				return;
			}
		}

		this.children.add(node);
		updateKeys();
	}

	public InnerNode split() {

		InnerNode in = new InnerNode(this.degree);
		// if even, left one more than right;
		int len = this.children.size();
		int mid = (len % 2 == 0) ? (len / 2) : (len / 2 + 1);

		ArrayList<Node> lChild = new ArrayList<>(this.children.subList(0, mid));
		ArrayList<Node> rChild = new ArrayList<>(this.children.subList(mid, len));

		this.updateChildren(lChild);
		in.updateChildren(rChild);

		return in;

	}

	public Node getLastChild() {
		return children.get(children.size() - 1);
	}

	public Node getFirstChild() {
		return children.get(0);
	}

	// get sibling of one child
	public Node getRightSibling(Node child) {
		// get this child's index
		int index = children.indexOf(child);

		// check if it has right sibling
		if (index + 1 < children.size()) {
			return children.get(index + 1);
		} else {
			return null;
		}
	}

	public Node getLeftSibling(Node child) {
		// get this child's index
		int index = children.indexOf(child);

		// check if it has left sibling
		if (index <= 0) {
			return null;
		} else {
			return children.get(index - 1);
		}
	}

}