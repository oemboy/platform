
package net.platform.center.bean;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */

@Getter
@Setter
@ToString
public class RequestHeader {
    private Map<String, String> header = new HashMap<>();
}
