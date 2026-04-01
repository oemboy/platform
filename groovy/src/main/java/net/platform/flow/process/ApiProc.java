
package net.platform.flow.process;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import groovy.json.JsonSlurper;
import groovy.lang.GroovyShell;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public class ApiProc {
    private static final JsonSlurper JSON_PARSER = new JsonSlurper();

    private final ConcurrentHashMap<String, String> flowContentMap = new ConcurrentHashMap<>(32);

    private final ConcurrentHashMap<String, Optional<Flow>> flowMap = new ConcurrentHashMap<>(32);

    private final GroovyShell shell = new FlowGroovyShell();

    private final FlowErrorHandle flowErrorHandle = new DefaultFlowErrorHandle();

    private final FlowBaseUnit parseJson = args -> {
        if (args instanceof String strArg) {
            return JSON_PARSER.parseText(strArg);
        }
        return null;
    };

    private final FlowBaseUnit jsonOutput = args -> args == null ? null : groovy.json.JsonOutput.toJson(args);

    @PostConstruct
    public void init() throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources("classpath*:FlowDSL.groovy");
        for (Resource resource : resources) {
            String script = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            shell.evaluate(script);
        }
    }

    public Optional<Flow> getApiFlow(String url) {
        ClassPathResource classPathResource = new ClassPathResource(getResourceFile(url));
        try (InputStream stream = classPathResource.getInputStream()) {
            String flowDsl = IOUtils.toString(stream, StandardCharsets.UTF_8);
            if (flowDsl.equals(flowContentMap.get(url)) && flowMap.containsKey(url)) {
                return flowMap.get(url);
            }
            flowContentMap.put(url, flowDsl);
            flowMap.put(url, Optional.of((Flow) shell.evaluate(flowDsl)));
            return flowMap.get(url);
        } catch (IOException e) {
            log.error("getApiFlow:", e);
            return Optional.empty();
        }
    }

    private String getResourceFile(String url) {
        return "/groovy/" + url + ".groovy";
    }

    public String processApi(Flow flow, String body) {
        log.info("INPUT: {}", body);
        final FlowContext<String, Object> ctx = new DefaultFlowContext();
        ctx.put("input", parseJson.exec(body));
        ctx.put("errorHandle", flowErrorHandle);
        ctx.put("parseJson", parseJson);
        ctx.put("jsonOutput", jsonOutput);
        Object res;
        try {
            flow.run(ctx);
            res = ctx.get("output");
        } catch (Exception e) {
            res = e.getMessage();
        }

        Object obj = jsonOutput.exec(res);
        if (obj instanceof String str) {
            return str;
        }

        return null;

    }
}
