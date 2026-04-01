
package net.platform.groovy.process;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Context<K, V> {
    /**
     * put value
     *
     * @param key key
     * @param value value
     */
    void put(K key, V value);

    /**
     * get key
     *
     * @param key key
     * @return value
     */
    V get(K key);
}
