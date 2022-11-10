package utilities;

import java.util.Iterator;

public class CircularBuffer<T> implements Iterable<T> {
	static final int MIN_CAP = 10;
	public int capacity;
	public int size;
	public Object[] buf;
	int head;
	int tail;
	
	public CircularBuffer(int capacity) {
		if (capacity < MIN_CAP)
			throw new IllegalArgumentException("Capacity cannot be less than " + MIN_CAP);
		this.capacity = capacity;
		buf = new Object[capacity];
	}

	public boolean add(T element) {
		if (size == capacity)
			return false;
		
		buf[tail] = element;
		size++;
		if (tail + 1 == buf.length)
			tail = 0;
		else 
			tail++;
		
		return true;
	}
	
	public T poll() {
		if (size == 0) 
			return null;
		
		T res = (T)buf[head];
		size--;
		if (head + 1 == buf.length)
			head = 0;
		else
			head++;
		
		return res;
	}
	
	public T peek() {
		if (size == 0) 
			return null;
		return (T)buf[head];
	}

	public Iterator<T> iterator() {
		return new Iterator<>() {
			int elementsLeft = size;
			int headIndex = head;
			public boolean hasNext() {
				return elementsLeft > 0;
			}

			public T next() {
				T res = (T)buf[headIndex];
				if (headIndex + 1 == buf.length)
					headIndex = 0;
				else
					headIndex++;
				elementsLeft--;
				return res;
			}
		};
	}
}
