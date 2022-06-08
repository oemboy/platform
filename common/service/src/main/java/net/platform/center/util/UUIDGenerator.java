
package net.platform.center.util;

import java.util.UUID;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class UUIDGenerator {
    private UUIDGenerator() {
    }

    public static String getLength8() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0];
    }

    public static String getLength12() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1];
    }

    public static String getLength16() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2];
    }

    public static String getLength20() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2] + ids[3];
    }

    public static String getLength24() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[4];
    }

    public static String getLength32() {
        UUID id = UUID.randomUUID();
        String[] ids = id.toString().split("-");
        return ids[0] + ids[1] + ids[2] + ids[3] + ids[4];
    }
}
