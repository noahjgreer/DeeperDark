/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface MessageChain.Packer {
    public static final MessageChain.Packer NONE = body -> null;

    public @Nullable MessageSignatureData pack(MessageBody var1);
}
