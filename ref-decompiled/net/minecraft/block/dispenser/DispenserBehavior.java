package net.minecraft.block.dispenser;

import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public interface DispenserBehavior {
   Logger LOGGER = LogUtils.getLogger();
   DispenserBehavior NOOP = (pointer, stack) -> {
      return stack;
   };

   ItemStack dispense(BlockPointer pointer, ItemStack stack);

   static void registerDefaults() {
      DispenserBlock.registerProjectileBehavior(Items.ARROW);
      DispenserBlock.registerProjectileBehavior(Items.TIPPED_ARROW);
      DispenserBlock.registerProjectileBehavior(Items.SPECTRAL_ARROW);
      DispenserBlock.registerProjectileBehavior(Items.EGG);
      DispenserBlock.registerProjectileBehavior(Items.BLUE_EGG);
      DispenserBlock.registerProjectileBehavior(Items.BROWN_EGG);
      DispenserBlock.registerProjectileBehavior(Items.SNOWBALL);
      DispenserBlock.registerProjectileBehavior(Items.EXPERIENCE_BOTTLE);
      DispenserBlock.registerProjectileBehavior(Items.SPLASH_POTION);
      DispenserBlock.registerProjectileBehavior(Items.LINGERING_POTION);
      DispenserBlock.registerProjectileBehavior(Items.FIREWORK_ROCKET);
      DispenserBlock.registerProjectileBehavior(Items.FIRE_CHARGE);
      DispenserBlock.registerProjectileBehavior(Items.WIND_CHARGE);
      ItemDispenserBehavior itemDispenserBehavior = new ItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
            EntityType entityType = ((SpawnEggItem)stack.getItem()).getEntityType(pointer.world().getRegistryManager(), stack);

            try {
               entityType.spawnFromItemStack(pointer.world(), stack, (LivingEntity)null, pointer.pos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
            } catch (Exception var6) {
               LOGGER.error("Error while dispensing spawn egg from dispenser at {}", pointer.pos(), var6);
               return ItemStack.EMPTY;
            }

            stack.decrement(1);
            pointer.world().emitGameEvent((Entity)null, GameEvent.ENTITY_PLACE, pointer.pos());
            return stack;
         }
      };
      Iterator var1 = SpawnEggItem.getAll().iterator();

      while(var1.hasNext()) {
         SpawnEggItem spawnEggItem = (SpawnEggItem)var1.next();
         DispenserBlock.registerBehavior(spawnEggItem, itemDispenserBehavior);
      }

      DispenserBlock.registerBehavior(Items.ARMOR_STAND, new ItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);
            ServerWorld serverWorld = pointer.world();
            Consumer consumer = EntityType.copier((armorStand) -> {
               armorStand.setYaw(direction.getPositiveHorizontalDegrees());
            }, serverWorld, stack, (LivingEntity)null);
            ArmorStandEntity armorStandEntity = (ArmorStandEntity)EntityType.ARMOR_STAND.spawn(serverWorld, consumer, blockPos, SpawnReason.DISPENSER, false, false);
            if (armorStandEntity != null) {
               stack.decrement(1);
            }

            return stack;
         }
      });
      DispenserBlock.registerBehavior(Items.CHEST, new FallibleItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            List list = pointer.world().getEntitiesByClass(AbstractDonkeyEntity.class, new Box(blockPos), (donkey) -> {
               return donkey.isAlive() && !donkey.hasChest();
            });
            Iterator var5 = list.iterator();

            AbstractDonkeyEntity abstractDonkeyEntity;
            do {
               if (!var5.hasNext()) {
                  return super.dispenseSilently(pointer, stack);
               }

               abstractDonkeyEntity = (AbstractDonkeyEntity)var5.next();
            } while(!abstractDonkeyEntity.isTame() || !abstractDonkeyEntity.getStackReference(499).set(stack));

            stack.decrement(1);
            this.setSuccess(true);
            return stack;
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenserBehavior(EntityType.OAK_BOAT));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenserBehavior(EntityType.SPRUCE_BOAT));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenserBehavior(EntityType.BIRCH_BOAT));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenserBehavior(EntityType.JUNGLE_BOAT));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenserBehavior(EntityType.DARK_OAK_BOAT));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenserBehavior(EntityType.ACACIA_BOAT));
      DispenserBlock.registerBehavior(Items.CHERRY_BOAT, new BoatDispenserBehavior(EntityType.CHERRY_BOAT));
      DispenserBlock.registerBehavior(Items.MANGROVE_BOAT, new BoatDispenserBehavior(EntityType.MANGROVE_BOAT));
      DispenserBlock.registerBehavior(Items.PALE_OAK_BOAT, new BoatDispenserBehavior(EntityType.PALE_OAK_BOAT));
      DispenserBlock.registerBehavior(Items.BAMBOO_RAFT, new BoatDispenserBehavior(EntityType.BAMBOO_RAFT));
      DispenserBlock.registerBehavior(Items.OAK_CHEST_BOAT, new BoatDispenserBehavior(EntityType.OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.SPRUCE_CHEST_BOAT, new BoatDispenserBehavior(EntityType.SPRUCE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.BIRCH_CHEST_BOAT, new BoatDispenserBehavior(EntityType.BIRCH_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.JUNGLE_CHEST_BOAT, new BoatDispenserBehavior(EntityType.JUNGLE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.DARK_OAK_CHEST_BOAT, new BoatDispenserBehavior(EntityType.DARK_OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.ACACIA_CHEST_BOAT, new BoatDispenserBehavior(EntityType.ACACIA_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.CHERRY_CHEST_BOAT, new BoatDispenserBehavior(EntityType.CHERRY_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.MANGROVE_CHEST_BOAT, new BoatDispenserBehavior(EntityType.MANGROVE_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.PALE_OAK_CHEST_BOAT, new BoatDispenserBehavior(EntityType.PALE_OAK_CHEST_BOAT));
      DispenserBlock.registerBehavior(Items.BAMBOO_CHEST_RAFT, new BoatDispenserBehavior(EntityType.BAMBOO_CHEST_RAFT));
      DispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
         private final ItemDispenserBehavior fallback = new ItemDispenserBehavior();

         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            FluidModificationItem fluidModificationItem = (FluidModificationItem)stack.getItem();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            World world = pointer.world();
            if (fluidModificationItem.placeFluid((LivingEntity)null, world, blockPos, (BlockHitResult)null)) {
               fluidModificationItem.onEmptied((LivingEntity)null, world, stack, blockPos);
               return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.BUCKET));
            } else {
               return this.fallback.dispense(pointer, stack);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.TADPOLE_BUCKET, dispenserBehavior);
      DispenserBlock.registerBehavior(Items.BUCKET, new ItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            WorldAccess worldAccess = pointer.world();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            BlockState blockState = worldAccess.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block instanceof FluidDrainable fluidDrainable) {
               ItemStack itemStack = fluidDrainable.tryDrainFluid((LivingEntity)null, worldAccess, blockPos, blockState);
               if (itemStack.isEmpty()) {
                  return super.dispenseSilently(pointer, stack);
               } else {
                  worldAccess.emitGameEvent((Entity)null, (RegistryEntry)GameEvent.FLUID_PICKUP, (BlockPos)blockPos);
                  Item item = itemStack.getItem();
                  return this.decrementStackWithRemainder(pointer, stack, new ItemStack(item));
               }
            } else {
               return super.dispenseSilently(pointer, stack);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            ServerWorld serverWorld = pointer.world();
            this.setSuccess(true);
            Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);
            BlockState blockState = serverWorld.getBlockState(blockPos);
            if (AbstractFireBlock.canPlaceAt(serverWorld, blockPos, direction)) {
               serverWorld.setBlockState(blockPos, AbstractFireBlock.getState(serverWorld, blockPos));
               serverWorld.emitGameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
            } else if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
               if (blockState.getBlock() instanceof TntBlock) {
                  if (TntBlock.primeTnt(serverWorld, blockPos)) {
                     serverWorld.removeBlock(blockPos, false);
                  } else {
                     this.setSuccess(false);
                  }
               } else {
                  this.setSuccess(false);
               }
            } else {
               serverWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true));
               serverWorld.emitGameEvent((Entity)null, GameEvent.BLOCK_CHANGE, blockPos);
            }

            if (this.isSuccess()) {
               stack.damage(1, (ServerWorld)serverWorld, (ServerPlayerEntity)null, (Consumer)((item) -> {
               }));
            }

            return stack;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            this.setSuccess(true);
            World world = pointer.world();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            if (!BoneMealItem.useOnFertilizable(stack, world, blockPos) && !BoneMealItem.useOnGround(stack, world, blockPos, (Direction)null)) {
               this.setSuccess(false);
            } else if (!world.isClient) {
               world.syncWorldEvent(1505, blockPos, 15);
            }

            return stack;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            ServerWorld serverWorld = pointer.world();
            if (!serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
               this.setSuccess(false);
               return stack;
            } else {
               BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
               TntEntity tntEntity = new TntEntity(serverWorld, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, (LivingEntity)null);
               serverWorld.spawnEntity(tntEntity);
               serverWorld.playSound((Entity)null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
               serverWorld.emitGameEvent((Entity)null, GameEvent.ENTITY_PLACE, blockPos);
               stack.decrement(1);
               this.setSuccess(true);
               return stack;
            }
         }
      });
      DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            World world = pointer.world();
            Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);
            if (world.isAir(blockPos) && WitherSkullBlock.canDispense(world, blockPos, stack)) {
               world.setBlockState(blockPos, (BlockState)Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, RotationPropertyHelper.fromDirection(direction)), 3);
               world.emitGameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
               BlockEntity blockEntity = world.getBlockEntity(blockPos);
               if (blockEntity instanceof SkullBlockEntity) {
                  WitherSkullBlock.onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
               }

               stack.decrement(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(EquippableDispenserBehavior.dispense(pointer, stack));
            }

            return stack;
         }
      });
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            World world = pointer.world();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
               if (!world.isClient) {
                  world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), 3);
                  world.emitGameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockPos);
               }

               stack.decrement(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(EquippableDispenserBehavior.dispense(pointer, stack));
            }

            return stack;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new BlockPlacementDispenserBehavior());
      DyeColor[] var7 = DyeColor.values();
      int var3 = var7.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DyeColor dyeColor = var7[var4];
         DispenserBlock.registerBehavior(ShulkerBoxBlock.get(dyeColor).asItem(), new BlockPlacementDispenserBehavior());
      }

      DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new FallibleItemDispenserBehavior() {
         private ItemStack pickUpFluid(BlockPointer pointer, ItemStack inputStack, ItemStack outputStack) {
            pointer.world().emitGameEvent((Entity)null, GameEvent.FLUID_PICKUP, pointer.pos());
            return this.decrementStackWithRemainder(pointer, inputStack, outputStack);
         }

         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            this.setSuccess(false);
            ServerWorld serverWorld = pointer.world();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            BlockState blockState = serverWorld.getBlockState(blockPos);
            if (blockState.isIn(BlockTags.BEEHIVES, (state) -> {
               return state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock;
            }) && (Integer)blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
               ((BeehiveBlock)blockState.getBlock()).takeHoney(serverWorld, blockState, blockPos, (PlayerEntity)null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
               this.setSuccess(true);
               return this.pickUpFluid(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
            } else if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
               this.setSuccess(true);
               return this.pickUpFluid(pointer, stack, PotionContentsComponent.createStack(Items.POTION, Potions.WATER));
            } else {
               return super.dispenseSilently(pointer, stack);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.GLOWSTONE, new FallibleItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
            BlockPos blockPos = pointer.pos().offset(direction);
            World world = pointer.world();
            BlockState blockState = world.getBlockState(blockPos);
            this.setSuccess(true);
            if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
               if ((Integer)blockState.get(RespawnAnchorBlock.CHARGES) != 4) {
                  RespawnAnchorBlock.charge((Entity)null, world, blockPos, blockState);
                  stack.decrement(1);
               } else {
                  this.setSuccess(false);
               }

               return stack;
            } else {
               return super.dispenseSilently(pointer, stack);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
      DispenserBlock.registerBehavior(Items.BRUSH.asItem(), new FallibleItemDispenserBehavior() {
         protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            ServerWorld serverWorld = pointer.world();
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            List list = serverWorld.getEntitiesByClass(ArmadilloEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR);
            if (list.isEmpty()) {
               this.setSuccess(false);
               return stack;
            } else {
               Iterator var6 = list.iterator();

               ArmadilloEntity armadilloEntity;
               do {
                  if (!var6.hasNext()) {
                     this.setSuccess(false);
                     return stack;
                  }

                  armadilloEntity = (ArmadilloEntity)var6.next();
               } while(!armadilloEntity.brushScute());

               stack.damage(16, (ServerWorld)serverWorld, (ServerPlayerEntity)null, (Consumer)((item) -> {
               }));
               return stack;
            }
         }
      });
      DispenserBlock.registerBehavior(Items.HONEYCOMB, new FallibleItemDispenserBehavior() {
         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
            World world = pointer.world();
            BlockState blockState = world.getBlockState(blockPos);
            Optional optional = HoneycombItem.getWaxedState(blockState);
            if (optional.isPresent()) {
               world.setBlockState(blockPos, (BlockState)optional.get());
               world.syncWorldEvent(3003, blockPos, 0);
               stack.decrement(1);
               this.setSuccess(true);
               return stack;
            } else {
               return super.dispenseSilently(pointer, stack);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.POTION, new ItemDispenserBehavior() {
         private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

         public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            if (!potionContentsComponent.matches(Potions.WATER)) {
               return this.fallbackBehavior.dispense(pointer, stack);
            } else {
               ServerWorld serverWorld = pointer.world();
               BlockPos blockPos = pointer.pos();
               BlockPos blockPos2 = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
               if (!serverWorld.getBlockState(blockPos2).isIn(BlockTags.CONVERTABLE_TO_MUD)) {
                  return this.fallbackBehavior.dispense(pointer, stack);
               } else {
                  if (!serverWorld.isClient) {
                     for(int i = 0; i < 5; ++i) {
                        serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + serverWorld.random.nextDouble(), (double)(blockPos.getY() + 1), (double)blockPos.getZ() + serverWorld.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                     }
                  }

                  serverWorld.playSound((Entity)null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  serverWorld.emitGameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
                  serverWorld.setBlockState(blockPos2, Blocks.MUD.getDefaultState());
                  return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
               }
            }
         }
      });
      DispenserBlock.registerBehavior(Items.MINECART, new MinecartDispenserBehavior(EntityType.MINECART));
      DispenserBlock.registerBehavior(Items.CHEST_MINECART, new MinecartDispenserBehavior(EntityType.CHEST_MINECART));
      DispenserBlock.registerBehavior(Items.FURNACE_MINECART, new MinecartDispenserBehavior(EntityType.FURNACE_MINECART));
      DispenserBlock.registerBehavior(Items.TNT_MINECART, new MinecartDispenserBehavior(EntityType.TNT_MINECART));
      DispenserBlock.registerBehavior(Items.HOPPER_MINECART, new MinecartDispenserBehavior(EntityType.HOPPER_MINECART));
      DispenserBlock.registerBehavior(Items.COMMAND_BLOCK_MINECART, new MinecartDispenserBehavior(EntityType.COMMAND_BLOCK_MINECART));
   }
}
