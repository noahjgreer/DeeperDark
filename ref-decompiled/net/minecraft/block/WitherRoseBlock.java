package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherRoseBlock extends FlowerBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(STEW_EFFECT_CODEC.forGetter(FlowerBlock::getStewEffects), createSettingsCodec()).apply(instance, WitherRoseBlock::new);
   });

   public MapCodec getCodec() {
      return CODEC;
   }

   public WitherRoseBlock(RegistryEntry registryEntry, float f, AbstractBlock.Settings settings) {
      this(createStewEffectList(registryEntry, f), settings);
   }

   public WitherRoseBlock(SuspiciousStewEffectsComponent suspiciousStewEffectsComponent, AbstractBlock.Settings settings) {
      super(suspiciousStewEffectsComponent, settings);
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.NETHERRACK) || floor.isOf(Blocks.SOUL_SAND) || floor.isOf(Blocks.SOUL_SOIL);
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      VoxelShape voxelShape = this.getOutlineShape(state, world, pos, ShapeContext.absent());
      Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
      double d = (double)pos.getX() + vec3d.x;
      double e = (double)pos.getZ() + vec3d.z;

      for(int i = 0; i < 3; ++i) {
         if (random.nextBoolean()) {
            world.addParticleClient(ParticleTypes.SMOKE, d + random.nextDouble() / 5.0, (double)pos.getY() + (0.5 - random.nextDouble()), e + random.nextDouble() / 5.0, 0.0, 0.0, 0.0);
         }
      }

   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (world instanceof ServerWorld serverWorld) {
         if (world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof LivingEntity livingEntity) {
            if (!livingEntity.isInvulnerableTo(serverWorld, world.getDamageSources().wither())) {
               livingEntity.addStatusEffect(this.getContactEffect());
            }
         }
      }

   }

   public StatusEffectInstance getContactEffect() {
      return new StatusEffectInstance(StatusEffects.WITHER, 40);
   }
}
