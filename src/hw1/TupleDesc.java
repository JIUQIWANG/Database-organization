package hw1;

import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc {

    private Type[] types;
    private String[] fields;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the specified
     * types, with associated named fields.
     *
     * @param typeAr  array specifying the number of and types of fields in this
     *                TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may
     *                be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // your code here
        types = typeAr;
        fields = fieldAr;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // your code here
        return fields.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // your code here
        if (i >= fields.length)
            throw new NoSuchElementException();
        return fields[i];
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
        // your code here
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == name) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        // your code here
        if (i >= types.length)
            throw new NoSuchElementException();
        return types[i];
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc. Note
     *         that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // your code here
        int result = 0;
        for (int i = 0; i < types.length; i++) {
            if (types[i] == Type.INT) {
                result += 4;
            } else {
                result += 129;
            }
        }
        return result;
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // your code here
        TupleDesc other = (TupleDesc) o;
        if (this.getSize() != other.getSize()) {
            return false;
        }
        for (int i = 0; i < this.types.length; i++) {
            if (types[i] != other.getType(i)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        int prime = 31;
        int result = 1;
        result = prime * result + ((types == null) ? 0 : types.hashCode());
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        return result;
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although the
     * exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // your code here
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (types[i] == Type.INT) {
                sb.append("INT");
                sb.append("(");
                sb.append(fields[i]);
                sb.append(")");
            } else {
                sb.append("STRING");
                sb.append("(");
                sb.append(fields[i]);
                sb.append(")");
            }
            if (i != types.length - 1)
                sb.append(",");
        }
        return sb.toString();
    }
}
