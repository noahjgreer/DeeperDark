package net.minecraft.item.map;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record MapDecorationType(Identifier assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
   public static final int NO_MAP_COLOR = -1;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public MapDecorationType(Identifier identifier, boolean bl, int i, boolean bl2, boolean bl3) {
      this.assetId = identifier;
      this.showOnItemFrame = bl;
      this.mapColor = i;
      this.explorationMapElement = bl2;
      this.trackCount = bl3;
   }

   public boolean hasMapColor() {
      return this.mapColor != -1;
   }

   public Identifier assetId() {
      return this.assetId;
   }

   public boolean showOnItemFrame() {
      return this.showOnItemFrame;
   }

   public int mapColor() {
      return this.mapColor;
   }

   public boolean explorationMapElement() {
      return this.explorationMapElement;
   }

   public boolean trackCount() {
      return this.trackCount;
   }

   static {
      CODEC = Registries.MAP_DECORATION_TYPE.getEntryCodec();
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.MAP_DECORATION_TYPE);
   }
}
