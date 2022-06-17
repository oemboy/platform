
package net.platform.kafka.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import lombok.Data;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Configuration(proxyBeanMethods = false)
@Data
public class KafkaCustomConfiguration implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {KafkaContainerConfig.class.getName(), KafkaCronConfig.class.getName()};
    }

}
