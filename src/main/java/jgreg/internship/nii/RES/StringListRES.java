package jgreg.internship.nii.RES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public final class StringListRES implements SharedResourceObject {
	private static final Logger logger = Logger.getLogger(StringListRES.class
			.getCanonicalName());

	private ArrayList<String> list;

	public void load(DataResource aData) throws ResourceInitializationException {
		String filename = aData.getUri().toString();
		File file = new File(filename);

		try {
			list = new ArrayList(FileUtils.readLines(file).stream()
					.map(line -> line.trim()).filter(line -> !line.isEmpty())
					.filter(line -> !line.startsWith("#"))
					.collect(Collectors.toList()));
		} catch (IOException ex) {
			throw new ResourceInitializationException(ex);
		}
	}

	public boolean add(String s) {
		return list.add(s);
	}

	public void add(int index, String s) {
		list.add(index, s);
	}

	public boolean addAll(Collection<String> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<String> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	public Object clone() {
		return list.clone();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public void ensureCapacity(int minCapacity) {
		list.ensureCapacity(minCapacity);
	}

	public String get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<String> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<String> listIterator() {
		return list.listIterator();
	}

	public ListIterator<String> listIterator(int index) {
		return list.listIterator(index);
	}

	public String remove(int index) {
		return list.remove(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<String> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<String> c) {
		return list.retainAll(c);
	}

	public String set(int index, String s) {
		return list.set(index, s);
	}

	public int size() {
		return list.size();
	}

	public List<String> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public String[] toArray(String[] a) {
		return list.toArray(a);
	}

	public void trimToSize() {
		list.trimToSize();
	}

    public ArrayList<String> getList() {
        return list;
    }
}
