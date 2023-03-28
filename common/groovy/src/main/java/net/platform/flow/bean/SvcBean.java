package net.platform.flow.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: bing zi
 * @Date: 2022/6/4 15:22
 */
@Data
public class SvcBean implements Serializable {
    @JsonProperty
    private String svcId;
    @JsonProperty
    private String svcPath;
    @JsonProperty
    private String method;
}
