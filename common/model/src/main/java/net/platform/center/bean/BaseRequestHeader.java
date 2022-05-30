
package net.platform.center.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class BaseRequestHeader<T> extends RequestHeader {
    @JsonProperty("body")
    private T body;
}
