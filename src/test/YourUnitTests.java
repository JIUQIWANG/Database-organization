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
import hw1.Tuple;
import hw1.StringField;
import hw1.IntField;

public class YourUnitTests {

	private HeapFile hf;
	private TupleDesc td;
	private Catalog c;
	private HeapPage hp;

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
		StringField testField = new StringField(s);
		t.setField(1, testField);

		// make sure getting result is same to original data
		assertTrue(t.getField(1).toString().compareTo(testField.toString()) == 0);
	}

}
