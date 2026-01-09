package net.minecraft.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

public class PacketCodecDispatcher implements PacketCodec {
   private static final int UNKNOWN_PACKET_INDEX = -1;
   private final Function packetIdGetter;
   private final List packetTypes;
   private final Object2IntMap typeToIndex;

   PacketCodecDispatcher(Function packetIdGetter, List packetTypes, Object2IntMap typeToIndex) {
      this.packetIdGetter = packetIdGetter;
      this.packetTypes = packetTypes;
      this.typeToIndex = typeToIndex;
   }

   public Object decode(ByteBuf byteBuf) {
      int i = VarInts.read(byteBuf);
      if (i >= 0 && i < this.packetTypes.size()) {
         PacketType packetType = (PacketType)this.packetTypes.get(i);

         try {
            return packetType.codec.decode(byteBuf);
         } catch (Exception var5) {
            if (var5 instanceof UndecoratedException) {
               throw var5;
            } else {
               throw new DecoderException("Failed to decode packet '" + String.valueOf(packetType.id) + "'", var5);
            }
         }
      } else {
         throw new DecoderException("Received unknown packet id " + i);
      }
   }

   public void encode(ByteBuf byteBuf, Object object) {
      Object object2 = this.packetIdGetter.apply(object);
      int i = this.typeToIndex.getOrDefault(object2, -1);
      if (i == -1) {
         throw new EncoderException("Sending unknown packet '" + String.valueOf(object2) + "'");
      } else {
         VarInts.write(byteBuf, i);
         PacketType packetType = (PacketType)this.packetTypes.get(i);

         try {
            PacketCodec packetCodec = packetType.codec;
            packetCodec.encode(byteBuf, object);
         } catch (Exception var7) {
            if (var7 instanceof UndecoratedException) {
               throw var7;
            } else {
               throw new EncoderException("Failed to encode packet '" + String.valueOf(object2) + "'", var7);
            }
         }
      }
   }

   public static Builder builder(Function packetIdGetter) {
      return new Builder(packetIdGetter);
   }

   // $FF: synthetic method
   public void encode(final Object object, final Object object2) {
      this.encode((ByteBuf)object, object2);
   }

   // $FF: synthetic method
   public Object decode(final Object object) {
      return this.decode((ByteBuf)object);
   }

   private static record PacketType(PacketCodec codec, Object id) {
      final PacketCodec codec;
      final Object id;

      PacketType(PacketCodec packetCodec, Object object) {
         this.codec = packetCodec;
         this.id = object;
      }

      public PacketCodec codec() {
         return this.codec;
      }

      public Object id() {
         return this.id;
      }
   }

   public interface UndecoratedException {
   }

   public static class Builder {
      private final List packetTypes = new ArrayList();
      private final Function packetIdGetter;

      Builder(Function packetIdGetter) {
         this.packetIdGetter = packetIdGetter;
      }

      public Builder add(Object id, PacketCodec codec) {
         this.packetTypes.add(new PacketType(codec, id));
         return this;
      }

      public PacketCodecDispatcher build() {
         Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
         object2IntOpenHashMap.defaultReturnValue(-2);
         Iterator var2 = this.packetTypes.iterator();

         PacketType packetType;
         int j;
         do {
            if (!var2.hasNext()) {
               return new PacketCodecDispatcher(this.packetIdGetter, List.copyOf(this.packetTypes), object2IntOpenHashMap);
            }

            packetType = (PacketType)var2.next();
            int i = object2IntOpenHashMap.size();
            j = object2IntOpenHashMap.putIfAbsent(packetType.id, i);
         } while(j == -2);

         throw new IllegalStateException("Duplicate registration for type " + String.valueOf(packetType.id));
      }
   }
}
