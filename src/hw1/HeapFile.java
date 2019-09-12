package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A heap file stores a collection of tuples. It is also responsible for
 * managing pages. It needs to be able to manage page creation as well as
 * correctly manipulating pages when tuples are added or deleted.
 * 
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {

	public static final int PAGE_SIZE = 4096;
	private int id;
	private File heapFile;
	private TupleDesc tupleType;

	/**
	 * Creates a new heap file in the given location that can accept tuples of the
	 * given type
	 * 
	 * @param f     location of the heap file
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		// your code here
		heapFile = f;
		id = getFile().hashCode();
		tupleType = type;
	}

	public File getFile() {
		// your code here
		return heapFile;
	}

	public TupleDesc getTupleDesc() {
		// your code here
		return tupleType;
	}

	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a
	 * RandomAccessFile object should be used here.
	 * 
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		// your code here
		RandomAccessFile ra;
		HeapPage hp;
		try {
			ra = new RandomAccessFile(heapFile, "r");
			ra.seek(id * PAGE_SIZE);
			byte[] data = new byte[PAGE_SIZE];
			ra.read(data);
			hp = new HeapPage(id, data, this.id);
			ra.close();
			return hp;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a unique id number for this heap file. Consider using the hash of the
	 * File itself.
	 * 
	 * @return
	 */
	public int getId() {
		// your code here
		return getFile().hashCode();
	}

	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the
	 * file, a RandomAccessFile object should be used in this method.
	 * 
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		// your code here
		try {
			RandomAccessFile ra = new RandomAccessFile(heapFile, "rw");
			ra.seek(p.getId() * PAGE_SIZE);
			ra.write(p.getPageData());
			ra.close();
			return;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating
	 * a new page if all others are full. It then passes the tuple to this page to
	 * be stored. It then writes the page to disk (see writePage)
	 * 
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		// your code here
		try {

			for (int i = 0; i < getNumPages(); i++) {
				HeapPage temp = readPage(i);

				if (isEmptySlot(temp) == true) {

					temp.addTuple(t);
					writePage(temp);
					return temp;
				}
			}

			HeapPage ne = new HeapPage(getNumPages(), new byte[PAGE_SIZE], getId());
			ne.addTuple(t);
			writePage(ne);
			return ne;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isEmptySlot(HeapPage hp) {
		for (int i = 0; i < hp.getNumSlots(); i++) {
			if (hp.slotOccupied(i) == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will examine the tuple to find out where it is stored, then
	 * delete it from the proper HeapPage. It then writes the modified page to disk.
	 * 
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t) {
		// your code here
		try {
			HeapPage temp = readPage(t.getPid());
			temp.deleteTuple(t);
			writePage(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * 
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		// your code here
		ArrayList<Tuple> result = new ArrayList<>();
		for (int i = 0; i < getNumPages(); i++) {
			Iterator<Tuple> temp = readPage(i).iterator();
			while (temp.hasNext()) {
				result.add(temp.next());
			}
		}
		return result;
	}

	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * 
	 * @return the number of pages
	 */
	public int getNumPages() {
		// your code here
		long len = heapFile.length();
		return (int) len / PAGE_SIZE;
	}
}
