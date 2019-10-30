package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.TupleDesc;
import hw4.BufferPool;
import hw4.Permissions;
import hw1.Tuple;
import hw1.StringField;
import hw1.IntField;

public class YourUnitTests {

	private HeapFile hf;
	private TupleDesc td;
	private Catalog c;
	private HeapPage hp;
	private BufferPool bp;
	private int tableId;

	@Before
	public void setup() {

		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}

		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");

		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		hp = hf.readPage(0);

		bp = Database.getBufferPool();

	}

	@Test
	public void testRemovePage() {
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] { 0, 0, 0, (byte) 131 }));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		// fill out current page
		for (int i = 0; i < 29; i++) {
			hf.addTuple(t);
		}
		Tuple t1 = new Tuple(td);
		t1.setField(0, new IntField(new byte[] { 0, 0, 0, (byte) 131 }));
		byte[] s1 = new byte[129];
		s1[0] = 2;
		s1[1] = 98;
		s1[2] = 121;
		t.setField(1, new StringField(s1));
		// add a new page
		hf.addTuple(t1);
		assertTrue(hf.getNumPages() == 2);
		hf.deleteTuple(t1);
		assertTrue(hf.getNumPages() == 1);
	}

	@Test
	public void testTupleGetter() {
		// create new tuple
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] { 0, 0, 0, (byte) 131 }));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		// add a new page

		// assertTrue(t.getField(1).toString().compareTo(testField.toString()) == 0);
	}

	// examine the methods in bufferpool
	@Test
	public void testGetPage() throws Exception {
		/**
		 * two cases: structure to track the locks: read/write locks are different only
		 * one write but multiple read
		 */

		// test multiple read
		bp.getPage(0, tableId, 0, Permissions.READ_ONLY); // one txn read
		bp.getPage(1, tableId, 0, Permissions.READ_ONLY); // another txn read
		assertTrue(true); // assert true

		// test one write
		bp.getPage(0, tableId, 0, Permissions.READ_WRITE); // one txn write
		bp.getPage(1, tableId, 0, Permissions.READ_WRITE); // another txn write
		assertTrue(false); // assert false

	}

	@Test
	public void testRelease() throws Exception {
		// one possible case
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] { 0, 0, 0, (byte) 131 }));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));

		bp.getPage(0, tableId, 0, Permissions.READ_WRITE);
		bp.insertTuple(0, tableId, t); // insert the tuple into the page
		bp.transactionComplete(0, true); // txn should complete

		bp.releasePage(0, tableId, 0);

		// remove any key after release
		assertTrue(bp.holdsLock(0, tableId, 0) == false);
	}

	@Test
	public void testGetPagesWithExistedWriteLock() throws Exception {
		// test two transcation acuiqre same page
		// two transcation both acquire read access
		// read
		bp.getPage(0, tableId, 0, Permissions.READ_ONLY);
		bp.getPage(1, tableId, 0, Permissions.READ_ONLY);
		assertTrue(true);
		assertTrue("should hold read lock", bp.holdsLock(0, tableId, 0));
		assertTrue("should hold read lock", bp.holdsLock(1, tableId, 0));

		bp.transactionComplete(0, true);
		bp.transactionComplete(1, true);
		assertTrue(true);

		// normal write lock release and get
		bp.getPage(0, tableId, 0, Permissions.READ_WRITE);
		bp.transactionComplete(0, true);
		bp.getPage(1, tableId, 0, Permissions.READ_WRITE);
		bp.transactionComplete(1, true);
		assertTrue(true);

		// get page with existed write locks
		try {
			bp.getPage(0, tableId, 0, Permissions.READ_WRITE);
			assertTrue("should hold lock", bp.holdsLock(0, tableId, 0));
			bp.getPage(1, tableId, 0, Permissions.READ_WRITE);

		} catch (Exception e) {
			assertTrue(true);
			bp.transactionComplete(0, true);
			bp.transactionComplete(1, true);
		}
		fail("Should have throw an exception");

	}

}
