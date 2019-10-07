package hw2;

import java.util.ArrayList;
import java.util.HashMap;

import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;
import hw1.Field;
import hw1.IntField;
import hw1.StringField;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
 private AggregateOperator o;
 private boolean groupBy;
 private TupleDesc td; 
 

 /**
  * HashMap:
  * <k,v>: if groupby, k is the group ;v is the aggregation value
  */
 
 private HashMap<Field,ArrayList<Field>> map;
 private Field defaultKey;

 public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
  //your code here
  this.o = o;
  this.groupBy = groupBy;
  this.td = td;
  map = new HashMap<>();
  defaultKey = new IntField(1);

 }

 /**
  * Merges the given tuple into the current aggregation
  * @param t the tuple to be aggregated
  */
 public void merge(Tuple t) {
  //your code here
  Field k ;
  Field v;
  if (!this.groupBy) {
   k = defaultKey;
   v = t.getField(0);
  }else {
   k = t.getField(0);
   v = t.getField(1);
  }
  
  // if key doesn't exist in map, create a new ArrayList
  if (!map.containsKey(k)) {
   map.put(k, new ArrayList<Field>());
  }
  
  map.get(k).add(v); 

 }
 
 /**
  * Returns the result of the aggregation
  * @return a list containing the tuples after aggregation
  */
 public ArrayList<Tuple> getResults() {
  //your code here
  ArrayList<Tuple> res = new ArrayList<Tuple>();
  
  // using foreach loop for iteration 
  map.forEach( (k,v) -> {
   
   // we only have Int or String type
   if(v.get(0).getType()==Type.STRING) {
    String str = this.o== AggregateOperator.MAX || this.o == AggregateOperator.MIN ? ((StringField) (v.get(0))).getValue() : null;
    
    int cnt = 0; // the number of record within a group
    
    for (Field f: v) {
     String curStr = ((StringField)f).getValue();
     switch (this.o) {
     case MAX:
      str = curStr.compareTo(str) > 0 ? curStr:str;
      break;
     case MIN:
      str = curStr.compareTo(str) < 0 ? curStr:str;
      break;
     case COUNT:
      cnt++;
      break;
     case AVG:
//      break;
     case SUM:
//      break;
      
     }
     
    } 
    
    Field newField = this.o==AggregateOperator.COUNT ? new IntField(cnt) : new StringField(str);
    Tuple newTp = new Tuple(this.td);
    
    if(!this.groupBy) {
     newTp.setField(0, newField); // value only
    }else {
     newTp.setField(0, k); // group
     newTp.setField(1, newField);

    }
    res.add(newTp);

   }else if(v.get(0).getType()==Type.INT) {
    
    Integer num = this.o== AggregateOperator.MIN || this.o == AggregateOperator.MAX ? ((IntField) (v.get(0))).getValue() : 0;

    for (Field f: v) {
     int curNum = ((IntField) f).getValue();
     switch (this.o) {
     case MAX:
      num = curNum>num ? curNum:num;
      break;
     case MIN:
      num = curNum<num ? curNum:num;
      break;
     case AVG:
      num += curNum;
      break;
     case COUNT:
      num++;
      break;
     case SUM:
      num += curNum;
      break;
      
     }
     
    } 
    
    num = this.o==AggregateOperator.AVG ? num/v.size():num; 
    Tuple newTp = new Tuple(this.td);
    
    if(!this.groupBy) {
     newTp.setField(0, new IntField(num)); // group
    }else {
     newTp.setField(0, k); 
     newTp.setField(1, new IntField(num));

    }
    res.add(newTp);
   }else {
    try {
     throw new Exception("For the purposes of our database, we will only be using two datatypes: int and string (char)"); 
    }catch(Exception e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
    }
   }
  });
  
  return res;
 }

}