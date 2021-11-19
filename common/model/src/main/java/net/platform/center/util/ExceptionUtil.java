
package net.platform.center.util;

import java.io.FileNotFoundException;
import java.net.BindException;
import java.security.acl.NotOwnerException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.jar.JarException;

import javax.naming.InsufficientResourcesException;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理辅助类<br />
 * <p>
 * 提供以下能力：
 * <li>敏感异常识别{@linkplain ExceptionUtil#isSensitiveException(Throwable)}</li>
 * </p>
 *
 * @Author: Jack
 * @Date: 2021/9/5
 * @since 1.0.0
 */
@Slf4j
public abstract class ExceptionUtil {

    /**
     * 敏感异常信息集合， 用于判断异常是否为敏感异常
     */
    private static final List<Class<? extends Throwable>> SENSITIVE_EXCEPTION =
        Arrays.asList(FileNotFoundException.class, JarException.class, MissingResourceException.class,
            NotOwnerException.class, ConcurrentModificationException.class, InsufficientResourcesException.class,
            BindException.class, OutOfMemoryError.class, StackOverflowError.class, SQLException.class);

    private ExceptionUtil() {
    }

    /**
     * 判断异常是否为敏感异常，{@code true } 表示为敏感异常， {@code false } 表示为非敏感异常。
     * <p>
     * 敏感异常详情如下：
     * <li>{@linkplain FileNotFoundException}：泄露文件系统结构和文件名列举</li>
     * <li>{@linkplain JarException}：泄露文件系统结构</li>
     * <li>{@linkplain MissingResourceException}：资源列举</li>
     * <li>{@linkplain NotOwnerException}：所有人列举</li>
     * <li>{@linkplain ConcurrentModificationException} ：可能提供线程不安全的代码信息</li>
     * <li>{@linkplain InsufficientResourcesException}：服务器资源不足，可能会造成dos攻击</li>
     * <li>{@linkplain BindException}：当不信任客户端能够选择服务器端口时造成开放端口列举</li>
     * <li>{@linkplain OutOfMemoryError}：服务器资源不足，可能会造成dos攻击</li>
     * <li>{@linkplain StackOverflowError}：服务器资源不足，可能会造成dos攻击</li>
     * <li>{@linkplain SQLException}：数据库结构，用户名列举</li>
     * </p>
     * <p>
     * 使用 {@linkplain ExceptionUtil#SENSITIVE_EXCEPTION} 集合保存敏感异常信息。
     *
     * @param ex 需要判断的异常对象
     * @return {@code true } 表示为敏感异常， {@code false } 表示为非敏感异常
     */
    public static boolean isSensitiveException(Throwable ex) {
        return Objects.nonNull(ex) && SENSITIVE_EXCEPTION.stream().anyMatch(clazz -> clazz.isInstance(ex));
    }
}
