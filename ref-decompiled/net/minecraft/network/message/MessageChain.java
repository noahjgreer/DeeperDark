package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.encryption.Signer;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MessageChain {
   static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   MessageLink link;
   Instant lastTimestamp;

   public MessageChain(UUID sender, UUID sessionId) {
      this.lastTimestamp = Instant.EPOCH;
      this.link = MessageLink.of(sender, sessionId);
   }

   public Packer getPacker(Signer signer) {
      return (body) -> {
         MessageLink messageLink = this.link;
         if (messageLink == null) {
            return null;
         } else {
            this.link = messageLink.next();
            return new MessageSignatureData(signer.sign((updatable) -> {
               SignedMessage.update(updatable, messageLink, body);
            }));
         }
      };
   }

   public Unpacker getUnpacker(final PlayerPublicKey playerPublicKey) {
      final SignatureVerifier signatureVerifier = playerPublicKey.createSignatureInstance();
      return new Unpacker() {
         public SignedMessage unpack(@Nullable MessageSignatureData messageSignatureData, MessageBody messageBody) throws MessageChainException {
            if (messageSignatureData == null) {
               throw new MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
            } else if (playerPublicKey.data().isExpired()) {
               throw new MessageChainException(MessageChain.MessageChainException.EXPIRED_PROFILE_KEY_EXCEPTION);
            } else {
               MessageLink messageLink = MessageChain.this.link;
               if (messageLink == null) {
                  throw new MessageChainException(MessageChain.MessageChainException.CHAIN_BROKEN_EXCEPTION);
               } else if (messageBody.timestamp().isBefore(MessageChain.this.lastTimestamp)) {
                  this.setChainBroken();
                  throw new MessageChainException(MessageChain.MessageChainException.OUT_OF_ORDER_CHAT_EXCEPTION);
               } else {
                  MessageChain.this.lastTimestamp = messageBody.timestamp();
                  SignedMessage signedMessage = new SignedMessage(messageLink, messageSignatureData, messageBody, (Text)null, FilterMask.PASS_THROUGH);
                  if (!signedMessage.verify(signatureVerifier)) {
                     this.setChainBroken();
                     throw new MessageChainException(MessageChain.MessageChainException.INVALID_SIGNATURE_EXCEPTION);
                  } else {
                     if (signedMessage.isExpiredOnServer(Instant.now())) {
                        MessageChain.LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", messageBody.content());
                     }

                     MessageChain.this.link = messageLink.next();
                     return signedMessage;
                  }
               }
            }
         }

         public void setChainBroken() {
            MessageChain.this.link = null;
         }
      };
   }

   @FunctionalInterface
   public interface Packer {
      Packer NONE = (body) -> {
         return null;
      };

      @Nullable
      MessageSignatureData pack(MessageBody body);
   }

   public static class MessageChainException extends TextifiedException {
      static final Text MISSING_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.missingProfileKey");
      static final Text CHAIN_BROKEN_EXCEPTION = Text.translatable("chat.disabled.chain_broken");
      static final Text EXPIRED_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.expiredProfileKey");
      static final Text INVALID_SIGNATURE_EXCEPTION = Text.translatable("chat.disabled.invalid_signature");
      static final Text OUT_OF_ORDER_CHAT_EXCEPTION = Text.translatable("chat.disabled.out_of_order_chat");

      public MessageChainException(Text message) {
         super(message);
      }
   }

   @FunctionalInterface
   public interface Unpacker {
      static Unpacker unsigned(UUID sender, BooleanSupplier secureProfileEnforced) {
         return (signature, body) -> {
            if (secureProfileEnforced.getAsBoolean()) {
               throw new MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
            } else {
               return SignedMessage.ofUnsigned(sender, body.content());
            }
         };
      }

      SignedMessage unpack(@Nullable MessageSignatureData signature, MessageBody body) throws MessageChainException;

      default void setChainBroken() {
      }
   }
}
