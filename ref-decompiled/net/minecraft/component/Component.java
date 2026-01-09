package net.minecraft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record Component(ComponentType type, Object value) {
   public static final PacketCodec PACKET_CODEC = new PacketCodec() {
      public Component decode(RegistryByteBuf registryByteBuf) {
         ComponentType componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
         return read(registryByteBuf, componentType);
      }

      private static Component read(RegistryByteBuf buf, ComponentType type) {
         return new Component(type, type.getPacketCodec().decode(buf));
      }

      public void encode(RegistryByteBuf registryByteBuf, Component component) {
         write(registryByteBuf, component);
      }

      private static void write(RegistryByteBuf buf, Component component) {
         ComponentType.PACKET_CODEC.encode(buf, component.type());
         component.type().getPacketCodec().encode(buf, component.value());
      }

      // $FF: synthetic method
      public void encode(final Object object, final Object object2) {
         this.encode((RegistryByteBuf)object, (Component)object2);
      }

      // $FF: synthetic method
      public Object decode(final Object object) {
         return this.decode((RegistryByteBuf)object);
      }
   };

   public Component(ComponentType componentType, Object object) {
      this.type = componentType;
      this.value = object;
   }

   static Component of(Map.Entry entry) {
      return of((ComponentType)entry.getKey(), entry.getValue());
   }

   public static Component of(ComponentType type, Object value) {
      return new Component(type, value);
   }

   public void apply(MergedComponentMap components) {
      components.set(this.type, this.value);
   }

   public DataResult encode(DynamicOps ops) {
      Codec codec = this.type.getCodec();
      return codec == null ? DataResult.error(() -> {
         return "Component of type " + String.valueOf(this.type) + " is not encodable";
      }) : codec.encodeStart(ops, this.value);
   }

   public String toString() {
      String var10000 = String.valueOf(this.type);
      return var10000 + "=>" + String.valueOf(this.value);
   }

   public ComponentType type() {
      return this.type;
   }

   public Object value() {
      return this.value;
   }
}
