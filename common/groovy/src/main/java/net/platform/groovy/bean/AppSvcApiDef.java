package net.platform.groovy.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AppSvcApiDef implements Serializable {
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
