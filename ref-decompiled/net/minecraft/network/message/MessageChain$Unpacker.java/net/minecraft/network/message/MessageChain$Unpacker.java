/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public static interface MessageChain.Unpacker {
    public static MessageChain.Unpacker unsigned(UUID sender, BooleanSupplier secureProfileEnforced) {
        return (signature, body) -> {
            if (secureProfileEnforced.getAsBoolean()) {
                throw new MessageChain.MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
            }
            return SignedMessage.ofUnsigned(sender, body.content());
        };
    }

    public SignedMessage unpack(@Nullable MessageSignatureData var1, MessageBody var2) throws MessageChain.MessageChainException;

    default public void setChainBroken() {
    }
}
