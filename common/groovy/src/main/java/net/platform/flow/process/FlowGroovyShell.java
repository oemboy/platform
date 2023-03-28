
package net.platform.flow.process;

import java.nio.charset.StandardCharsets;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import groovy.lang.GroovyShell;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class FlowGroovyShell extends GroovyShell {
    private static final String[] IMPORT_CLASS = {"groovy.json.JsonSlurper", "groovy.json.JsonBuilder",
        "net.platform.flow.step.EdgeMatcher", "net.platform.flow.FlowDSL",

    };

    private static final String[][] STATIC_IMPORT_METHODS = {{"net.platform.flow.FlowDSL", "flow"}};

    private static final CompilerConfiguration DEFAULT_COMPILER_CONFIG = new CompilerConfiguration();

    static {
        ImportCustomizer icz = new ImportCustomizer();
        icz.addImports(IMPORT_CLASS);
        for (String[] method : STATIC_IMPORT_METHODS) {
            icz.addStaticImport(method[0], method[1]);
        }
        DEFAULT_COMPILER_CONFIG.addCompilationCustomizers(icz);
        DEFAULT_COMPILER_CONFIG.setSourceEncoding(StandardCharsets.UTF_8.name());

    }

    public FlowGroovyShell() {
        super(DEFAULT_COMPILER_CONFIG);
    }
}
