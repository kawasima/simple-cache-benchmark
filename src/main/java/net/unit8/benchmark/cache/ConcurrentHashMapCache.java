package net.unit8.benchmark.cache;

import com.google.common.cache.AbstractCache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kawasima
 */
public class ConcurrentHashMapCache<K,V> extends AbstractCache<K,V> {
    private ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<K, V>(65536);

    @Override
    public V getIfPresent(Object key) {
        return cache.get(key);
    }

    @Override
    public void put(final K key, final V value) {
        synchronized (cache) {
            if (!cache.containsKey(key)) {
                cache.put(key, value);
            }
        }
    }

}
