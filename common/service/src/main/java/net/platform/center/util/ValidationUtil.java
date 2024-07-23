
package net.platform.center.util;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * @author jack
 * @since 2021/6/4 15:22
 */

public final class ValidationUtil {
    private static final Validator VALIDATOR = buildValidator();

    private ValidationUtil() {
    }

    private static Validator buildValidator() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return validatorFactory.getValidator();
        }
    }

    public static Validator getValidator() {
        return VALIDATOR;
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
