package net.minecraft.network.packet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.util.Identifier;

public interface CustomPayload {
   Id getId();

   static PacketCodec codecOf(ValueFirstEncoder encoder, PacketDecoder decoder) {
      return PacketCodec.of(encoder, decoder);
   }

   static Id id(String id) {
      return new Id(Identifier.ofVanilla(id));
   }

   static PacketCodec createCodec(final CodecFactory unknownCodecFactory, List types) {
      final Map map = (Map)types.stream().collect(Collectors.toUnmodifiableMap((type) -> {
         return type.id().id();
      }, Type::codec));
      return new PacketCodec() {
         private PacketCodec getCodec(Identifier id) {
            PacketCodec packetCodec = (PacketCodec)map.get(id);
            return packetCodec != null ? packetCodec : unknownCodecFactory.create(id);
         }

         private void encode(PacketByteBuf value, Id id, CustomPayload payload) {
            value.writeIdentifier(id.id());
            PacketCodec packetCodec = this.getCodec(id.id);
            packetCodec.encode(value, payload);
         }

         public void encode(PacketByteBuf packetByteBuf, CustomPayload customPayload) {
            this.encode(packetByteBuf, customPayload.getId(), customPayload);
         }

         public CustomPayload decode(PacketByteBuf packetByteBuf) {
            Identifier identifier = packetByteBuf.readIdentifier();
            return (CustomPayload)this.getCodec(identifier).decode(packetByteBuf);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((PacketByteBuf)object, (CustomPayload)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((PacketByteBuf)object);
         }
      };
   }

   public static record Id(Identifier id) {
      final Identifier id;

      public Id(Identifier identifier) {
         this.id = identifier;
      }

      public Identifier id() {
         return this.id;
      }
   }

   public interface CodecFactory {
      PacketCodec create(Identifier id);
   }

   public static record Type(Id id, PacketCodec codec) {
      public Type(Id id, PacketCodec packetCodec) {
         this.id = id;
         this.codec = packetCodec;
      }

      public Id id() {
         return this.id;
      }

      public PacketCodec codec() {
         return this.codec;
      }
   }
}
