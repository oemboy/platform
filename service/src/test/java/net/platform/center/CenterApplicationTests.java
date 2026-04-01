
package net.platform.center;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import net.platform.center.util.UUIDGenerator;

@Profile("junit")
class CenterApplicationTests {

    @Test
    void contextLoads() {
        assertEquals(24, UUIDGenerator.getLength24().length());
    }

}
