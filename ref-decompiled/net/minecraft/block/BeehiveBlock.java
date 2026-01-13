/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BeehiveBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.CampfireBlock
 *  net.minecraft.block.FireBlock
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.entity.BeehiveBlockEntity
 *  net.minecraft.block.entity.BeehiveBlockEntity$BeeState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BlockStateComponent
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.TntEntity
 *  net.minecraft.entity.boss.WitherEntity
 *  net.minecraft.entity.mob.CreeperEntity
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.WitherSkullEntity
 *  net.minecraft.entity.vehicle.TntMinecartEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.loot.LootTables
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.EnchantmentTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BeehiveBlock
extends BlockWithEntity {
    public static final MapCodec<BeehiveBlock> CODEC = BeehiveBlock.createCodec(BeehiveBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;
    public static final int FULL_HONEY_LEVEL = 5;

    public MapCodec<BeehiveBlock> getCodec() {
        return CODEC;
    }

    public BeehiveBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HONEY_LEVEL, (Comparable)Integer.valueOf(0))).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)HONEY_LEVEL);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!world.isClient() && blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            if (!EnchantmentHelper.hasAnyEnchantmentsIn((ItemStack)tool, (TagKey)EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING)) {
                beehiveBlockEntity.angerBees(player, state, BeehiveBlockEntity.BeeState.EMERGENCY);
                ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
                this.angerNearbyBees(world, pos);
            }
            Criteria.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity)player, state, tool, beehiveBlockEntity.getBeeCount());
        }
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        super.onExploded(state, world, pos, explosion, stackMerger);
        this.angerNearbyBees((World)world, pos);
    }

    private void angerNearbyBees(World world, BlockPos pos) {
        Box box = new Box(pos).expand(8.0, 6.0, 8.0);
        List list = world.getNonSpectatingEntities(BeeEntity.class, box);
        if (!list.isEmpty()) {
            List list2 = world.getNonSpectatingEntities(PlayerEntity.class, box);
            if (list2.isEmpty()) {
                return;
            }
            for (BeeEntity beeEntity : list) {
                if (beeEntity.getTarget() != null) continue;
                PlayerEntity playerEntity = (PlayerEntity)Util.getRandom((List)list2, (Random)world.random);
                beeEntity.setTarget((LivingEntity)playerEntity);
            }
        }
    }

    public static void dropHoneycomb(ServerWorld world, ItemStack tool, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Entity interactingEntity, BlockPos pos) {
        BeehiveBlock.generateBlockInteractLoot((ServerWorld)world, (RegistryKey)LootTables.BEEHIVE_HARVEST, (BlockState)state, (BlockEntity)blockEntity, (ItemStack)tool, (Entity)interactingEntity, (worldx, stack) -> BeehiveBlock.dropStack((World)worldx, (BlockPos)pos, (ItemStack)stack));
    }

    /*
     * Unable to fully structure code
     */
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        block11: {
            i = (Integer)state.get((Property)BeehiveBlock.HONEY_LEVEL);
            bl = false;
            if (i < 5) break block11;
            item = stack.getItem();
            if (!(world instanceof ServerWorld)) ** GOTO lbl-1000
            serverWorld = (ServerWorld)world;
            if (stack.isOf(Items.SHEARS)) {
                BeehiveBlock.dropHoneycomb((ServerWorld)serverWorld, (ItemStack)stack, (BlockState)state, (BlockEntity)world.getBlockEntity(pos), (Entity)player, (BlockPos)pos);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.damage(1, (LivingEntity)player, hand.getEquipmentSlot());
                bl = true;
                world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.SHEAR, pos);
            } else if (stack.isOf(Items.GLASS_BOTTLE)) {
                stack.decrement(1);
                world.playSound((Entity)player, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (stack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack((ItemConvertible)Items.HONEY_BOTTLE));
                } else if (!player.getInventory().insertStack(new ItemStack((ItemConvertible)Items.HONEY_BOTTLE))) {
                    player.dropItem(new ItemStack((ItemConvertible)Items.HONEY_BOTTLE), false);
                }
                bl = true;
                world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.FLUID_PICKUP, pos);
            }
            if (!world.isClient() && bl) {
                player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
            }
        }
        if (bl) {
            if (!CampfireBlock.isLitCampfireInRange((World)world, (BlockPos)pos)) {
                if (this.hasBees(world, pos)) {
                    this.angerNearbyBees(world, pos);
                }
                this.takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
            } else {
                this.takeHoney(world, state, pos);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private boolean hasBees(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            return !beehiveBlockEntity.hasNoBees();
        }
        return false;
    }

    public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveBlockEntity.BeeState beeState) {
        this.takeHoney(world, state, pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees(player, state, beeState);
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, (BlockState)state.with((Property)HONEY_LEVEL, (Comparable)Integer.valueOf(0)), 3);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Integer)state.get((Property)HONEY_LEVEL) >= 5) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.spawnHoneyParticles(world, pos, state);
            }
        }
    }

    private void spawnHoneyParticles(World world, BlockPos pos, BlockState state) {
        if (!state.getFluidState().isEmpty() || world.random.nextFloat() < 0.3f) {
            return;
        }
        VoxelShape voxelShape = state.getCollisionShape((BlockView)world, pos);
        double d = voxelShape.getMax(Direction.Axis.Y);
        if (d >= 1.0 && !state.isIn(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMin(Direction.Axis.Y);
            if (e > 0.0) {
                this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() + e - 0.05);
            } else {
                BlockPos blockPos = pos.down();
                BlockState blockState = world.getBlockState(blockPos);
                VoxelShape voxelShape2 = blockState.getCollisionShape((BlockView)world, blockPos);
                double f = voxelShape2.getMax(Direction.Axis.Y);
                if ((f < 1.0 || !blockState.isFullCube((BlockView)world, blockPos)) && blockState.getFluidState().isEmpty()) {
                    this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() - 0.05);
                }
            }
        }
    }

    private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double height) {
        this.addHoneyParticle(world, (double)pos.getX() + shape.getMin(Direction.Axis.X), (double)pos.getX() + shape.getMax(Direction.Axis.X), (double)pos.getZ() + shape.getMin(Direction.Axis.Z), (double)pos.getZ() + shape.getMax(Direction.Axis.Z), height);
    }

    private void addHoneyParticle(World world, double minX, double maxX, double minZ, double maxZ, double height) {
        world.addParticleClient((ParticleEffect)ParticleTypes.DRIPPING_HONEY, MathHelper.lerp((double)world.random.nextDouble(), (double)minX, (double)maxX), height, MathHelper.lerp((double)world.random.nextDouble(), (double)minZ, (double)maxZ), 0.0, 0.0, 0.0);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HONEY_LEVEL, FACING});
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeehiveBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : BeehiveBlock.validateTicker(type, (BlockEntityType)BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world instanceof ServerWorld) {
            BlockEntity blockEntity;
            ServerWorld serverWorld = (ServerWorld)world;
            if (player.shouldSkipBlockDrops() && ((Boolean)serverWorld.getGameRules().getValue(GameRules.DO_TILE_DROPS)).booleanValue() && (blockEntity = world.getBlockEntity(pos)) instanceof BeehiveBlockEntity) {
                boolean bl;
                BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
                int i = (Integer)state.get((Property)HONEY_LEVEL);
                boolean bl2 = bl = !beehiveBlockEntity.hasNoBees();
                if (bl || i > 0) {
                    ItemStack itemStack = new ItemStack((ItemConvertible)this);
                    itemStack.applyComponentsFrom(beehiveBlockEntity.createComponentMap());
                    itemStack.set(DataComponentTypes.BLOCK_STATE, (Object)BlockStateComponent.DEFAULT.with((Property)HONEY_LEVEL, (Comparable)Integer.valueOf(i)));
                    ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
                    itemEntity.setToDefaultPickupDelay();
                    world.spawnEntity((Entity)itemEntity);
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        BlockEntity blockEntity;
        Entity entity = (Entity)builder.getOptional(LootContextParameters.THIS_ENTITY);
        if ((entity instanceof TntEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TntMinecartEntity) && (blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getDroppedStacks(state, builder);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack itemStack = super.getPickStack(world, pos, state, includeData);
        if (includeData) {
            itemStack.set(DataComponentTypes.BLOCK_STATE, (Object)BlockStateComponent.DEFAULT.with((Property)HONEY_LEVEL, (Comparable)((Integer)state.get((Property)HONEY_LEVEL))));
        }
        return itemStack;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        BlockEntity blockEntity;
        if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock && (blockEntity = world.getBlockEntity(pos)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }
}

