package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record GlobalPos(RegistryKey dimension, BlockPos pos) {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(World.CODEC.fieldOf("dimension").forGetter(GlobalPos::dimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::pos)).apply(instance, GlobalPos::create);
   });
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public GlobalPos(RegistryKey dimension, BlockPos pos) {
      this.dimension = dimension;
      this.pos = pos;
   }

   public static GlobalPos create(RegistryKey dimension, BlockPos pos) {
      return new GlobalPos(dimension, pos);
   }

   public String toString() {
      String var10000 = String.valueOf(this.dimension);
      return var10000 + " " + String.valueOf(this.pos);
   }

   public boolean isWithinRange(RegistryKey dimension, BlockPos otherPos, int maxDistance) {
      return this.dimension.equals(dimension) && this.pos.getChebyshevDistance(otherPos) <= maxDistance;
   }

   public RegistryKey dimension() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   static {
      CODEC = MAP_CODEC.codec();
      PACKET_CODEC = PacketCodec.tuple(RegistryKey.createPacketCodec(RegistryKeys.WORLD), GlobalPos::dimension, BlockPos.PACKET_CODEC, GlobalPos::pos, GlobalPos::create);
   }
}
