package net.unit8.benchmark.cache;

import com.google.common.cache.AbstractCache;

import java.util.HashMap;

/**
 * @author kawasima
 */
public class SynchronizedHashMapCache<K, V> extends AbstractCache<K,V> {
    private HashMap<K, V> cache = new HashMap<K, V>();

    @Override
    public synchronized V getIfPresent(Object key) {
        return cache.get(key);
    }

    @Override
    public synchronized void put(final K key, final V value) {
        if (!cache.containsKey(key)) {
            cache.put(key, value);
        }
    }
}
