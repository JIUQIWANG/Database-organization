package hw2;

import java.util.ArrayList;
import java.util.*;
import java.lang.*;
import java.rmi.UnexpectedException;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will
 * be used to implement SQL queries.
 * 
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;

	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		// your code here
		/**
		 * Initiate
		 */
		tuples = l;
		this.td = td;
	}

	/**
	 * This method performs a select operation on a relation
	 * 
	 * @param field   number (refer to TupleDesc) of the field to be compared, left
	 *                side of comparison
	 * @param op      the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		// your code here
		/**
		 * Implement WHERE clause: Select operations will all take the form: column
		 * (operation) constant. An example WHERE clause might be WHERE a1 = 530.
		 */

		ArrayList<Tuple> res = new ArrayList<Tuple>();

		for (Tuple tp : tuples) {
			if (tp.getField(field).compare(op, operand))
				res.add(tp);
		}

		// select operation does not change tuple description
		return new Relation(res, td);

	}

	/**
	 * This method performs a rename operation on a relation
	 * 
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be
	 *               renamed
	 * @param names  a list of new names. The order of these names is the same as
	 *               the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) throws Exception {
		// your code here
		/**
		 * Implement rename
		 */

		Type[] types = new Type[this.td.numFields()];
		String[] curNames = new String[this.td.numFields()];
		Set<String> existName = new HashSet<>();
		// copy
		for (int i = 0; i < this.td.numFields(); i++) {
			types[i] = this.td.getType(i);
			curNames[i] = this.td.getFieldName(i);
		}
		for (String var : curNames) {
			existName.add(var);
		}

		for (int i = 0; i < names.size(); i++) {
			if (existName.contains(names.get(i))) {
				throw new UnexpectedException("colume name exist");
			}
		}

		Relation temp = new Relation(this.tuples, new TupleDesc(types, curNames));
		// udpate
		for (int i = 0; i < fields.size(); i++) {
			if (names.get(i) == "") {
				return temp;
			}
			curNames[fields.get(i)] = names.get(i);

		}

		return new Relation(this.tuples, new TupleDesc(types, curNames));

	}

	/**
	 * This method performs a project operation on a relation
	 * 
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in
	 *               the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		// your code here
		/**
		 * Implement SELECT clause
		 */

		ArrayList<Tuple> res = new ArrayList<Tuple>();
		Type[] types = new Type[fields.size()];
		String[] curNames = new String[fields.size()];

		// update schema
		for (int i = 0; i < fields.size(); i++) {
			types[i] = this.td.getType(fields.get(i));
			curNames[i] = this.td.getFieldName(fields.get(i));
		}

		for (int i = 0; i < tuples.size(); i++) {
			Tuple tp = new Tuple(new TupleDesc(types, curNames));

			for (int j = 0; j < fields.size(); j++) {
				tp.setField(j, tuples.get(i).getField(fields.get(j)));
			}

			res.add(tp);
		}

		return new Relation(res, new TupleDesc(types, curNames));

	}

	/**
	 * This method performs a join between this relation and a second relation. The
	 * resulting relation will contain all of the columns from both of the given
	 * relations, joined using the equality operator (=)
	 * 
	 * @param other  the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be
	 *               used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in
	 *               the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		// your code here
		/**
		 * inner join: Implement Cartesian Product combined with a select
		 */
		ArrayList<Tuple> res = new ArrayList<Tuple>();
		Type[] types = new Type[this.td.numFields() + other.getDesc().numFields()];
		String[] curNames = new String[this.td.numFields() + other.getDesc().numFields()];
		int tdSize = this.td.numFields();

		// update schema: merge two TupleDesc
		for (int m = 0; m < this.td.numFields(); m++) {
			types[m] = this.td.getType(m);
			curNames[m] = this.td.getFieldName(m);
		}
		for (int n = 0; n < other.getDesc().numFields(); n++) {
			types[n + tdSize] = other.getDesc().getType(n);
			curNames[n + tdSize] = other.getDesc().getFieldName(n);
		}

		// join on field1 = field2
		for (Tuple tuple1 : tuples) {
			for (Tuple tuple2 : other.getTuples()) {

				if (tuple1.getField(field1).equals(tuple2.getField(field2))) {
					Tuple tp = new Tuple(new TupleDesc(types, curNames));

					for (int p = 0; p < this.td.numFields(); p++) {
						tp.setField(p, tuple1.getField(p));
					}
					for (int q = 0; q < other.getDesc().numFields(); q++) {
						tp.setField(q + tdSize, tuple2.getField(q));
					}
					res.add(tp);

				}
			}
		}

		return new Relation(res, new TupleDesc(types, curNames));
	}

	/**
	 * Performs an aggregation operation on a relation. See the lab write up for
	 * details.
	 * 
	 * @param op      the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		// your code here
		Aggregator agg = new Aggregator(op, groupBy, td);

		for (Tuple tp : tuples) {
			agg.merge(tp);
		}

		return new Relation(agg.getResults(), this.td);
	}

	public TupleDesc getDesc() {
		// your code here
		return this.td;
	}

	public ArrayList<Tuple> getTuples() {
		// your code here
		return this.tuples;
	}

	/**
	 * Returns a string representation of this relation. The string representation
	 * should first contain the TupleDesc, followed by each of the tuples in this
	 * relation
	 */
	public String toString() {
		// your code here
		String res = this.td.toString();
		for (Tuple tp : this.tuples) {
			res += tp.toString();
		}
		return res;
	}
}
