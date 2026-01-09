package net.minecraft.world.event;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockPositionSource implements PositionSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter((blockPositionSource) -> {
         return blockPositionSource.pos;
      })).apply(instance, BlockPositionSource::new);
   });
   public static final PacketCodec PACKET_CODEC;
   private final BlockPos pos;

   public BlockPositionSource(BlockPos pos) {
      this.pos = pos;
   }

   public Optional getPos(World world) {
      return Optional.of(Vec3d.ofCenter(this.pos));
   }

   public PositionSourceType getType() {
      return PositionSourceType.BLOCK;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, (source) -> {
         return source.pos;
      }, BlockPositionSource::new);
   }

   public static class Type implements PositionSourceType {
      public MapCodec getCodec() {
         return BlockPositionSource.CODEC;
      }

      public PacketCodec getPacketCodec() {
         return BlockPositionSource.PACKET_CODEC;
      }
   }
}
