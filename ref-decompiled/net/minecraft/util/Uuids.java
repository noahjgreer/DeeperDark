package net.minecraft.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import com.mojang.util.UndashedUuid;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class Uuids {
   public static final Codec INT_STREAM_CODEC;
   public static final Codec SET_CODEC;
   public static final Codec LINKED_SET_CODEC;
   public static final Codec STRING_CODEC;
   public static final Codec CODEC;
   public static final Codec STRICT_CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final int BYTE_ARRAY_SIZE = 16;
   private static final String OFFLINE_PLAYER_UUID_PREFIX = "OfflinePlayer:";

   private Uuids() {
   }

   public static UUID toUuid(int[] array) {
      return new UUID((long)array[0] << 32 | (long)array[1] & 4294967295L, (long)array[2] << 32 | (long)array[3] & 4294967295L);
   }

   public static int[] toIntArray(UUID uuid) {
      long l = uuid.getMostSignificantBits();
      long m = uuid.getLeastSignificantBits();
      return toIntArray(l, m);
   }

   private static int[] toIntArray(long uuidMost, long uuidLeast) {
      return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
   }

   public static byte[] toByteArray(UUID uuid) {
      byte[] bs = new byte[16];
      ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
      return bs;
   }

   public static UUID toUuid(Dynamic dynamic) {
      int[] is = dynamic.asIntStream().toArray();
      if (is.length != 4) {
         throw new IllegalArgumentException("Could not read UUID. Expected int-array of length 4, got " + is.length + ".");
      } else {
         return toUuid(is);
      }
   }

   public static UUID getOfflinePlayerUuid(String nickname) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
   }

   public static GameProfile getOfflinePlayerProfile(String nickname) {
      UUID uUID = getOfflinePlayerUuid(nickname);
      return new GameProfile(uUID, nickname);
   }

   static {
      INT_STREAM_CODEC = Codec.INT_STREAM.comapFlatMap((uuidStream) -> {
         return Util.decodeFixedLengthArray((IntStream)uuidStream, 4).map(Uuids::toUuid);
      }, (uuid) -> {
         return Arrays.stream(toIntArray(uuid));
      });
      SET_CODEC = Codec.list(INT_STREAM_CODEC).xmap(Sets::newHashSet, Lists::newArrayList);
      LINKED_SET_CODEC = Codec.list(INT_STREAM_CODEC).xmap(Sets::newLinkedHashSet, Lists::newArrayList);
      STRING_CODEC = Codec.STRING.comapFlatMap((string) -> {
         try {
            return DataResult.success(UUID.fromString(string), Lifecycle.stable());
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Invalid UUID " + string + ": " + var2.getMessage();
            });
         }
      }, UUID::toString);
      CODEC = Codec.withAlternative(Codec.STRING.comapFlatMap((string) -> {
         try {
            return DataResult.success(UndashedUuid.fromStringLenient(string), Lifecycle.stable());
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Invalid UUID " + string + ": " + var2.getMessage();
            });
         }
      }, UndashedUuid::toString), INT_STREAM_CODEC);
      STRICT_CODEC = Codec.withAlternative(INT_STREAM_CODEC, STRING_CODEC);
      PACKET_CODEC = new PacketCodec() {
         public UUID decode(ByteBuf byteBuf) {
            return PacketByteBuf.readUuid(byteBuf);
         }

         public void encode(ByteBuf byteBuf, UUID uUID) {
            PacketByteBuf.writeUuid(byteBuf, uUID);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (UUID)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }
}
