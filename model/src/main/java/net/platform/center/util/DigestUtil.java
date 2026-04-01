
package net.platform.center.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author: bing zi
 * @Date: 2022/7/27
 */
public final class DigestUtil {
    public static final String SHA_256 = "SHA-256";

    private DigestUtil() {
    }

    public static String getSha256(String str) throws NoSuchAlgorithmException {
        byte[] bytes = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        MessageDigest messageDigest;

        messageDigest = MessageDigest.getInstance(SHA_256);
        messageDigest.update(bytes);

        StringBuilder sb = new StringBuilder();
        byte[] digest = messageDigest.digest();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
