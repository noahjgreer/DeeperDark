package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConduitBlockEntity extends BlockEntity {
   private static final int field_31333 = 2;
   private static final int field_31334 = 13;
   private static final float field_31335 = -0.0375F;
   private static final int field_31336 = 16;
   private static final int MIN_BLOCKS_TO_ACTIVATE = 42;
   private static final int field_31338 = 8;
   private static final Block[] ACTIVATING_BLOCKS;
   public int ticks;
   private float ticksActive;
   private boolean active;
   private boolean eyeOpen;
   private final List activatingBlocks = Lists.newArrayList();
   @Nullable
   private LazyEntityReference targetEntity;
   private long nextAmbientSoundTime;

   public ConduitBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.CONDUIT, pos, state);
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.targetEntity = LazyEntityReference.fromData(view, "Target");
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      LazyEntityReference.writeData(this.targetEntity, view, "Target");
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public static void clientTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
      ++blockEntity.ticks;
      long l = world.getTime();
      List list = blockEntity.activatingBlocks;
      if (l % 40L == 0L) {
         blockEntity.active = updateActivatingBlocks(world, pos, list);
         openEye(blockEntity, list);
      }

      LivingEntity livingEntity = (LivingEntity)LazyEntityReference.resolve(blockEntity.targetEntity, world, LivingEntity.class);
      spawnNautilusParticles(world, pos, list, livingEntity, blockEntity.ticks);
      if (blockEntity.isActive()) {
         ++blockEntity.ticksActive;
      }

   }

   public static void serverTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
      ++blockEntity.ticks;
      long l = world.getTime();
      List list = blockEntity.activatingBlocks;
      if (l % 40L == 0L) {
         boolean bl = updateActivatingBlocks(world, pos, list);
         if (bl != blockEntity.active) {
            SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
            world.playSound((Entity)null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         blockEntity.active = bl;
         openEye(blockEntity, list);
         if (bl) {
            givePlayersEffects(world, pos, list);
            tryAttack((ServerWorld)world, pos, state, blockEntity, list.size() >= 42);
         }
      }

      if (blockEntity.isActive()) {
         if (l % 80L == 0L) {
            world.playSound((Entity)null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         if (l > blockEntity.nextAmbientSoundTime) {
            blockEntity.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
            world.playSound((Entity)null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   private static void openEye(ConduitBlockEntity blockEntity, List activatingBlocks) {
      blockEntity.setEyeOpen(activatingBlocks.size() >= 42);
   }

   private static boolean updateActivatingBlocks(World world, BlockPos pos, List activatingBlocks) {
      activatingBlocks.clear();

      int i;
      int j;
      int k;
      for(i = -1; i <= 1; ++i) {
         for(j = -1; j <= 1; ++j) {
            for(k = -1; k <= 1; ++k) {
               BlockPos blockPos = pos.add(i, j, k);
               if (!world.isWater(blockPos)) {
                  return false;
               }
            }
         }
      }

      for(i = -2; i <= 2; ++i) {
         for(j = -2; j <= 2; ++j) {
            for(k = -2; k <= 2; ++k) {
               int l = Math.abs(i);
               int m = Math.abs(j);
               int n = Math.abs(k);
               if ((l > 1 || m > 1 || n > 1) && (i == 0 && (m == 2 || n == 2) || j == 0 && (l == 2 || n == 2) || k == 0 && (l == 2 || m == 2))) {
                  BlockPos blockPos2 = pos.add(i, j, k);
                  BlockState blockState = world.getBlockState(blockPos2);
                  Block[] var11 = ACTIVATING_BLOCKS;
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     Block block = var11[var13];
                     if (blockState.isOf(block)) {
                        activatingBlocks.add(blockPos2);
                     }
                  }
               }
            }
         }
      }

      return activatingBlocks.size() >= 16;
   }

   private static void givePlayersEffects(World world, BlockPos pos, List activatingBlocks) {
      int i = activatingBlocks.size();
      int j = i / 7 * 16;
      int k = pos.getX();
      int l = pos.getY();
      int m = pos.getZ();
      Box box = (new Box((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))).expand((double)j).stretch(0.0, (double)world.getHeight(), 0.0);
      List list = world.getNonSpectatingEntities(PlayerEntity.class, box);
      if (!list.isEmpty()) {
         Iterator var10 = list.iterator();

         while(var10.hasNext()) {
            PlayerEntity playerEntity = (PlayerEntity)var10.next();
            if (pos.isWithinDistance(playerEntity.getBlockPos(), (double)j) && playerEntity.isTouchingWaterOrRain()) {
               playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
            }
         }

      }
   }

   private static void tryAttack(ServerWorld world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, boolean canAttack) {
      LazyEntityReference lazyEntityReference = getValidTarget(blockEntity.targetEntity, world, pos, canAttack);
      LivingEntity livingEntity = (LivingEntity)LazyEntityReference.resolve(lazyEntityReference, world, LivingEntity.class);
      if (livingEntity != null) {
         world.playSound((Entity)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
         livingEntity.damage(world, world.getDamageSources().magic(), 4.0F);
      }

      if (!Objects.equals(lazyEntityReference, blockEntity.targetEntity)) {
         blockEntity.targetEntity = lazyEntityReference;
         world.updateListeners(pos, state, state, 2);
      }

   }

   @Nullable
   private static LazyEntityReference getValidTarget(@Nullable LazyEntityReference currentTarget, ServerWorld world, BlockPos pos, boolean canAttack) {
      if (!canAttack) {
         return null;
      } else if (currentTarget == null) {
         return findAttackTarget(world, pos);
      } else {
         LivingEntity livingEntity = (LivingEntity)LazyEntityReference.resolve(currentTarget, world, LivingEntity.class);
         return livingEntity != null && livingEntity.isAlive() && pos.isWithinDistance(livingEntity.getBlockPos(), 8.0) ? currentTarget : null;
      }
   }

   @Nullable
   private static LazyEntityReference findAttackTarget(ServerWorld world, BlockPos pos) {
      List list = world.getEntitiesByClass(LivingEntity.class, getAttackZone(pos), (entity) -> {
         return entity instanceof Monster && entity.isTouchingWaterOrRain();
      });
      return list.isEmpty() ? null : new LazyEntityReference((LivingEntity)Util.getRandom(list, world.random));
   }

   private static Box getAttackZone(BlockPos pos) {
      return (new Box(pos)).expand(8.0);
   }

   private static void spawnNautilusParticles(World world, BlockPos pos, List activatingBlocks, @Nullable Entity entity, int ticks) {
      Random random = world.random;
      double d = (double)(MathHelper.sin((float)(ticks + 35) * 0.1F) / 2.0F + 0.5F);
      d = (d * d + d) * 0.30000001192092896;
      Vec3d vec3d = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 1.5 + d, (double)pos.getZ() + 0.5);
      Iterator var9 = activatingBlocks.iterator();

      float f;
      while(var9.hasNext()) {
         BlockPos blockPos = (BlockPos)var9.next();
         if (random.nextInt(50) == 0) {
            BlockPos blockPos2 = blockPos.subtract(pos);
            f = -0.5F + random.nextFloat() + (float)blockPos2.getX();
            float g = -2.0F + random.nextFloat() + (float)blockPos2.getY();
            float h = -0.5F + random.nextFloat() + (float)blockPos2.getZ();
            world.addParticleClient(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, (double)f, (double)g, (double)h);
         }
      }

      if (entity != null) {
         Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
         float i = (-0.5F + random.nextFloat()) * (3.0F + entity.getWidth());
         float j = -1.0F + random.nextFloat() * entity.getHeight();
         f = (-0.5F + random.nextFloat()) * (3.0F + entity.getWidth());
         Vec3d vec3d3 = new Vec3d((double)i, (double)j, (double)f);
         world.addParticleClient(ParticleTypes.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public boolean isEyeOpen() {
      return this.eyeOpen;
   }

   private void setEyeOpen(boolean eyeOpen) {
      this.eyeOpen = eyeOpen;
   }

   public float getRotation(float tickProgress) {
      return (this.ticksActive + tickProgress) * -0.0375F;
   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }

   static {
      ACTIVATING_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   }
}
