/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.encryption;

import java.security.SecureRandom;

public static class NetworkEncryptionUtils.SecureRandomUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static long nextLong() {
        return SECURE_RANDOM.nextLong();
    }
}
