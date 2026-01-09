package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public record CommonPlayerSpawnInfo(RegistryEntry dimensionType, RegistryKey dimension, long seed, GameMode gameMode, @Nullable GameMode lastGameMode, boolean isDebug, boolean isFlat, Optional lastDeathLocation, int portalCooldown, int seaLevel) {
   public CommonPlayerSpawnInfo(RegistryByteBuf buf) {
      this((RegistryEntry)DimensionType.PACKET_CODEC.decode(buf), buf.readRegistryKey(RegistryKeys.WORLD), buf.readLong(), GameMode.byIndex(buf.readByte()), GameMode.getOrNull(buf.readByte()), buf.readBoolean(), buf.readBoolean(), buf.readOptional(PacketByteBuf::readGlobalPos), buf.readVarInt(), buf.readVarInt());
   }

   public CommonPlayerSpawnInfo(RegistryEntry registryEntry, RegistryKey registryKey, long l, GameMode gameMode, @Nullable GameMode gameMode2, boolean bl, boolean bl2, Optional optional, int i, int j) {
      this.dimensionType = registryEntry;
      this.dimension = registryKey;
      this.seed = l;
      this.gameMode = gameMode;
      this.lastGameMode = gameMode2;
      this.isDebug = bl;
      this.isFlat = bl2;
      this.lastDeathLocation = optional;
      this.portalCooldown = i;
      this.seaLevel = j;
   }

   public void write(RegistryByteBuf buf) {
      DimensionType.PACKET_CODEC.encode(buf, this.dimensionType);
      buf.writeRegistryKey(this.dimension);
      buf.writeLong(this.seed);
      buf.writeByte(this.gameMode.getIndex());
      buf.writeByte(GameMode.getId(this.lastGameMode));
      buf.writeBoolean(this.isDebug);
      buf.writeBoolean(this.isFlat);
      buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
      buf.writeVarInt(this.portalCooldown);
      buf.writeVarInt(this.seaLevel);
   }

   public RegistryEntry dimensionType() {
      return this.dimensionType;
   }

   public RegistryKey dimension() {
      return this.dimension;
   }

   public long seed() {
      return this.seed;
   }

   public GameMode gameMode() {
      return this.gameMode;
   }

   @Nullable
   public GameMode lastGameMode() {
      return this.lastGameMode;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }

   public Optional lastDeathLocation() {
      return this.lastDeathLocation;
   }

   public int portalCooldown() {
      return this.portalCooldown;
   }

   public int seaLevel() {
      return this.seaLevel;
   }
}
