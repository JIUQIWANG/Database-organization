package hw3;

import java.util.ArrayList;

import hw1.Field;

public interface Node {

	public int getDegree();

	public boolean isLeafNode();

	public boolean isFull();

	public int overHalf();

	public Field getKey();
	public ArrayList<Field> getKeys();

	public void setParent(InnerNode n);

	public InnerNode getParent();
	

}
