/*
 * Md5.java
 * classes : com.cloud.app.downimg.Md5
 * author Andrew Lee
 * V 1.0.0
 * Create at 2014��6��5�� ����4:06:55
 * Copyright: 2014 Interstellar Cloud Inc. All rights reserved.
 */
package com.jinr.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * com.cloud.app.downimg.Md5
 *
 * @author Andrew Lee <br/>
 *         create at 2014��6��5�� ����4:06:55
 */
public class MD5 {
    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}
