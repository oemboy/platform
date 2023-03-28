
package net.platform.flow.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @Author: bing zi
 * @Date: 2022/6/4 15:22
 */
@Data
public class AppSvcApiDef {
    @JsonProperty
    private String apiFlow0;

    @JsonProperty
    private String apiFlow1;

    @JsonProperty
    private String apiFlow2;

    @JsonProperty
    private String apiFlow3;

    @JsonProperty
    private String apiFlow4;

}
