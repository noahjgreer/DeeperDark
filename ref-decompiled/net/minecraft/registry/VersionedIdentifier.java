package net.minecraft.registry;

import net.minecraft.SharedConstants;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record VersionedIdentifier(String namespace, String id, String version) {
   public static final PacketCodec PACKET_CODEC;
   public static final String DEFAULT_NAMESPACE = "minecraft";

   public VersionedIdentifier(String string, String string2, String string3) {
      this.namespace = string;
      this.id = string2;
      this.version = string3;
   }

   public static VersionedIdentifier createVanilla(String path) {
      return new VersionedIdentifier("minecraft", path, SharedConstants.getGameVersion().id());
   }

   public boolean isVanilla() {
      return this.namespace.equals("minecraft");
   }

   public String toString() {
      return this.namespace + ":" + this.id + ":" + this.version;
   }

   public String namespace() {
      return this.namespace;
   }

   public String id() {
      return this.id;
   }

   public String version() {
      return this.version;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, VersionedIdentifier::namespace, PacketCodecs.STRING, VersionedIdentifier::id, PacketCodecs.STRING, VersionedIdentifier::version, VersionedIdentifier::new);
   }
}
