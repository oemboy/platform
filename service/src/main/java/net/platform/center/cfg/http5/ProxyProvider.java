
package net.platform.center.cfg.http5;

import java.net.InetSocketAddress;

/**
 * @author bing zi
 * @since 2026/1/27
 */

public interface ProxyProvider {
    // 返回代理地址，返回 null 则表示不使用代理
    InetSocketAddress getProxyAddress();

    InetSocketAddress getProxyAddress(String paramKey);
}
