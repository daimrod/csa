package jgreg.internship.nii.RES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jgreg.internship.nii.Utils.Utils;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

// TODO: Auto-generated Javadoc
/**
 * The Class StringListRES.
 */
public final class StringListRES implements SharedResourceObject {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(StringListRES.class
			.getCanonicalName());

	/** The list. */
	private ArrayList<String> list;

	/* (non-Javadoc)
	 * @see org.apache.uima.resource.SharedResourceObject#load(org.apache.uima.resource.DataResource)
	 */
	public void load(DataResource aData) throws ResourceInitializationException {
		String filename = aData.getUri().toString();
		File file = new File(filename);

		try {
			list = Utils.readLines(file);
		} catch (IOException ex) {
			throw new ResourceInitializationException(ex);
		}
	}

	/**
	 * Adds the.
	 *
	 * @param s the s
	 * @return true, if successful
	 */
	public boolean add(String s) {
		return list.add(s);
	}

	/**
	 * Adds the.
	 *
	 * @param index the index
	 * @param s the s
	 */
	public void add(int index, String s) {
		list.add(index, s);
	}

	/**
	 * Adds the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean addAll(Collection<String> c) {
		return list.addAll(c);
	}

	/**
	 * Adds the all.
	 *
	 * @param index the index
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean addAll(int index, Collection<String> c) {
		return list.addAll(index, c);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		list.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return list.clone();
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean contains(Object o) {
		return list.contains(o);
	}

	/**
	 * Ensure capacity.
	 *
	 * @param minCapacity the min capacity
	 */
	public void ensureCapacity(int minCapacity) {
		list.ensureCapacity(minCapacity);
	}

	/**
	 * Gets the.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String get(int index) {
		return list.get(index);
	}

	/**
	 * Index of.
	 *
	 * @param o the o
	 * @return the int
	 */
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<String> iterator() {
		return list.iterator();
	}

	/**
	 * Last index of.
	 *
	 * @param o the o
	 * @return the int
	 */
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	/**
	 * List iterator.
	 *
	 * @return the list iterator
	 */
	public ListIterator<String> listIterator() {
		return list.listIterator();
	}

	/**
	 * List iterator.
	 *
	 * @param index the index
	 * @return the list iterator
	 */
	public ListIterator<String> listIterator(int index) {
		return list.listIterator(index);
	}

	/**
	 * Removes the.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String remove(int index) {
		return list.remove(index);
	}

	/**
	 * Removes the.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean remove(Object o) {
		return list.remove(o);
	}

	/**
	 * Removes the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean removeAll(Collection<String> c) {
		return list.removeAll(c);
	}

	/**
	 * Retain all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public boolean retainAll(Collection<String> c) {
		return list.retainAll(c);
	}

	/**
	 * Sets the.
	 *
	 * @param index the index
	 * @param s the s
	 * @return the string
	 */
	public String set(int index, String s) {
		return list.set(index, s);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return list.size();
	}

	/**
	 * Sub list.
	 *
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the list
	 */
	public List<String> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	/**
	 * To array.
	 *
	 * @return the object[]
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/**
	 * To array.
	 *
	 * @param a the a
	 * @return the string[]
	 */
	public String[] toArray(String[] a) {
		return list.toArray(a);
	}

	/**
	 * Trim to size.
	 */
	public void trimToSize() {
		list.trimToSize();
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public ArrayList<String> getList() {
		return list;
	}
}
