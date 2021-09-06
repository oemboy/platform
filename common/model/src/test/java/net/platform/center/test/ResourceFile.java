
package net.platform.center.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(ResourceArgumentsProvider.class)
public @interface ResourceFile {

    String[] resources();

    String encoding() default "UTF-8";

    Class<?> type() default String.class;
}
