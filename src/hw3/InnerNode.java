package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	// private boolean isRoot;
	private int degree;
	private ArrayList<Field> keys;
	private ArrayList<Node> children;

	public InnerNode(int degree) {
		// your code here
		this.degree = degree; // max num of children

		keys = new ArrayList<>();
		children = new ArrayList<>();

	}

	public void setKeys(ArrayList<Field> keys){
		this.keys = keys;
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

	public void removeKey(Field f) {
		this.keys.remove(f);
	}

	public void removeChild(Node node) {
		this.children.remove(this.children.indexOf(node));
		this.updateKeys(); //always remember to update keys
	}

	public boolean isFull() {
		return !(this.keys.size() < this.degree);
	}

	public int overHalf() {
		int mid = (this.degree % 2 == 0) ? (this.degree / 2) : (this.degree / 2 + 1);
		int len = this.keys.size() - 1;

		if (len < mid) {
			return -1;
		} else if (len == mid) {
			return 0;
		} else {
			return 1;
		}
	}

	public Field getKey() {
		return this.children.get(this.children.size() - 1).getKey();
	}

	public Node getChild(Field f) {

		for (int i = 0; i < this.keys.size(); i++) {
			if (f.compare(RelationalOperator.LTE, this.keys.get(i))) {
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
		updateKeys();//always remember to update keys

	}

	public void addChild(Node node) {
		Field key = node.getKey();

		for (int i = 0; i < this.children.size(); i++) {
			if (key.compare(RelationalOperator.LTE, this.children.get(i).getKey())) {
				this.children.add(i, node);
				updateKeys();//always remember to update keys
				return;
			}
		}

		this.children.add(node);
		updateKeys(); //always remember to update keys
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

	public Node getFirstChild() {
		return children.get(0);
	}

	public Node getLastChild() {
		return children.get(children.size() - 1);
	}

	// get sibling of one child: left priority
	public Node getLeftSibling(Node child) {
		// get this child's index
		int index = this.children.indexOf(child);

		// check left sibling
		return index>0 ? this.children.get(index - 1) : null;

	}
	
	public Node getRightSibling(Node child) {
		// get this child's index
		int index = this.children.indexOf(child);

		// check right sibling
		return index < this.children.size() -1 ? this.children.get(index + 1) : null;

	}



}