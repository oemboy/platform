
package net.platform.groovy.process;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Context<K, V> {
    void put(K key, V value);

    V get(K key);
}
