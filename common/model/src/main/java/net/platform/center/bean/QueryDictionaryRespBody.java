package net.platform.center.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class QueryDictionaryRespBody {
    @JsonProperty("dictionaries")
    private List<Dictionary> dictionaries;
}
