
package net.platform.center.util;

import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;

public final class ValidationUtil {
    private static Validator validator;

    private ValidationUtil() {
    }

    public static Validator getValidator() {
        if (Objects.isNull(validator)) {
            synchronized (ValidationUtil.class) {
                if (Objects.isNull(validator)) {
                    validator = Validation.buildDefaultValidatorFactory().getValidator();
                }
            }
        }
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
