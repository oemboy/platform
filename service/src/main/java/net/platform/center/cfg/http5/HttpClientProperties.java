
package net.platform.center.cfg.http5;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author bing zi
 * @since 2026/3/3 15:22
 */

@Data
@ConfigurationProperties(prefix = "http")
public class HttpClientProperties {
    // 是否开启自定义 HttpClient 自动配置
    private boolean enabled = true;

    /** 最大连接数 */
    private Integer maxTotal = 900;

    /** 每个路由的最大连接数 */
    private Integer maxPerRoute = 300;

    /** 连接超时时间（秒） */
    private Integer connectionTimeout = 30;

    /** 读取超时时间（秒） */
    private Integer socketTimeout = 120;
}
