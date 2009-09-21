package se.kth.livetech.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftHashMap<K,V> extends AbstractMap<K,V> {
	private final Map<K,V> hash = new HashMap<K,V>();

    public V get(Object key) {
    	return hash.get(key);
    }

	public V put(K key, V value) {
		return hash.put(key, value);
	}
	public void remove(K key) {
		hash.remove(key);
	}
	public void clear() {
		hash.clear();
	}
	public int size() {
		return hash.size();
	}
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}	
}
