/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import net.minecraft.network.message.MessageSignatureData;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface ArgumentSignatureDataMap.ArgumentSigner {
    public @Nullable MessageSignatureData sign(String var1);
}
