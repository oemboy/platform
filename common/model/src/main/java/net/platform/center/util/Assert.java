
package net.platform.center.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import net.platform.center.exception.BusinessException;
import net.platform.center.exception.ExceptionCode;

/**
 * @author jack
 * @since 2021/6/4 15:22
 */

public final class Assert {
    private Assert() {
    }

    /**
     * Assert a boolean expression, throwing an {@code BusinessException}
     * if the expression evaluates to {@code false}.
     * <p>
     * Call {@link #isTrue} if you wish to throw an {@code BusinessException}
     * on an assertion failure.
     *
     * @param expression a boolean expression
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if {@code expression} is {@code false}
     */
    public static void state(boolean expression, ExceptionCode code, Object... args) {
        if (!expression) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code BusinessException}
     * if the expression evaluates to {@code false}.
     *
     * @param expression a boolean expression
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if {@code expression} is {@code false}
     */
    public static void isTrue(boolean expression, ExceptionCode code, Object... args) {
        if (!expression) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that an object is {@code null}.
     * 
     * <pre class="code">
     * Assert.isNull(value, "The value must be null");
     * </pre>
     *
     * @param object the object to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the object is not {@code null}
     */
    public static void isNull(@Nullable Object object, ExceptionCode code, Object... args) {
        if (object != null) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     *
     * @param object the object to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the object is {@code null}
     */
    public static void notNull(@Nullable Object object, ExceptionCode code, Object... args) {
        if (object == null) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * 
     * <pre class="code">
     * Assert.hasLength(name, "Name must not be empty");
     * </pre>
     *
     * @param text the String to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the text is empty
     */
    public static void hasLength(@Nullable String text, ExceptionCode code, Object... args) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * 
     * <pre class="code">
     * Assert.hasText(name, "'name' must not be empty");
     * </pre>
     *
     * @param text the String to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the text does not contain valid text content
     * @see StringUtils
     */
    public static void hasText(@Nullable String text, ExceptionCode code, Object... args) {
        if (StringUtils.isBlank(text)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that the given text does not contain the given substring.
     * 
     * <pre class="code">
     * Assert.doesNotContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch the text to search
     * @param substring the substring to find within the text
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the text contains the substring
     */
    public static void doesNotContain(@Nullable String textToSearch, String substring, ExceptionCode code,
        Object... args) {
        if (Strings.CS.contains(textToSearch, substring)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * 
     * <pre class="code">
     * Assert.notEmpty(array, "The array must contain elements");
     * </pre>
     *
     * @param array the array to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(@Nullable Object[] array, ExceptionCode code, Object... args) {
        if (ObjectUtils.isEmpty(array)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that an array contains no {@code null} elements.
     * <p>
     * Note: Does not complain if the array is empty!
     * 
     * <pre class="code">
     * Assert.noNullElements(array, "The array must contain non-null elements");
     * </pre>
     *
     * @param array the array to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the object array contains a {@code null} element
     */
    public static void noNullElements(@Nullable Object[] array, ExceptionCode code, Object... args) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new BusinessException(code, args);
                }
            }
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * 
     * <pre class="code">
     * Assert.notEmpty(collection, "Collection must contain elements");
     * </pre>
     *
     * @param collection the collection to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the collection is {@code null} or
     *         contains no elements
     */
    public static void notEmpty(@Nullable Collection<?> collection, ExceptionCode code, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that a collection contains no {@code null} elements.
     * <p>
     * Note: Does not complain if the collection is empty!
     * 
     * <pre class="code">
     * Assert.noNullElements(collection, "Collection must contain non-null elements");
     * </pre>
     *
     * @param collection the collection to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the collection contains a {@code null} element
     * @since 5.2
     */
    public static void noNullElements(@Nullable Collection<?> collection, ExceptionCode code, Object... args) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new BusinessException(code, args);
                }
            }
        }
    }

    /**
     * Assert that a Map contains entries; that is, it must not be {@code null}
     * and must contain at least one entry.
     * 
     * <pre class="code">
     * Assert.notEmpty(map, "Map must contain entries");
     * </pre>
     *
     * @param map the map to check
     * @param code the exception message to use if the assertion fails
     * @throws BusinessException if the map is {@code null} or contains no entries
     */
    public static void notEmpty(@Nullable Map<?, ?> map, ExceptionCode code, Object... args) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     * 
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo, "Foo expected");
     * </pre>
     *
     * @param type the type to check against
     * @param obj the object to check
     * @param code a message which will be prepended to provide further context.
     *        If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     *        will be appended. If it ends in a space, the name of the offending object's
     *        type will be appended. In any other case, a ":" with a space and the name
     *        of the offending object's type will be appended.
     * @throws BusinessException if the object is not an instance of type
     */
    public static void isInstanceOf(Class<?> type, @Nullable Object obj, ExceptionCode code, Object... args) {
        Objects.requireNonNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new BusinessException(code, args);
        }
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * 
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass, "Number expected");
     * </pre>
     *
     * @param superType the super type to check against
     * @param subType the sub type to check
     * @param code a message which will be prepended to provide further context.
     *        If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     *        will be appended. If it ends in a space, the name of the offending sub type
     *        will be appended. In any other case, a ":" with a space and the name of the
     *        offending sub type will be appended.
     * @throws BusinessException if the classes are not assignable
     */
    public static void isAssignable(Class<?> superType, @Nullable Class<?> subType, ExceptionCode code,
        Object... args) {
        Objects.requireNonNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new BusinessException(code, args);
        }
    }
}
