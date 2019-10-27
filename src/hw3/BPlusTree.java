package hw3;




import hw1.Field;


public class BPlusTree {
    private int pInner;
    private int pLeaf;
    
    private Node root;
    
    
    public BPlusTree(int pInner, int pLeaf) {
    	//your code here
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    	this.root = new LeafNode(pLeaf); // init to a leaf node
    	
    }
    
    public LeafNode search(Field f) {
    	//your code here
    	return tree_search(this.root,f); 
    	
    }
    
    private	LeafNode tree_search(Node node, Field f) {
    	
    	
    	while(!node.isLeafNode()) {
    		node = ((InnerNode)node).getChild(f);
    	}
    	
    	return ((LeafNode)node).containsKey(f) ? ((LeafNode)node) : null;
    
    }
    
    public void insert(Entry e) {
    	//your code here
    	
		tree_insert(this.root, e);
		
		// if the root needs to be split
		if(this.root.isFull())
    		splitHelper(this.root,null);
    	
    }
    
    
    private void tree_insert(Node node, Entry e) {
    	
    	// node is a leaf node 
    	if (node.isLeafNode()){
			((LeafNode)node).addEntry(e);
    		
    	}else { // node is inner node or leaf node without space
 
    		InnerNode in = (InnerNode)node;
    		Node child = in.getChild(e.getField());

    		tree_insert(child, e);
    		
    		// split
    		if (child.isFull())
    			splitHelper(child,in);

    		// update keys
    		in.updateKeys();
    	}
    
    }
    
    private void splitHelper(Node node, InnerNode parent) {
    	    	
    	Node newNode = node.isLeafNode() ? ((LeafNode)node).split() : ((InnerNode)node).split();
    	
    	if (parent==null) {		
    		InnerNode in = new InnerNode(this.pInner);
    		in.addChild(node);
    		in.addChild(newNode);
    		
    		this.root = in;
    	}else {
			parent.addChild(newNode);
    	}
    	
    }

	public void delete (Entry e) {
		
	}
	
	public Node getRoot() {
		return this.root;
	}
}
