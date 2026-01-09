package net.minecraft.block;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TorchBlock extends AbstractTorchBlock {
   protected static final MapCodec PARTICLE_TYPE_CODEC;
   public static final MapCodec CODEC;
   protected final SimpleParticleType particle;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings) {
      super(settings);
      this.particle = particle;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      double d = (double)pos.getX() + 0.5;
      double e = (double)pos.getY() + 0.7;
      double f = (double)pos.getZ() + 0.5;
      world.addParticleClient(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
      world.addParticleClient(this.particle, d, e, f, 0.0, 0.0, 0.0);
   }

   static {
      PARTICLE_TYPE_CODEC = Registries.PARTICLE_TYPE.getCodec().comapFlatMap((particleType) -> {
         DataResult var10000;
         if (particleType instanceof SimpleParticleType simpleParticleType) {
            var10000 = DataResult.success(simpleParticleType);
         } else {
            var10000 = DataResult.error(() -> {
               return "Not a SimpleParticleType: " + String.valueOf(particleType);
            });
         }

         return var10000;
      }, (particleType) -> {
         return particleType;
      }).fieldOf("particle_options");
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(PARTICLE_TYPE_CODEC.forGetter((block) -> {
            return block.particle;
         }), createSettingsCodec()).apply(instance, TorchBlock::new);
      });
   }
}
