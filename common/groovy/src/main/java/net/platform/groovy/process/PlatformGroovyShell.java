
package net.platform.groovy.process;

import java.nio.charset.StandardCharsets;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import groovy.lang.GroovyShell;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
public class PlatformGroovyShell extends GroovyShell {
    private static final String[] IMPORT_CLASS = {"groovy.json.JsonSlurper", "groovy.json.JsonBuilder",};

    private static final CompilerConfiguration DEFAULT_COMPILER_CONFIG = new CompilerConfiguration();

    static {
        ImportCustomizer icz = new ImportCustomizer();
        icz.addImports(IMPORT_CLASS);
        DEFAULT_COMPILER_CONFIG.addCompilationCustomizers(icz);
        DEFAULT_COMPILER_CONFIG.setSourceEncoding(StandardCharsets.UTF_8.name());
    }

    public PlatformGroovyShell() {
        super(DEFAULT_COMPILER_CONFIG);
    }
}
