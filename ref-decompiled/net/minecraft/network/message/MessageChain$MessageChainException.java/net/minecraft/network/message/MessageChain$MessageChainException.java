/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.message;

import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;

public static class MessageChain.MessageChainException
extends TextifiedException {
    static final Text MISSING_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.missingProfileKey");
    static final Text CHAIN_BROKEN_EXCEPTION = Text.translatable("chat.disabled.chain_broken");
    static final Text EXPIRED_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.expiredProfileKey");
    static final Text INVALID_SIGNATURE_EXCEPTION = Text.translatable("chat.disabled.invalid_signature");
    static final Text OUT_OF_ORDER_CHAT_EXCEPTION = Text.translatable("chat.disabled.out_of_order_chat");

    public MessageChain.MessageChainException(Text message) {
        super(message);
    }
}
