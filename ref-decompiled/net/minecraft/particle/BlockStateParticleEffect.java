package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.particle.v1.FabricBlockStateParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;

public class BlockStateParticleEffect implements ParticleEffect, FabricBlockStateParticleEffect {
   private static final Codec BLOCK_STATE_CODEC;
   private final ParticleType type;
   private final BlockState blockState;

   public static MapCodec createCodec(ParticleType type) {
      return BLOCK_STATE_CODEC.xmap((state) -> {
         return new BlockStateParticleEffect(type, state);
      }, (effect) -> {
         return effect.blockState;
      }).fieldOf("block_state");
   }

   public static PacketCodec createPacketCodec(ParticleType type) {
      return PacketCodecs.entryOf(Block.STATE_IDS).xmap((state) -> {
         return new BlockStateParticleEffect(type, state);
      }, (effect) -> {
         return effect.blockState;
      });
   }

   public BlockStateParticleEffect(ParticleType type, BlockState blockState) {
      this.type = type;
      this.blockState = blockState;
   }

   public ParticleType getType() {
      return this.type;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   static {
      BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, Registries.BLOCK.getCodec(), Block::getDefaultState);
   }
}
