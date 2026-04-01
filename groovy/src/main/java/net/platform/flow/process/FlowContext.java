
package net.platform.flow.process;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface FlowContext<K, V> {
    /**
     * 存入key
     * @param key key
     * @param value value
     */
    void put(K key, V value);

    /**
     * 获取key值
     * @param key key
     * @return 返回对象
     */
    V get(K key);
}
