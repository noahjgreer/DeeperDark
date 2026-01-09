package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record SignedMessage(MessageLink link, @Nullable MessageSignatureData signature, MessageBody signedBody, @Nullable Text unsignedContent, FilterMask filterMask) {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(MessageLink.CODEC.fieldOf("link").forGetter(SignedMessage::link), MessageSignatureData.CODEC.optionalFieldOf("signature").forGetter((message) -> {
         return Optional.ofNullable(message.signature);
      }), MessageBody.CODEC.forGetter(SignedMessage::signedBody), TextCodecs.CODEC.optionalFieldOf("unsigned_content").forGetter((message) -> {
         return Optional.ofNullable(message.unsignedContent);
      }), FilterMask.CODEC.optionalFieldOf("filter_mask", FilterMask.PASS_THROUGH).forGetter(SignedMessage::filterMask)).apply(instance, (link, signature, signedBody, unsignedContent, filterMask) -> {
         return new SignedMessage(link, (MessageSignatureData)signature.orElse((Object)null), signedBody, (Text)unsignedContent.orElse((Object)null), filterMask);
      });
   });
   private static final UUID NIL_UUID;
   public static final Duration SERVERBOUND_TIME_TO_LIVE;
   public static final Duration CLIENTBOUND_TIME_TO_LIVE;

   public SignedMessage(MessageLink messageLink, @Nullable MessageSignatureData messageSignatureData, MessageBody messageBody, @Nullable Text text, FilterMask filterMask) {
      this.link = messageLink;
      this.signature = messageSignatureData;
      this.signedBody = messageBody;
      this.unsignedContent = text;
      this.filterMask = filterMask;
   }

   public static SignedMessage ofUnsigned(String content) {
      return ofUnsigned(NIL_UUID, content);
   }

   public static SignedMessage ofUnsigned(UUID sender, String content) {
      MessageBody messageBody = MessageBody.ofUnsigned(content);
      MessageLink messageLink = MessageLink.of(sender);
      return new SignedMessage(messageLink, (MessageSignatureData)null, messageBody, (Text)null, FilterMask.PASS_THROUGH);
   }

   public SignedMessage withUnsignedContent(Text unsignedContent) {
      Text text = !unsignedContent.equals(Text.literal(this.getSignedContent())) ? unsignedContent : null;
      return new SignedMessage(this.link, this.signature, this.signedBody, text, this.filterMask);
   }

   public SignedMessage withoutUnsigned() {
      return this.unsignedContent != null ? new SignedMessage(this.link, this.signature, this.signedBody, (Text)null, this.filterMask) : this;
   }

   public SignedMessage withFilterMask(FilterMask filterMask) {
      return this.filterMask.equals(filterMask) ? this : new SignedMessage(this.link, this.signature, this.signedBody, this.unsignedContent, filterMask);
   }

   public SignedMessage withFilterMaskEnabled(boolean enabled) {
      return this.withFilterMask(enabled ? this.filterMask : FilterMask.PASS_THROUGH);
   }

   public SignedMessage stripSignature() {
      MessageBody messageBody = MessageBody.ofUnsigned(this.getSignedContent());
      MessageLink messageLink = MessageLink.of(this.getSender());
      return new SignedMessage(messageLink, (MessageSignatureData)null, messageBody, this.unsignedContent, this.filterMask);
   }

   public static void update(SignatureUpdatable.SignatureUpdater updater, MessageLink link, MessageBody body) throws SignatureException {
      updater.update(Ints.toByteArray(1));
      link.update(updater);
      body.update(updater);
   }

   public boolean verify(SignatureVerifier verifier) {
      return this.signature != null && this.signature.verify(verifier, (updater) -> {
         update(updater, this.link, this.signedBody);
      });
   }

   public String getSignedContent() {
      return this.signedBody.content();
   }

   public Text getContent() {
      return (Text)Objects.requireNonNullElseGet(this.unsignedContent, () -> {
         return Text.literal(this.getSignedContent());
      });
   }

   public Instant getTimestamp() {
      return this.signedBody.timestamp();
   }

   public long getSalt() {
      return this.signedBody.salt();
   }

   public boolean isExpiredOnServer(Instant currentTime) {
      return currentTime.isAfter(this.getTimestamp().plus(SERVERBOUND_TIME_TO_LIVE));
   }

   public boolean isExpiredOnClient(Instant currentTime) {
      return currentTime.isAfter(this.getTimestamp().plus(CLIENTBOUND_TIME_TO_LIVE));
   }

   public UUID getSender() {
      return this.link.sender();
   }

   public boolean isSenderMissing() {
      return this.getSender().equals(NIL_UUID);
   }

   public boolean hasSignature() {
      return this.signature != null;
   }

   public boolean canVerifyFrom(UUID sender) {
      return this.hasSignature() && this.link.sender().equals(sender);
   }

   public boolean isFullyFiltered() {
      return this.filterMask.isFullyFiltered();
   }

   public static String toString(SignedMessage message) {
      String var10000 = message.signedBody.content();
      return "'" + var10000 + "' @ " + String.valueOf(message.signedBody.timestamp()) + "\n - From: " + String.valueOf(message.link.sender()) + "/" + String.valueOf(message.link.sessionId()) + ", message #" + message.link.index() + "\n - Salt: " + message.signedBody.salt() + "\n - Signature: " + MessageSignatureData.toString(message.signature) + "\n - Last Seen: [\n" + (String)message.signedBody.lastSeenMessages().entries().stream().map((entry) -> {
         return "     " + MessageSignatureData.toString(entry) + "\n";
      }).collect(Collectors.joining()) + " ]\n";
   }

   public MessageLink link() {
      return this.link;
   }

   @Nullable
   public MessageSignatureData signature() {
      return this.signature;
   }

   public MessageBody signedBody() {
      return this.signedBody;
   }

   @Nullable
   public Text unsignedContent() {
      return this.unsignedContent;
   }

   public FilterMask filterMask() {
      return this.filterMask;
   }

   static {
      NIL_UUID = Util.NIL_UUID;
      SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
      CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));
   }
}
