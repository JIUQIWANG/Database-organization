package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class LeafNode implements Node {
	private int degree;
	private ArrayList<Entry> entries;
	private InnerNode parent;
	// implement leaf nodes as a linked list for convenient implement
	private LeafNode prev;
	private LeafNode next;

	public LeafNode(int degree) {
		// your code here
		this.degree = degree;
		entries = new ArrayList<>();

		this.prev = null;
		this.next = null;

	}

	public void setParent(InnerNode p) {
		parent = p;
	}

	public InnerNode getParent() {
		return parent;
	}

	public ArrayList<Entry> getEntries() {
		// your code here
		return this.entries;
	}

	public int getDegree() {
		// your code here
		return this.degree;
	}

	public boolean isLeafNode() {
		return true;
	}

	/*
	 * your code here
	 */

	public boolean isFull() {

		return !(this.entries.size() <= this.degree);
	}

	public int overHalf() {
		int mid = (this.degree % 2 == 0) ? (this.degree / 2) : (this.degree / 2 + 1);
		int len = this.entries.size();
		if (len < mid) {
			return -1;
		} else if (len == mid) {
			return 0;
		} else {
			return 1;
		}
	}

	public void setEntries(ArrayList<Entry> entries) {
		this.entries = entries;
	}
	
	public void addEntry(Entry e) {

		// keep increasing order

		for (int i = 0; i < this.entries.size(); i++) {
			if (this.entries.get(i).getField().compare(RelationalOperator.EQ, e.getField()))
				return;
			if (this.entries.get(i).getField().compare(RelationalOperator.GT, e.getField())) {
				this.entries.add(i, e);
				return;
			}
		}

		// add to the end
		this.entries.add(e);
	}

	public void removeEntry(Entry e) {

		for (int i = 0; i < this.entries.size(); i++) {
			if (e.getField().compare(RelationalOperator.EQ, this.entries.get(i).getField()))
				this.entries.remove(i);

		}
	}

	public Entry getFirstEntry() {
		return this.entries.get(0);
	}

	public Entry getLastEntry() {
		return this.entries.get(this.entries.size() - 1);
	}

	public Field getKey() { // return itself
		return this.getLastEntry().getField();
	}

	public ArrayList<Field> getKeys() {
		// your code here
		ArrayList<Field> keys = new ArrayList<>();
		for (Entry entry: this.entries) {
			keys.add(entry.getField());
		}
		return keys;
	}
	
	public boolean containsKey(Field f) {

		for (Entry entry : this.entries) {
			if (f.compare(RelationalOperator.EQ, entry.getField())) {
				return true;
			}
		}
		return false;
	}

	public LeafNode split() {
		LeafNode ln = new LeafNode(this.degree);

		// // if even, left one more than right;
		int len = this.entries.size();

		int mid = (len % 2 == 0) ? (len / 2) : (len / 2 + 1);

		ArrayList<Entry> lEntry = new ArrayList<>(this.entries.subList(0, mid));
		ArrayList<Entry> rEntry = new ArrayList<>(this.entries.subList(mid, len));

		this.setEntries(lEntry);
		ln.setEntries(rEntry);

		return ln;
	}

}
