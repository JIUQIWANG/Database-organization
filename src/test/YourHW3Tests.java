package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;
import hw3.LeafNode;
import hw3.Node;

public class YourHW3Tests {

	@Test
	public void testSplitLeafInsert() {
		//create a tree, insert a bunch of values
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(8), 0));
		bt.insert(new Entry(new IntField(5), 0));
		bt.insert(new Entry(new IntField(1), 0));

		//verify root properties
		Node root = bt.getRoot();
		assertTrue(root.isLeafNode() == false);

		InnerNode in = (InnerNode)root;

		ArrayList<Field> k = in.getKeys();
		ArrayList<Node> c = in.getChildren();
		
		/**
		 * test leaf node of previous 5 and 8 is split
		 * 5 is pushed up to root
		 * 
		 */
		assertTrue(k.get(0).compare(RelationalOperator.EQ, new IntField(5)));

		//get left node's children, verify
		Node l = c.get(0);
		Node r = c.get(1);

		assertTrue(l.isLeafNode());
		assertTrue(r.isLeafNode());

		LeafNode ll = (LeafNode)l;
		LeafNode rr = (LeafNode)r;

		ArrayList<Entry> el = ll.getEntries();
		ArrayList<Entry> er = rr.getEntries();

		assertTrue(el.get(0).getField().equals(new IntField(1)));
		assertTrue(el.get(1).getField().equals(new IntField(5)));

		assertTrue(er.get(0).getField().equals(new IntField(8)));

	}
	
	
	@Test
	public void testRebalanceDelete() {
		//create a tree, insert a bunch of values
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(8), 0));
		bt.insert(new Entry(new IntField(5), 0));
		bt.insert(new Entry(new IntField(1), 0));
		bt.insert(new Entry(new IntField(7), 0));
		bt.insert(new Entry(new IntField(3), 0));
		bt.insert(new Entry(new IntField(12), 0));
		bt.insert(new Entry(new IntField(9), 0));
		bt.insert(new Entry(new IntField(6), 0));

		bt.delete(new Entry(new IntField(3), 0));
		bt.delete(new Entry(new IntField(5), 0));
		bt.delete(new Entry(new IntField(12), 0));
		
		//verify root properties
		Node root = bt.getRoot();

		assertTrue(root.isLeafNode() == false);

		InnerNode in = (InnerNode)root;

		ArrayList<Field> k = in.getKeys();
		ArrayList<Node> c = in.getChildren();

		assertTrue(k.get(0).compare(RelationalOperator.EQ, new IntField(7)));

		//grab left and right children from root
		InnerNode l = (InnerNode)c.get(0);
		InnerNode r = (InnerNode)c.get(1);

		assertTrue(l.isLeafNode() == false);
		assertTrue(r.isLeafNode() == false);

		//check values in left node
		ArrayList<Field> kl = l.getKeys();
		ArrayList<Node> cl = l.getChildren();

		assertTrue(kl.get(0).compare(RelationalOperator.EQ, new IntField(1)));
		assertTrue(kl.get(1).compare(RelationalOperator.EQ, new IntField(6)));

		//grab childrens
		LeafNode cl0 = (LeafNode)cl.get(0);
		LeafNode cl1 = (LeafNode)cl.get(1);
		LeafNode cl2 = (LeafNode)cl.get(2);
		
		assertTrue(cl0.isLeafNode());
		assertTrue(cl1.isLeafNode());
		assertTrue(cl2.isLeafNode());
		

		//check values in first node
		ArrayList<Entry> cl0entries = cl0.getEntries();

		assertTrue(cl0entries.get(0).getField().equals(new IntField(1)));

		//check values in second node
		ArrayList<Entry> cl1entries = cl1.getEntries();

		assertTrue(cl1entries.get(0).getField().equals(new IntField(6)));
		
		//check values in third node
		ArrayList<Entry> cl2entries = cl2.getEntries();

		assertTrue(cl2entries.get(0).getField().equals(new IntField(7)));
		
		//check values in right node
		ArrayList<Field> kr = r.getKeys();
		ArrayList<Node> cr = r.getChildren();

		assertTrue(kr.get(0).compare(RelationalOperator.EQ, new IntField(9)));

		//grab childrens 
		LeafNode cr0 = (LeafNode)cr.get(0);
		LeafNode cr1 = (LeafNode)cr.get(1);
		assertTrue(cr0.isLeafNode());
		assertTrue(cr1.isLeafNode());
		

		//check values in first node
		ArrayList<Entry> cr0entries = cr0.getEntries();

		assertTrue(cr0entries.get(0).getField().equals(new IntField(8)));

		//check values in second node
		ArrayList<Entry> cr1entries = cr1.getEntries();

		assertTrue(cr1entries.get(0).getField().equals(new IntField(9)));
		
			
	}
	
	
	

}
