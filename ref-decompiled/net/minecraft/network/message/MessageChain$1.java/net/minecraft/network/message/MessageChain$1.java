/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.message;

import java.time.Instant;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import org.jspecify.annotations.Nullable;

class MessageChain.1
implements MessageChain.Unpacker {
    final /* synthetic */ PlayerPublicKey field_50253;
    final /* synthetic */ SignatureVerifier field_50254;

    MessageChain.1() {
        this.field_50253 = playerPublicKey;
        this.field_50254 = signatureVerifier;
    }

    @Override
    public SignedMessage unpack(@Nullable MessageSignatureData messageSignatureData, MessageBody messageBody) throws MessageChain.MessageChainException {
        if (messageSignatureData == null) {
            throw new MessageChain.MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
        }
        if (this.field_50253.data().isExpired()) {
            throw new MessageChain.MessageChainException(MessageChain.MessageChainException.EXPIRED_PROFILE_KEY_EXCEPTION);
        }
        MessageLink messageLink = MessageChain.this.link;
        if (messageLink == null) {
            throw new MessageChain.MessageChainException(MessageChain.MessageChainException.CHAIN_BROKEN_EXCEPTION);
        }
        if (messageBody.timestamp().isBefore(MessageChain.this.lastTimestamp)) {
            this.setChainBroken();
            throw new MessageChain.MessageChainException(MessageChain.MessageChainException.OUT_OF_ORDER_CHAT_EXCEPTION);
        }
        MessageChain.this.lastTimestamp = messageBody.timestamp();
        SignedMessage signedMessage = new SignedMessage(messageLink, messageSignatureData, messageBody, null, FilterMask.PASS_THROUGH);
        if (!signedMessage.verify(this.field_50254)) {
            this.setChainBroken();
            throw new MessageChain.MessageChainException(MessageChain.MessageChainException.INVALID_SIGNATURE_EXCEPTION);
        }
        if (signedMessage.isExpiredOnServer(Instant.now())) {
            LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", (Object)messageBody.content());
        }
        MessageChain.this.link = messageLink.next();
        return signedMessage;
    }

    @Override
    public void setChainBroken() {
        MessageChain.this.link = null;
    }
}
