package net.platform.center.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class RequestHeader {
    private Map<String, String> header = new HashMap<>();
}
