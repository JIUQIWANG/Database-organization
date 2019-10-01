package hw2;

import java.util.ArrayList;
import java.util.List;
import hw1.Database;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;
import hw1.Catalog;
import hw1.Field;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Query {

	private String q;

	public Query(String q) {
		this.q = q;
	}

	public Relation execute() {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect) selectStatement.getSelectBody();
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
		// your code here
		// From and Join logic
		Catalog cata = Database.getCatalog();
		List<Relation> relationList = new ArrayList<>();
		List<Integer> tableidList = new ArrayList<>();
		for (String var : tableList) {

			int tableid = cata.getTableId(var);
			tableidList.add(tableid);
			relationList.add(new Relation(cata.getDbFile(tableid).getAllTuples(), cata.getTupleDesc(tableid)));
		}
		List<Join> joinList = sb.getJoins();
		Relation afterJoin = relationList.get(0);
		int relationIndex = 1;
		if (joinList == null) {
			afterJoin = relationList.get(0);
		} else {
			for (int i = 0; i < joinList.size(); i++) {
				String[] cols = joinList.get(i).getOnExpression().toString().split("=");
				String first = cols[0].split("\\.")[1].trim();
				String second = cols[1].split("\\.")[1].trim();
				int firstArgument = 0;
				int secondArgument = 0;
				try {
					firstArgument = afterJoin.getDesc().nameToId(first);
					secondArgument = relationList.get(relationIndex).getDesc().nameToId(second);
				} catch (Exception e) {
					firstArgument = afterJoin.getDesc().nameToId(second);
					secondArgument = relationList.get(relationIndex).getDesc().nameToId(first);
				}

				afterJoin = afterJoin.join(relationList.get(relationIndex), firstArgument, secondArgument);
				relationIndex++;
			}
		}
		// Where Logic
		WhereExpressionVisitor wev = new WhereExpressionVisitor();
		Expression whereExpresstion = sb.getWhere();
		if (whereExpresstion == null) {

		} else {
			whereExpresstion.accept(wev);
			String whereName = wev.getLeft();
			Field whereField = wev.getRight();
			RelationalOperator whereOP = wev.getOp();
			afterJoin = afterJoin.select(afterJoin.getDesc().nameToId(whereName), whereOP, whereField);
		}
		// finish where logic
		// Select logic
		List<SelectItem> selectItem = sb.getSelectItems();
		ColumnVisitor visitor = new ColumnVisitor();
		List<Expression> groupBy = sb.getGroupByColumnReferences();
		ArrayList<Integer> cols = new ArrayList<>();
		AggregateOperator op;
		int aggregateFlag = 0;
		for (SelectItem item : selectItem) {
			item.accept(visitor);
			if (visitor.isAggregate()) {
				op = visitor.getOp();
				if (visitor.getColumn().equals("*")) {
					afterJoin = afterJoin.aggregate(op, groupBy != null);
					aggregateFlag = 1;
					continue;
				}
				cols.add(afterJoin.getDesc().nameToId(visitor.getColumn()));
				afterJoin = afterJoin.project(cols).aggregate(op, groupBy != null);
				aggregateFlag = 1;
				continue;
			}
			if (visitor.getColumn().equals("*")) {
				return afterJoin;
			}
			String coName = visitor.getColumn();
			cols.add(afterJoin.getDesc().nameToId(coName));
		}
		if (aggregateFlag == 0)
			return afterJoin.project(cols);
		else {
			return afterJoin;
		}

	}
}
