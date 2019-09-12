package hw1;

import java.io.File;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.experimental.theories.FromDataPoints;

/**
 * This class represents a tuple that will contain a single row's worth of
 * information from a table. It also includes information about where it is
 * stored
 * 
 * @author Sam Madden modified by Doug Shook
 *
 */
public class Tuple {

	/**
	 * Creates a new tuple with the given description
	 * 
	 * @param t the schema for this tuple
	 */
	private TupleDesc schema;
	private int pageID = 0;
	private int slotID = 0;
	private Map<Integer, Field> mp;

	public Tuple(TupleDesc t) {
		// your code here
		this.schema = t;
		mp = new HashMap<>();
	}

	public TupleDesc getDesc() {
		// your code here
		return this.schema;
	}

	/**
	 * retrieves the page id where this tuple is stored
	 * 
	 * @return the page id of this tuple
	 */
	public int getPid() {
		// your code here
		return this.pageID;
	}

	public void setPid(int pid) {
		// your code here
		pageID = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * 
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		// your code here
		return slotID;
	}

	public void setId(int id) {
		// your code here
		slotID = id;
	}

	public void setDesc(TupleDesc td) {
		// your code here;
		schema = td;
	}

	/**
	 * Stores the given data at the i-th field
	 * 
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		// your code here
		mp.put(schema.getFieldName(i).hashCode(), v);
	}

	public Field getField(int i) {
		// your code here
		return mp.get(schema.getFieldName(i).hashCode());
	}

	/**
	 * Creates a string representation of this tuple that displays its contents. You
	 * should convert the binary data into a readable format (i.e. display the ints
	 * in base-10 and convert the String columns to readable text).
	 */
	public String toString() {
		// your code here
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < schema.numFields(); i++) {

			sb.append(getField(i).toString());

		}
		return sb.toString();
	}
}
