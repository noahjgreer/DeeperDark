/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.encryption;

import java.security.SignatureException;

@FunctionalInterface
public static interface SignatureUpdatable.SignatureUpdater {
    public void update(byte[] var1) throws SignatureException;
}
