package net.minecraft.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public interface ComponentType {
   Codec CODEC = Codec.lazyInitialized(() -> {
      return Registries.DATA_COMPONENT_TYPE.getCodec();
   });
   PacketCodec PACKET_CODEC = PacketCodec.recursive((packetCodec) -> {
      return PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE);
   });
   Codec PERSISTENT_CODEC = CODEC.validate((componentType) -> {
      return componentType.shouldSkipSerialization() ? DataResult.error(() -> {
         return "Encountered transient component " + String.valueOf(Registries.DATA_COMPONENT_TYPE.getId(componentType));
      }) : DataResult.success(componentType);
   });
   Codec TYPE_TO_VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, ComponentType::getCodecOrThrow);

   static Builder builder() {
      return new Builder();
   }

   @Nullable
   Codec getCodec();

   default Codec getCodecOrThrow() {
      Codec codec = this.getCodec();
      if (codec == null) {
         throw new IllegalStateException(String.valueOf(this) + " is not a persistent component");
      } else {
         return codec;
      }
   }

   default boolean shouldSkipSerialization() {
      return this.getCodec() == null;
   }

   PacketCodec getPacketCodec();

   public static class Builder {
      @Nullable
      private Codec codec;
      @Nullable
      private PacketCodec packetCodec;
      private boolean cache;

      public Builder codec(Codec codec) {
         this.codec = codec;
         return this;
      }

      public Builder packetCodec(PacketCodec packetCodec) {
         this.packetCodec = packetCodec;
         return this;
      }

      public Builder cache() {
         this.cache = true;
         return this;
      }

      public ComponentType build() {
         PacketCodec packetCodec = (PacketCodec)Objects.requireNonNullElseGet(this.packetCodec, () -> {
            return PacketCodecs.registryCodec((Codec)Objects.requireNonNull(this.codec, "Missing Codec for component"));
         });
         Codec codec = this.cache && this.codec != null ? DataComponentTypes.CACHE.wrap(this.codec) : this.codec;
         return new SimpleDataComponentType(codec, packetCodec);
      }

      static class SimpleDataComponentType implements ComponentType {
         @Nullable
         private final Codec codec;
         private final PacketCodec packetCodec;

         SimpleDataComponentType(@Nullable Codec codec, PacketCodec packetCodec) {
            this.codec = codec;
            this.packetCodec = packetCodec;
         }

         @Nullable
         public Codec getCodec() {
            return this.codec;
         }

         public PacketCodec getPacketCodec() {
            return this.packetCodec;
         }

         public String toString() {
            return Util.registryValueToString(Registries.DATA_COMPONENT_TYPE, this);
         }
      }
   }
}
