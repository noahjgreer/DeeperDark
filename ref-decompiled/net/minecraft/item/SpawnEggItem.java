package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SpawnEggItem extends Item {
   private static final Map SPAWN_EGGS = Maps.newIdentityHashMap();
   private final EntityType type;

   public SpawnEggItem(EntityType type, Item.Settings settings) {
      super(settings);
      this.type = type;
      SPAWN_EGGS.put(type, this);
   }

   public ActionResult useOnBlock(ItemUsageContext context) {
      World world = context.getWorld();
      if (world.isClient) {
         return ActionResult.SUCCESS;
      } else {
         ItemStack itemStack = context.getStack();
         BlockPos blockPos = context.getBlockPos();
         Direction direction = context.getSide();
         BlockState blockState = world.getBlockState(blockPos);
         BlockEntity var8 = world.getBlockEntity(blockPos);
         EntityType entityType;
         if (var8 instanceof Spawner) {
            Spawner spawner = (Spawner)var8;
            entityType = this.getEntityType(world.getRegistryManager(), itemStack);
            spawner.setEntityType(entityType, world.getRandom());
            world.updateListeners(blockPos, blockState, blockState, 3);
            world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
         } else {
            BlockPos blockPos2;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
               blockPos2 = blockPos;
            } else {
               blockPos2 = blockPos.offset(direction);
            }

            entityType = this.getEntityType(world.getRegistryManager(), itemStack);
            if (entityType.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
               itemStack.decrement(1);
               world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }

            return ActionResult.SUCCESS;
         }
      }
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
      if (blockHitResult.getType() != HitResult.Type.BLOCK) {
         return ActionResult.PASS;
      } else if (world instanceof ServerWorld) {
         ServerWorld serverWorld = (ServerWorld)world;
         BlockPos blockPos = blockHitResult.getBlockPos();
         if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return ActionResult.PASS;
         } else if (world.canEntityModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            EntityType entityType = this.getEntityType(serverWorld.getRegistryManager(), itemStack);
            Entity entity = entityType.spawnFromItemStack(serverWorld, itemStack, user, blockPos, SpawnReason.SPAWN_ITEM_USE, false, false);
            if (entity == null) {
               return ActionResult.PASS;
            } else {
               itemStack.decrementUnlessCreative(1, user);
               user.incrementStat(Stats.USED.getOrCreateStat(this));
               world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());
               return ActionResult.SUCCESS;
            }
         } else {
            return ActionResult.FAIL;
         }
      } else {
         return ActionResult.SUCCESS;
      }
   }

   public boolean isOfSameEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack, EntityType type) {
      return Objects.equals(this.getEntityType(registries, stack), type);
   }

   @Nullable
   public static SpawnEggItem forEntity(@Nullable EntityType type) {
      return (SpawnEggItem)SPAWN_EGGS.get(type);
   }

   public static Iterable getAll() {
      return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
   }

   public EntityType getEntityType(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
      NbtComponent nbtComponent = (NbtComponent)stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
      if (!nbtComponent.isEmpty()) {
         EntityType entityType = (EntityType)nbtComponent.getRegistryValueOfId(registries, RegistryKeys.ENTITY_TYPE);
         if (entityType != null) {
            return entityType;
         }
      }

      return this.type;
   }

   public FeatureSet getRequiredFeatures() {
      return this.type.getRequiredFeatures();
   }

   public Optional spawnBaby(PlayerEntity user, MobEntity entity, EntityType entityType, ServerWorld world, Vec3d pos, ItemStack stack) {
      if (!this.isOfSameEntityType(world.getRegistryManager(), stack, entityType)) {
         return Optional.empty();
      } else {
         Object mobEntity;
         if (entity instanceof PassiveEntity) {
            mobEntity = ((PassiveEntity)entity).createChild(world, (PassiveEntity)entity);
         } else {
            mobEntity = (MobEntity)entityType.create(world, SpawnReason.SPAWN_ITEM_USE);
         }

         if (mobEntity == null) {
            return Optional.empty();
         } else {
            ((MobEntity)mobEntity).setBaby(true);
            if (!((MobEntity)mobEntity).isBaby()) {
               return Optional.empty();
            } else {
               ((MobEntity)mobEntity).refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
               ((MobEntity)mobEntity).copyComponentsFrom(stack);
               world.spawnEntityAndPassengers((Entity)mobEntity);
               stack.decrementUnlessCreative(1, user);
               return Optional.of(mobEntity);
            }
         }
      }
   }

   public boolean shouldShowOperatorBlockWarnings(ItemStack stack, @Nullable PlayerEntity player) {
      if (player != null && player.getPermissionLevel() >= 2) {
         NbtComponent nbtComponent = (NbtComponent)stack.get(DataComponentTypes.ENTITY_DATA);
         if (nbtComponent != null) {
            EntityType entityType = (EntityType)nbtComponent.getRegistryValueOfId(player.getWorld().getRegistryManager(), RegistryKeys.ENTITY_TYPE);
            return entityType != null && entityType.canPotentiallyExecuteCommands();
         }
      }

      return false;
   }
}
