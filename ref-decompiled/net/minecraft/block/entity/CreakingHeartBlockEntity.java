package net.minecraft.block.entity;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class CreakingHeartBlockEntity extends BlockEntity {
   private static final int field_54776 = 32;
   public static final int field_54775 = 32;
   private static final int field_54777 = 34;
   private static final int field_54778 = 16;
   private static final int field_54779 = 8;
   private static final int field_54780 = 5;
   private static final int field_54781 = 20;
   private static final int field_55498 = 5;
   private static final int field_54782 = 100;
   private static final int field_54783 = 10;
   private static final int field_54784 = 10;
   private static final int field_54785 = 50;
   private static final int field_55085 = 2;
   private static final int field_55086 = 64;
   private static final int field_55499 = 30;
   private static final Optional DEFAULT_CREAKING_PUPPET = Optional.empty();
   @Nullable
   private Either creakingPuppet;
   private long ticks;
   private int creakingUpdateTimer;
   private int trailParticlesSpawnTimer;
   @Nullable
   private Vec3d lastCreakingPuppetPos;
   private int comparatorOutput;

   public CreakingHeartBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.CREAKING_HEART, pos, state);
   }

   public static void tick(World world, BlockPos pos, BlockState state, CreakingHeartBlockEntity blockEntity) {
      ++blockEntity.ticks;
      if (world instanceof ServerWorld serverWorld) {
         int i = blockEntity.calcComparatorOutput();
         if (blockEntity.comparatorOutput != i) {
            blockEntity.comparatorOutput = i;
            world.updateComparators(pos, Blocks.CREAKING_HEART);
         }

         if (blockEntity.trailParticlesSpawnTimer > 0) {
            if (blockEntity.trailParticlesSpawnTimer > 50) {
               blockEntity.spawnTrailParticles(serverWorld, 1, true);
               blockEntity.spawnTrailParticles(serverWorld, 1, false);
            }

            if (blockEntity.trailParticlesSpawnTimer % 10 == 0 && blockEntity.lastCreakingPuppetPos != null) {
               blockEntity.getCreakingPuppet().ifPresent((creaking) -> {
                  blockEntity.lastCreakingPuppetPos = creaking.getBoundingBox().getCenter();
               });
               Vec3d vec3d = Vec3d.ofCenter(pos);
               float f = 0.2F + 0.8F * (float)(100 - blockEntity.trailParticlesSpawnTimer) / 100.0F;
               Vec3d vec3d2 = vec3d.subtract(blockEntity.lastCreakingPuppetPos).multiply((double)f).add(blockEntity.lastCreakingPuppetPos);
               BlockPos blockPos = BlockPos.ofFloored(vec3d2);
               float g = (float)blockEntity.trailParticlesSpawnTimer / 2.0F / 100.0F + 0.5F;
               serverWorld.playSound((Entity)null, blockPos, SoundEvents.BLOCK_CREAKING_HEART_HURT, SoundCategory.BLOCKS, g, 1.0F);
            }

            --blockEntity.trailParticlesSpawnTimer;
         }

         if (blockEntity.creakingUpdateTimer-- < 0) {
            blockEntity.creakingUpdateTimer = blockEntity.world == null ? 20 : blockEntity.world.random.nextInt(5) + 20;
            BlockState blockState = getBlockState(world, state, pos, blockEntity);
            if (blockState != state) {
               world.setBlockState(pos, blockState, 3);
               if (blockState.get(CreakingHeartBlock.ACTIVE) == CreakingHeartState.UPROOTED) {
                  return;
               }
            }

            CreakingEntity creakingEntity;
            if (blockEntity.creakingPuppet == null) {
               if (blockState.get(CreakingHeartBlock.ACTIVE) == CreakingHeartState.AWAKE) {
                  if (world.getDifficulty() != Difficulty.PEACEFUL) {
                     if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 32.0, false);
                        if (playerEntity != null) {
                           creakingEntity = spawnCreakingPuppet(serverWorld, blockEntity);
                           if (creakingEntity != null) {
                              blockEntity.setCreakingPuppet(creakingEntity);
                              creakingEntity.playSound(SoundEvents.ENTITY_CREAKING_SPAWN);
                              world.playSound((Entity)null, blockEntity.getPos(), SoundEvents.BLOCK_CREAKING_HEART_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                           }
                        }

                     }
                  }
               }
            } else {
               Optional optional = blockEntity.getCreakingPuppet();
               if (optional.isPresent()) {
                  creakingEntity = (CreakingEntity)optional.get();
                  if (!CreakingHeartBlock.isNightAndNatural(world) && !creakingEntity.isPersistent() || blockEntity.getDistanceToPuppet() > 34.0 || creakingEntity.isStuckWithPlayer()) {
                     blockEntity.killPuppet((DamageSource)null);
                  }
               }

            }
         }
      }
   }

   private static BlockState getBlockState(World world, BlockState state, BlockPos pos, CreakingHeartBlockEntity creakingHeart) {
      if (!CreakingHeartBlock.shouldBeEnabled(state, world, pos) && creakingHeart.creakingPuppet == null) {
         return (BlockState)state.with(CreakingHeartBlock.ACTIVE, CreakingHeartState.UPROOTED);
      } else {
         boolean bl = CreakingHeartBlock.isNightAndNatural(world);
         return (BlockState)state.with(CreakingHeartBlock.ACTIVE, bl ? CreakingHeartState.AWAKE : CreakingHeartState.DORMANT);
      }
   }

   private double getDistanceToPuppet() {
      return (Double)this.getCreakingPuppet().map((creaking) -> {
         return Math.sqrt(creaking.squaredDistanceTo(Vec3d.ofBottomCenter(this.getPos())));
      }).orElse(0.0);
   }

   private void clearCreakingPuppet() {
      this.creakingPuppet = null;
      this.markDirty();
   }

   public void setCreakingPuppet(CreakingEntity creakingPuppet) {
      this.creakingPuppet = Either.left(creakingPuppet);
      this.markDirty();
   }

   public void setCreakingPuppetFromUuid(UUID creakingPuppetUuid) {
      this.creakingPuppet = Either.right(creakingPuppetUuid);
      this.ticks = 0L;
      this.markDirty();
   }

   private Optional getCreakingPuppet() {
      if (this.creakingPuppet == null) {
         return DEFAULT_CREAKING_PUPPET;
      } else {
         if (this.creakingPuppet.left().isPresent()) {
            CreakingEntity creakingEntity = (CreakingEntity)this.creakingPuppet.left().get();
            if (!creakingEntity.isRemoved()) {
               return Optional.of(creakingEntity);
            }

            this.setCreakingPuppetFromUuid(creakingEntity.getUuid());
         }

         World var2 = this.world;
         if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            if (this.creakingPuppet.right().isPresent()) {
               UUID uUID = (UUID)this.creakingPuppet.right().get();
               Entity entity = serverWorld.getEntity(uUID);
               if (entity instanceof CreakingEntity) {
                  CreakingEntity creakingEntity2 = (CreakingEntity)entity;
                  this.setCreakingPuppet(creakingEntity2);
                  return Optional.of(creakingEntity2);
               }

               if (this.ticks >= 30L) {
                  this.clearCreakingPuppet();
               }

               return DEFAULT_CREAKING_PUPPET;
            }
         }

         return DEFAULT_CREAKING_PUPPET;
      }
   }

   @Nullable
   private static CreakingEntity spawnCreakingPuppet(ServerWorld world, CreakingHeartBlockEntity blockEntity) {
      BlockPos blockPos = blockEntity.getPos();
      Optional optional = LargeEntitySpawnHelper.trySpawnAt(EntityType.CREAKING, SpawnReason.SPAWNER, world, blockPos, 5, 16, 8, LargeEntitySpawnHelper.Requirements.CREAKING, true);
      if (optional.isEmpty()) {
         return null;
      } else {
         CreakingEntity creakingEntity = (CreakingEntity)optional.get();
         world.emitGameEvent(creakingEntity, GameEvent.ENTITY_PLACE, creakingEntity.getPos());
         world.sendEntityStatus(creakingEntity, (byte)60);
         creakingEntity.initHomePos(blockPos);
         return creakingEntity;
      }
   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public void onPuppetDamage() {
      Object var2 = this.getCreakingPuppet().orElse((Object)null);
      if (var2 instanceof CreakingEntity creakingEntity) {
         World var3 = this.world;
         if (var3 instanceof ServerWorld serverWorld) {
            if (this.trailParticlesSpawnTimer <= 0) {
               this.spawnTrailParticles(serverWorld, 20, false);
               if (this.getCachedState().get(CreakingHeartBlock.ACTIVE) == CreakingHeartState.AWAKE) {
                  int i = this.world.getRandom().nextBetween(2, 3);

                  for(int j = 0; j < i; ++j) {
                     this.findResinGenerationPos().ifPresent((pos) -> {
                        this.world.playSound((Entity)null, pos, SoundEvents.BLOCK_RESIN_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(this.getCachedState()));
                     });
                  }
               }

               this.trailParticlesSpawnTimer = 100;
               this.lastCreakingPuppetPos = creakingEntity.getBoundingBox().getCenter();
            }
         }
      }
   }

   private Optional findResinGenerationPos() {
      Mutable mutable = new MutableObject((Object)null);
      BlockPos.iterateRecursively(this.pos, 2, 64, (pos, consumer) -> {
         Iterator var3 = Util.copyShuffled((Object[])Direction.values(), this.world.random).iterator();

         while(var3.hasNext()) {
            Direction direction = (Direction)var3.next();
            BlockPos blockPos = pos.offset(direction);
            if (this.world.getBlockState(blockPos).isIn(BlockTags.PALE_OAK_LOGS)) {
               consumer.accept(blockPos);
            }
         }

      }, (pos) -> {
         if (!this.world.getBlockState(pos).isIn(BlockTags.PALE_OAK_LOGS)) {
            return BlockPos.IterationState.ACCEPT;
         } else {
            Iterator var3 = Util.copyShuffled((Object[])Direction.values(), this.world.random).iterator();

            BlockPos blockPos;
            BlockState blockState;
            Direction direction2;
            do {
               if (!var3.hasNext()) {
                  return BlockPos.IterationState.ACCEPT;
               }

               Direction direction = (Direction)var3.next();
               blockPos = pos.offset(direction);
               blockState = this.world.getBlockState(blockPos);
               direction2 = direction.getOpposite();
               if (blockState.isAir()) {
                  blockState = Blocks.RESIN_CLUMP.getDefaultState();
               } else if (blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill()) {
                  blockState = (BlockState)Blocks.RESIN_CLUMP.getDefaultState().with(MultifaceBlock.WATERLOGGED, true);
               }
            } while(!blockState.isOf(Blocks.RESIN_CLUMP) || MultifaceBlock.hasDirection(blockState, direction2));

            this.world.setBlockState(blockPos, (BlockState)blockState.with(MultifaceBlock.getProperty(direction2), true), 3);
            mutable.setValue(blockPos);
            return BlockPos.IterationState.STOP;
         }
      });
      return Optional.ofNullable((BlockPos)mutable.getValue());
   }

   private void spawnTrailParticles(ServerWorld world, int count, boolean towardsPuppet) {
      Object var5 = this.getCreakingPuppet().orElse((Object)null);
      if (var5 instanceof CreakingEntity creakingEntity) {
         int i = towardsPuppet ? 16545810 : 6250335;
         Random random = world.random;

         for(double d = 0.0; d < (double)count; ++d) {
            Box box = creakingEntity.getBoundingBox();
            Vec3d vec3d = box.getMinPos().add(random.nextDouble() * box.getLengthX(), random.nextDouble() * box.getLengthY(), random.nextDouble() * box.getLengthZ());
            Vec3d vec3d2 = Vec3d.of(this.getPos()).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
            if (towardsPuppet) {
               Vec3d vec3d3 = vec3d;
               vec3d = vec3d2;
               vec3d2 = vec3d3;
            }

            TrailParticleEffect trailParticleEffect = new TrailParticleEffect(vec3d2, i, random.nextInt(40) + 10);
            world.spawnParticles(trailParticleEffect, true, true, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
         }

      }
   }

   public void onBlockReplaced(BlockPos pos, BlockState oldState) {
      this.killPuppet((DamageSource)null);
   }

   public void killPuppet(@Nullable DamageSource damageSource) {
      Object var3 = this.getCreakingPuppet().orElse((Object)null);
      if (var3 instanceof CreakingEntity creakingEntity) {
         if (damageSource == null) {
            creakingEntity.finishCrumbling();
         } else {
            creakingEntity.killFromHeart(damageSource);
            creakingEntity.setCrumbling();
            creakingEntity.setHealth(0.0F);
         }

         this.clearCreakingPuppet();
      }

   }

   public boolean isPuppet(CreakingEntity creaking) {
      return (Boolean)this.getCreakingPuppet().map((puppet) -> {
         return puppet == creaking;
      }).orElse(false);
   }

   public int getComparatorOutput() {
      return this.comparatorOutput;
   }

   public int calcComparatorOutput() {
      if (this.creakingPuppet != null && !this.getCreakingPuppet().isEmpty()) {
         double d = this.getDistanceToPuppet();
         double e = Math.clamp(d, 0.0, 32.0) / 32.0;
         return 15 - (int)Math.floor(e * 15.0);
      } else {
         return 0;
      }
   }

   protected void readData(ReadView view) {
      super.readData(view);
      view.read("creaking", Uuids.INT_STREAM_CODEC).ifPresentOrElse(this::setCreakingPuppetFromUuid, this::clearCreakingPuppet);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      if (this.creakingPuppet != null) {
         view.put("creaking", Uuids.INT_STREAM_CODEC, (UUID)this.creakingPuppet.map(Entity::getUuid, (uuid) -> {
            return uuid;
         }));
      }

   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }
}
