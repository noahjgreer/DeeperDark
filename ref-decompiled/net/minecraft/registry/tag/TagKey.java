package net.minecraft.registry.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.fabricmc.fabric.api.tag.FabricTagKey;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public record TagKey(RegistryKey registryRef, Identifier id) implements FabricTagKey {
   private static final Interner INTERNER = Interners.newWeakInterner();

   /** @deprecated */
   @Deprecated
   public TagKey(RegistryKey registryRef, Identifier id) {
      this.registryRef = registryRef;
      this.id = id;
   }

   public static Codec unprefixedCodec(RegistryKey registryRef) {
      return Identifier.CODEC.xmap((id) -> {
         return of(registryRef, id);
      }, TagKey::id);
   }

   public static Codec codec(RegistryKey registryRef) {
      return Codec.STRING.comapFlatMap((string) -> {
         return string.startsWith("#") ? Identifier.validate(string.substring(1)).map((id) -> {
            return of(registryRef, id);
         }) : DataResult.error(() -> {
            return "Not a tag id";
         });
      }, (string) -> {
         return "#" + String.valueOf(string.id);
      });
   }

   public static PacketCodec packetCodec(RegistryKey registryRef) {
      return Identifier.PACKET_CODEC.xmap((id) -> {
         return of(registryRef, id);
      }, TagKey::id);
   }

   public static TagKey of(RegistryKey registryRef, Identifier id) {
      return (TagKey)INTERNER.intern(new TagKey(registryRef, id));
   }

   public boolean isOf(RegistryKey registryRef) {
      return this.registryRef == registryRef;
   }

   public Optional tryCast(RegistryKey registryRef) {
      return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      String var10000 = String.valueOf(this.registryRef.getValue());
      return "TagKey[" + var10000 + " / " + String.valueOf(this.id) + "]";
   }

   public RegistryKey registryRef() {
      return this.registryRef;
   }

   public Identifier id() {
      return this.id;
   }
}
