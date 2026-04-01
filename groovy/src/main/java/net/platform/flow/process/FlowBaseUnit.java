
package net.platform.flow.process;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface FlowBaseUnit {
    /**
     * 执行
     * @param args 入参
     * @return 返回对象
     */
    Object exec(Object args);
}
