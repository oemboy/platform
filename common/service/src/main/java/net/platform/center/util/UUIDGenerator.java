
package net.platform.center.util;

import java.util.UUID;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class UUIDGenerator {
    private UUIDGenerator() {
    }

    public static String get8UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0];
    }

    public static String get12UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1];
    }

    public static String get16UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2];
    }

    public static String get20UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2] + ids[3];
    }

    public static String get24UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[4];
    }

    public static String get32UUID() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2] + ids[3] + ids[4];
    }
}
