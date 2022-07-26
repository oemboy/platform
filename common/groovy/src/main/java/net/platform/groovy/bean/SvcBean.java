package net.platform.groovy.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SvcBean implements Serializable {
    @JsonProperty
    private String svcId;
    @JsonProperty
    private String svcPath;
    @JsonProperty
    private String method;
}
