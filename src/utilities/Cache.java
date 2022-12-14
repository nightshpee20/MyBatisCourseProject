package utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class Cache {
	class CacheItem implements Comparable<CacheItem> {
		String key;
		Object res;
		int readsArrPos;
		
		CacheItem(String sql, Object params, Object res) {
			key = sql + params.toString();
			this.res = res;
		}

		@Override
		public int compareTo(CacheItem o) {
			if (reads[readsArrPos] == reads[o.readsArrPos])
				return 0;
			if (reads[readsArrPos] > reads[o.readsArrPos])
				return 1;
			return -1;
		}
	}
	
	public static final Integer DEF_CAP = 64;
	public static final Integer DEF_FLUSH_INTERVAL = 30_000;
	public static final EvictionPolicy  DEF_EP = EvictionPolicy.FIFO; 
	
	public enum EvictionPolicy {LRU, FIFO};
	private EvictionPolicy evictionPolicy;
	private int flushInterval;
	
	private Map<String, CacheItem> cacheMap;
	
	private PriorityQueue<CacheItem> evictionPQ;
	private int nextReadsArrPos;
	private int[] reads;
	private CircularBuffer<CacheItem> evictionCB;
	
	private int size;
	private int capacity;

	public Cache() {
		this(DEF_CAP, DEF_EP, DEF_FLUSH_INTERVAL);
	}
	
	public Cache(Integer capacity, EvictionPolicy ep, Integer flushInterval) {
		if (ep == EvictionPolicy.LRU) {
			evictionPQ = new PriorityQueue<>();
			nextReadsArrPos = 0;
			reads = new int[capacity];
		} else
			evictionCB = new CircularBuffer<>(capacity);
		
		this.flushInterval = flushInterval;
		this.capacity = capacity;
		cacheMap = new HashMap<>(capacity);
		evictionPolicy = ep;			
		size = 0;
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
		    public void run() { 
		    	reset();
		    }
		}, flushInterval, flushInterval);
	}
	
	public void add(String sql, Object params, Object res) {
		if (sql == null || params == null || res == null)
			throw new IllegalArgumentException("This method does not accept null arguments of any kind!");
		
		if (size == capacity)
			evict();
		else
			size++;
		
		CacheItem entry = new CacheItem(sql, params, res);
		cacheMap.put(entry.key, entry);
		
		if (evictionPolicy == EvictionPolicy.FIFO) {
			evictionCB.add(entry);
		} else {
			entry.readsArrPos = nextReadsArrPos++;
			evictionPQ.add(entry);
		}
	}
	
	public Object read(String sql, Object params) {
		if (sql == null || params == null)
			throw new IllegalArgumentException("sql/params cannot be null!");
		
		String key = sql + params.toString();
		CacheItem entry = cacheMap.get(key);
		if (entry == null)
			return null;
		
		if (entry.key.equals(key)) {
			if (evictionPQ != null) {
				evictionPQ.remove(entry);
				reads[entry.readsArrPos]++;
				evictionPQ.add(entry); 
			}
			
			return entry.res;
		}
		
		return null;
	}
	
	private void evict() {
		CacheItem leastRead = null;
		if (evictionCB != null) {
			leastRead = evictionCB.poll();
		} else {
			leastRead = evictionPQ.poll();
			int pos = leastRead.readsArrPos;
			nextReadsArrPos = pos;
			reads[pos] = 0;
		}
		
		String key = leastRead.key;
		cacheMap.remove(key);
		size--;
	}
	
	public void reset() {
		cacheMap.clear();
		
		if (evictionPQ != null) {
			evictionPQ.clear();
			nextReadsArrPos = 0;
			Arrays.fill(reads, 0);			
		} else
			Arrays.fill(evictionCB.buf, null);
	}
	
	public int size() {
		return size;
	}
}
