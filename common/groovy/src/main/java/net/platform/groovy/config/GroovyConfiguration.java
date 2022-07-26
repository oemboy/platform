
package net.platform.groovy.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @Author: bing zi
 * @Date: 2022/6/4 15:22
 */
@Configuration
@ImportResource(locations = {"classpath*: *-groovy.xml"})
public class GroovyConfiguration implements ApplicationContextAware {
    private static ApplicationContext context;

    private static void setContext(ApplicationContext context) {
        GroovyConfiguration.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

}
