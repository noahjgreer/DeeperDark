package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BeehiveBlockEntity extends BlockEntity {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FLOWER_POS_KEY = "flower_pos";
   private static final String BEES_KEY = "bees";
   static final List IRRELEVANT_BEE_NBT_KEYS = Arrays.asList("Air", "drop_chances", "equipment", "Brain", "CanPickUpLoot", "DeathTime", "fall_distance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "sleeping_pos", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
   public static final int MAX_BEE_COUNT = 3;
   private static final int ANGERED_CANNOT_ENTER_HIVE_TICKS = 400;
   private static final int MIN_OCCUPATION_TICKS_WITH_NECTAR = 2400;
   public static final int MIN_OCCUPATION_TICKS_WITHOUT_NECTAR = 600;
   private final List bees = Lists.newArrayList();
   @Nullable
   private BlockPos flowerPos;

   public BeehiveBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.BEEHIVE, pos, state);
   }

   public void markDirty() {
      if (this.isNearFire()) {
         this.angerBees((PlayerEntity)null, this.world.getBlockState(this.getPos()), BeehiveBlockEntity.BeeState.EMERGENCY);
      }

      super.markDirty();
   }

   public boolean isNearFire() {
      if (this.world == null) {
         return false;
      } else {
         Iterator var1 = BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1)).iterator();

         BlockPos blockPos;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            blockPos = (BlockPos)var1.next();
         } while(!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock));

         return true;
      }
   }

   public boolean hasNoBees() {
      return this.bees.isEmpty();
   }

   public boolean isFullOfBees() {
      return this.bees.size() == 3;
   }

   public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState beeState) {
      List list = this.tryReleaseBee(state, beeState);
      if (player != null) {
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            if (entity instanceof BeeEntity) {
               BeeEntity beeEntity = (BeeEntity)entity;
               if (player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0) {
                  if (!this.isSmoked()) {
                     beeEntity.setTarget(player);
                  } else {
                     beeEntity.setCannotEnterHiveTicks(400);
                  }
               }
            }
         }
      }

   }

   private List tryReleaseBee(BlockState state, BeeState beeState) {
      List list = Lists.newArrayList();
      this.bees.removeIf((bee) -> {
         return releaseBee(this.world, this.pos, state, bee.createData(), list, beeState, this.flowerPos);
      });
      if (!list.isEmpty()) {
         super.markDirty();
      }

      return list;
   }

   @Debug
   public int getBeeCount() {
      return this.bees.size();
   }

   public static int getHoneyLevel(BlockState state) {
      return (Integer)state.get(BeehiveBlock.HONEY_LEVEL);
   }

   @Debug
   public boolean isSmoked() {
      return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
   }

   public void tryEnterHive(BeeEntity entity) {
      if (this.bees.size() < 3) {
         entity.stopRiding();
         entity.removeAllPassengers();
         entity.detachLeash();
         this.addBee(BeehiveBlockEntity.BeeData.of(entity));
         if (this.world != null) {
            if (entity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
               this.flowerPos = entity.getFlowerPos();
            }

            BlockPos blockPos = this.getPos();
            this.world.playSound((Entity)null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (SoundEvent)SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(entity, this.getCachedState()));
         }

         entity.discard();
         super.markDirty();
      }
   }

   public void addBee(BeeData bee) {
      this.bees.add(new Bee(bee));
   }

   private static boolean releaseBee(World world, BlockPos pos, BlockState state, BeeData bee, @Nullable List entities, BeeState beeState, @Nullable BlockPos flowerPos) {
      if (BeeEntity.isNightOrRaining(world) && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
         return false;
      } else {
         Direction direction = (Direction)state.get(BeehiveBlock.FACING);
         BlockPos blockPos = pos.offset(direction);
         boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
         if (bl && beeState != BeehiveBlockEntity.BeeState.EMERGENCY) {
            return false;
         } else {
            Entity entity = bee.loadEntity(world, pos);
            if (entity != null) {
               if (entity instanceof BeeEntity) {
                  BeeEntity beeEntity = (BeeEntity)entity;
                  if (flowerPos != null && !beeEntity.hasFlower() && world.random.nextFloat() < 0.9F) {
                     beeEntity.setFlowerPos(flowerPos);
                  }

                  if (beeState == BeehiveBlockEntity.BeeState.HONEY_DELIVERED) {
                     beeEntity.onHoneyDelivered();
                     if (state.isIn(BlockTags.BEEHIVES, (statex) -> {
                        return statex.contains(BeehiveBlock.HONEY_LEVEL);
                     })) {
                        int i = getHoneyLevel(state);
                        if (i < 5) {
                           int j = world.random.nextInt(100) == 0 ? 2 : 1;
                           if (i + j > 5) {
                              --j;
                           }

                           world.setBlockState(pos, (BlockState)state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                        }
                     }
                  }

                  if (entities != null) {
                     entities.add(beeEntity);
                  }

                  float f = entity.getWidth();
                  double d = bl ? 0.0 : 0.55 + (double)(f / 2.0F);
                  double e = (double)pos.getX() + 0.5 + d * (double)direction.getOffsetX();
                  double g = (double)pos.getY() + 0.5 - (double)(entity.getHeight() / 2.0F);
                  double h = (double)pos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
                  entity.refreshPositionAndAngles(e, g, h, entity.getYaw(), entity.getPitch());
               }

               world.playSound((Entity)null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
               world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, world.getBlockState(pos)));
               return world.spawnEntity(entity);
            } else {
               return false;
            }
         }
      }
   }

   private boolean hasFlowerPos() {
      return this.flowerPos != null;
   }

   private static void tickBees(World world, BlockPos pos, BlockState state, List bees, @Nullable BlockPos flowerPos) {
      boolean bl = false;
      Iterator iterator = bees.iterator();

      while(iterator.hasNext()) {
         Bee bee = (Bee)iterator.next();
         if (bee.canExitHive()) {
            BeeState beeState = bee.hasNectar() ? BeehiveBlockEntity.BeeState.HONEY_DELIVERED : BeehiveBlockEntity.BeeState.BEE_RELEASED;
            if (releaseBee(world, pos, state, bee.createData(), (List)null, beeState, flowerPos)) {
               bl = true;
               iterator.remove();
            }
         }
      }

      if (bl) {
         markDirty(world, pos, state);
      }

   }

   public static void serverTick(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
      tickBees(world, pos, state, blockEntity.bees, blockEntity.flowerPos);
      if (!blockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
         double d = (double)pos.getX() + 0.5;
         double e = (double)pos.getY();
         double f = (double)pos.getZ() + 0.5;
         world.playSound((Entity)null, d, e, f, (SoundEvent)SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      DebugInfoSender.sendBeehiveDebugData(world, pos, state, blockEntity);
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.bees.clear();
      ((List)view.read("bees", BeehiveBlockEntity.BeeData.LIST_CODEC).orElse(List.of())).forEach(this::addBee);
      this.flowerPos = (BlockPos)view.read("flower_pos", BlockPos.CODEC).orElse((Object)null);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      view.put("bees", BeehiveBlockEntity.BeeData.LIST_CODEC, this.createBeesData());
      view.putNullable("flower_pos", BlockPos.CODEC, this.flowerPos);
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      this.bees.clear();
      List list = ((BeesComponent)components.getOrDefault(DataComponentTypes.BEES, BeesComponent.DEFAULT)).bees();
      list.forEach(this::addBee);
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.BEES, new BeesComponent(this.createBeesData()));
   }

   public void removeFromCopiedStackData(WriteView view) {
      super.removeFromCopiedStackData(view);
      view.remove("bees");
   }

   private List createBeesData() {
      return this.bees.stream().map(Bee::createData).toList();
   }

   public static enum BeeState {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;

      // $FF: synthetic method
      private static BeeState[] method_36714() {
         return new BeeState[]{HONEY_DELIVERED, BEE_RELEASED, EMERGENCY};
      }
   }

   public static record BeeData(NbtComponent entityData, int ticksInHive, int minTicksInHive) {
      final NbtComponent entityData;
      final int minTicksInHive;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(NbtComponent.CODEC.optionalFieldOf("entity_data", NbtComponent.DEFAULT).forGetter(BeeData::entityData), Codec.INT.fieldOf("ticks_in_hive").forGetter(BeeData::ticksInHive), Codec.INT.fieldOf("min_ticks_in_hive").forGetter(BeeData::minTicksInHive)).apply(instance, BeeData::new);
      });
      public static final Codec LIST_CODEC;
      public static final PacketCodec PACKET_CODEC;

      public BeeData(NbtComponent nbtComponent, int i, int j) {
         this.entityData = nbtComponent;
         this.ticksInHive = i;
         this.minTicksInHive = j;
      }

      public static BeeData of(Entity entity) {
         ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), BeehiveBlockEntity.LOGGER);

         BeeData var5;
         try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
            entity.saveData(nbtWriteView);
            List var10000 = BeehiveBlockEntity.IRRELEVANT_BEE_NBT_KEYS;
            Objects.requireNonNull(nbtWriteView);
            var10000.forEach(nbtWriteView::remove);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            boolean bl = nbtCompound.getBoolean("HasNectar", false);
            var5 = new BeeData(NbtComponent.of(nbtCompound), 0, bl ? 2400 : 600);
         } catch (Throwable var7) {
            try {
               logging.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         logging.close();
         return var5;
      }

      public static BeeData create(int ticksInHive) {
         NbtCompound nbtCompound = new NbtCompound();
         nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(EntityType.BEE).toString());
         return new BeeData(NbtComponent.of(nbtCompound), ticksInHive, 600);
      }

      @Nullable
      public Entity loadEntity(World world, BlockPos pos) {
         NbtCompound nbtCompound = this.entityData.copyNbt();
         List var10000 = BeehiveBlockEntity.IRRELEVANT_BEE_NBT_KEYS;
         Objects.requireNonNull(nbtCompound);
         var10000.forEach(nbtCompound::remove);
         Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, SpawnReason.LOAD, (entityx) -> {
            return entityx;
         });
         if (entity != null && entity.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
            entity.setNoGravity(true);
            if (entity instanceof BeeEntity) {
               BeeEntity beeEntity = (BeeEntity)entity;
               beeEntity.setHivePos(pos);
               tickEntity(this.ticksInHive, beeEntity);
            }

            return entity;
         } else {
            return null;
         }
      }

      private static void tickEntity(int ticksInHive, BeeEntity beeEntity) {
         int i = beeEntity.getBreedingAge();
         if (i < 0) {
            beeEntity.setBreedingAge(Math.min(0, i + ticksInHive));
         } else if (i > 0) {
            beeEntity.setBreedingAge(Math.max(0, i - ticksInHive));
         }

         beeEntity.setLoveTicks(Math.max(0, beeEntity.getLoveTicks() - ticksInHive));
      }

      public NbtComponent entityData() {
         return this.entityData;
      }

      public int ticksInHive() {
         return this.ticksInHive;
      }

      public int minTicksInHive() {
         return this.minTicksInHive;
      }

      static {
         LIST_CODEC = CODEC.listOf();
         PACKET_CODEC = PacketCodec.tuple(NbtComponent.PACKET_CODEC, BeeData::entityData, PacketCodecs.VAR_INT, BeeData::ticksInHive, PacketCodecs.VAR_INT, BeeData::minTicksInHive, BeeData::new);
      }
   }

   private static class Bee {
      private final BeeData data;
      private int ticksInHive;

      Bee(BeeData data) {
         this.data = data;
         this.ticksInHive = data.ticksInHive();
      }

      public boolean canExitHive() {
         return this.ticksInHive++ > this.data.minTicksInHive;
      }

      public BeeData createData() {
         return new BeeData(this.data.entityData, this.ticksInHive, this.data.minTicksInHive);
      }

      public boolean hasNectar() {
         return this.data.entityData.getNbt().getBoolean("HasNectar", false);
      }
   }
}
