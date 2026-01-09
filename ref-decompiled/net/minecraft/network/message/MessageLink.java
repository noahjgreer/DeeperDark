package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.util.UUID;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record MessageLink(int index, UUID sender, UUID sessionId) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.fieldOf("index").forGetter(MessageLink::index), Uuids.INT_STREAM_CODEC.fieldOf("sender").forGetter(MessageLink::sender), Uuids.INT_STREAM_CODEC.fieldOf("session_id").forGetter(MessageLink::sessionId)).apply(instance, MessageLink::new);
   });

   public MessageLink(int i, UUID uUID, UUID uUID2) {
      this.index = i;
      this.sender = uUID;
      this.sessionId = uUID2;
   }

   public static MessageLink of(UUID sender) {
      return of(sender, Util.NIL_UUID);
   }

   public static MessageLink of(UUID sender, UUID sessionId) {
      return new MessageLink(0, sender, sessionId);
   }

   public void update(SignatureUpdatable.SignatureUpdater updater) throws SignatureException {
      updater.update(Uuids.toByteArray(this.sender));
      updater.update(Uuids.toByteArray(this.sessionId));
      updater.update(Ints.toByteArray(this.index));
   }

   public boolean linksTo(MessageLink preceding) {
      return this.index > preceding.index() && this.sender.equals(preceding.sender()) && this.sessionId.equals(preceding.sessionId());
   }

   @Nullable
   public MessageLink next() {
      return this.index == Integer.MAX_VALUE ? null : new MessageLink(this.index + 1, this.sender, this.sessionId);
   }

   public int index() {
      return this.index;
   }

   public UUID sender() {
      return this.sender;
   }

   public UUID sessionId() {
      return this.sessionId;
   }
}
