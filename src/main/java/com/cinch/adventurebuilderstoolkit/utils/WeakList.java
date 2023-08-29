package com.cinch.adventurebuilderstoolkit.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WeakList<type extends Object> implements List<type>{
	private ArrayList<WeakReference<type>> innerList=new ArrayList<>();
	
	private boolean removeObject(Object type) {
		synchronized (this) {
			List<WeakReference<type>> removeList=new ArrayList<>();
			boolean removed=false;
			for (var r:innerList) {
				type g=r.get();
				removed|=(g!=null && g==type);
				if (g==null || g==type) {
					removeList.add(r);
				}
			}
			innerList.removeAll(removeList);
			return removed;
		}
	}
	
	private boolean removeItem(type type) {
		synchronized (this) {
			List<WeakReference<type>> removeList=new ArrayList<>();
			boolean removed=false;
			for (var r:innerList) {
				type g=r.get();
				removed|=(g!=null && g==type);
				if (g==null || g==type) {
					removeList.add(r);
				}
			}
			innerList.removeAll(removeList);
			return removed;
		}
	}
	
	
	private List<type> getRefList() {
		synchronized (this) {
			List<type> newList=new ArrayList<>();
			for (var r:innerList) {
				type g=r.get();
				if (g!=null) {
					newList.add(r.get());
				}
			}
			return newList;
		}
	}

	@Override
	public int size() {
		removeItem(null);
		return innerList.size();
	}

	@Override
	public boolean isEmpty() {
		removeItem(null);
		return innerList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		for (var r:innerList) {
			if (r.get()!=null || r.get().equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<type> iterator() {
		return getRefList().iterator();
	}

	@Override
	public Object[] toArray() {
		return getRefList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getRefList().toArray(a);
	}

	@Override
	public boolean add(type e) {
		innerList.add(new WeakReference<type>(e));
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return removeObject((Object)o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getRefList().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends type> c) {
		for (type v:c) {
			add(v);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends type> c) {
		for (type v:c) {
			add(index++,v);
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removed=false;
		for (var v:c) {
			removed|=remove(v);
		}
		return removed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		var l=getRefList();
		var changed=l.retainAll(c);
		innerList.clear();
		addAll(l);
		return changed;
		
	}

	@Override
	public void clear() {
		innerList.clear();
	}

	@Override
	public type get(int index) {
		return getRefList().get(index);
	}

	@Override
	public type set(int index, type element) {
		removeItem(null);
		var old=innerList.set(index, new WeakReference<type>(element));
		return old.get();
	}

	@Override
	public void add(int index, type element) {
		removeItem(null);
		innerList.add(index, new WeakReference<type>(element));
	}

	@Override
	public type remove(int index) {
		type o=get(index);
		removeItem(o);
		return o;
	}

	@Override
	public int indexOf(Object o) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return getRefList().lastIndexOf(o);
	}

	@Override
	public ListIterator<type> listIterator() {
		return getRefList().listIterator();
	}

	@Override
	public ListIterator<type> listIterator(int index) {
		return getRefList().listIterator(index);
	}

	@Override
	public List<type> subList(int fromIndex, int toIndex) {
		return getRefList().subList(fromIndex, toIndex);
	}
}
