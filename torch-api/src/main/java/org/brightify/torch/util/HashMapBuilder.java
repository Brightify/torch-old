package org.brightify.torch.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tadeas@brightify.org">Tadeas Kriz</a>
 */
public class HashMapBuilder<K, V> {

    private final HashMap<K, V> map;

    public HashMapBuilder() {
        map = new HashMap<K, V>();
    }

    public HashMapBuilder(int initialCapacity) {
        map = new HashMap<K, V>(initialCapacity);
    }

    public HashMapBuilder(int initialCapacity, float loadFactor) {
        map = new HashMap<K, V>(initialCapacity, loadFactor);
    }

    public HashMapBuilder(Map<? extends K, ? extends V> fromMap) {
        map = new HashMap<K, V>(fromMap);
    }

    public HashMap<K, V> hashMap() {
        return map;
    }

    public Map<K, V> map() {
        return map;
    }

    public Map<K, V> unmodifiableMap() {
        return Collections.unmodifiableMap(map);
    }

    public HashMapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public static <K, V> HashMapBuilder<K, V> from(Map<? extends K, ? extends V> map) {
        return new HashMapBuilder<K, V>(map);
    }

    public static <K, V> HashMapBuilder<K, V> begin() {
        return new HashMapBuilder<K, V>();
    }

    public static <K, V> HashMapBuilder<K, V> expect(int size) {
        return new HashMapBuilder<K, V>(size);
    }

    public static <K, V> HashMapBuilder<K, V> expect(int size, float load) {
        return new HashMapBuilder<K, V>(size, load);
    }



}
