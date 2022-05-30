
package net.platform.center.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @Author: jack
 * @Date: 2021/6/4 15:22
 */

public final class ValidationUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationUtil() {
    }

    public static Validator getValidator() {
        return validator;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return getValidator().validate(object, groups);
    }

    public static <T> void validateThrow(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> result = validate(object, groups);
        if (CollectionUtils.isNotEmpty(result)) {
            throw new ConstraintViolationException(result);
        }
    }
}
