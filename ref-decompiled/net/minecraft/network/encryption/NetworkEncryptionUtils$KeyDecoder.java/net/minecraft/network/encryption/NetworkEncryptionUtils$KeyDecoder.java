/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.encryption;

import java.security.Key;
import net.minecraft.network.encryption.NetworkEncryptionException;

static interface NetworkEncryptionUtils.KeyDecoder<T extends Key> {
    public T apply(byte[] var1) throws NetworkEncryptionException;
}
