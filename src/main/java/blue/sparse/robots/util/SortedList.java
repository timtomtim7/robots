package blue.sparse.robots.util;

import java.util.ArrayList;
import java.util.List;

public class SortedList<T extends Comparable<T>>{
	private List<T> data = new ArrayList<>();

	public int size() {
		return data.size();
	}

	public T peek() {
		return data.get(0);
	}

	public T pop() {
		return data.remove(0);
	}

	public void add(T t) {
		data.add(t);
		update(data.size() - 1);
	}

	public boolean update(int index) {
		final T self = data.get(index);
		int i = index;
		while(true) {
			if(i > 0 && data.get(i - 1).compareTo(self) >= 0) {
				swap(i, --i);
				continue;
			}

			if(i < data.size() - 1 && data.get(i + 1).compareTo(self) < 0) {
				swap(i, ++i);
				continue;
			}

			break;
		}

		return i != index;
	}

	public int indexOf(T t) {
		return data.indexOf(t);
	}

	public T get(T t) {
		final int index = data.indexOf(t);
		if(index < 0)
			return null;
		return data.get(index);
	}

	public boolean contains(T t) {
		return data.contains(t);
	}

	private void swap(int a, int b) {
		final T t = data.get(a);
		data.set(a, data.get(b));
		data.set(b, t);
	}
}
