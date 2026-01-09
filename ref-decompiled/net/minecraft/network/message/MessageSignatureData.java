package net.minecraft.network.message;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record MessageSignatureData(byte[] data) {
   public static final Codec CODEC;
   public static final int SIZE = 256;

   public MessageSignatureData(byte[] bs) {
      Preconditions.checkState(bs.length == 256, "Invalid message signature size");
      this.data = bs;
   }

   public static MessageSignatureData fromBuf(PacketByteBuf buf) {
      byte[] bs = new byte[256];
      buf.readBytes(bs);
      return new MessageSignatureData(bs);
   }

   public static void write(PacketByteBuf buf, MessageSignatureData signature) {
      buf.writeBytes(signature.data);
   }

   public boolean verify(SignatureVerifier verifier, SignatureUpdatable updatable) {
      return verifier.validate(updatable, this.data);
   }

   public ByteBuffer toByteBuffer() {
      return ByteBuffer.wrap(this.data);
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (this != o) {
         label26: {
            if (o instanceof MessageSignatureData) {
               MessageSignatureData messageSignatureData = (MessageSignatureData)o;
               if (Arrays.equals(this.data, messageSignatureData.data)) {
                  break label26;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public String toString() {
      return Base64.getEncoder().encodeToString(this.data);
   }

   public static String toString(@Nullable MessageSignatureData signature) {
      return signature == null ? "<no signature>" : signature.toString();
   }

   public Indexed pack(MessageSignatureStorage storage) {
      int i = storage.indexOf(this);
      return i != -1 ? new Indexed(i) : new Indexed(this);
   }

   public int calculateChecksum() {
      return Arrays.hashCode(this.data);
   }

   public byte[] data() {
      return this.data;
   }

   static {
      CODEC = Codecs.BASE_64.xmap(MessageSignatureData::new, MessageSignatureData::data);
   }

   public static record Indexed(int id, @Nullable MessageSignatureData fullSignature) {
      public static final int MISSING_ID = -1;

      public Indexed(MessageSignatureData signature) {
         this(-1, signature);
      }

      public Indexed(int id) {
         this(id, (MessageSignatureData)null);
      }

      public Indexed(int i, @Nullable MessageSignatureData messageSignatureData) {
         this.id = i;
         this.fullSignature = messageSignatureData;
      }

      public static Indexed fromBuf(PacketByteBuf buf) {
         int i = buf.readVarInt() - 1;
         return i == -1 ? new Indexed(MessageSignatureData.fromBuf(buf)) : new Indexed(i);
      }

      public static void write(PacketByteBuf buf, Indexed indexed) {
         buf.writeVarInt(indexed.id() + 1);
         if (indexed.fullSignature() != null) {
            MessageSignatureData.write(buf, indexed.fullSignature());
         }

      }

      public Optional getSignature(MessageSignatureStorage storage) {
         return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(storage.get(this.id));
      }

      public int id() {
         return this.id;
      }

      @Nullable
      public MessageSignatureData fullSignature() {
         return this.fullSignature;
      }
   }
}
