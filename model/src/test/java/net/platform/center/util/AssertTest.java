
package net.platform.center.util;

import static net.platform.center.exception.ExceptionEnum.ERROR;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.platform.center.exception.BusinessException;

/**
 * @author bing zi
 * @since 2024/1/24 10:33
 */
class AssertTest {

    @Test
    void doesNotContain() {
        assertDoesNotThrow(() -> Assert.doesNotContain("AAA", "BBB", ERROR));
    }

    @Test
    void doesNotContainThrow() {
        assertThrows(BusinessException.class, () -> Assert.doesNotContain("AAA", "AA", ERROR));
    }
}