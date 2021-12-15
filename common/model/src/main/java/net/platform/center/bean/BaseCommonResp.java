package net.platform.center.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BaseCommonResp<T> extends CommonResp{
    @JsonProperty("body")
    private T body;
}
