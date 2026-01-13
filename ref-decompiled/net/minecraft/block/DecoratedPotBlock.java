/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.DecoratedPotBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity$WobbleType
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.EnchantmentTags
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class DecoratedPotBlock
extends BlockWithEntity
implements Waterloggable {
    public static final MapCodec<DecoratedPotBlock> CODEC = DecoratedPotBlock.createCodec(DecoratedPotBlock::new);
    public static final Identifier SHERDS_DYNAMIC_DROP_ID = Identifier.ofVanilla((String)"sherds");
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty CRACKED = Properties.CRACKED;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);

    public MapCodec<DecoratedPotBlock> getCodec() {
        return CODEC;
    }

    public DecoratedPotBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)CRACKED, (Comparable)Boolean.valueOf(false)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing())).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER))).with((Property)CRACKED, (Comparable)Boolean.valueOf(false));
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DecoratedPotBlockEntity)) {
            return ActionResult.PASS;
        }
        DecoratedPotBlockEntity decoratedPotBlockEntity = (DecoratedPotBlockEntity)blockEntity;
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = decoratedPotBlockEntity.getStack();
        if (!stack.isEmpty() && (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual((ItemStack)itemStack, (ItemStack)stack) && itemStack.getCount() < itemStack.getMaxCount())) {
            float f;
            decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
            player.incrementStat(Stats.USED.getOrCreateStat((Object)stack.getItem()));
            ItemStack itemStack2 = stack.splitUnlessCreative(1, (LivingEntity)player);
            if (decoratedPotBlockEntity.isEmpty()) {
                decoratedPotBlockEntity.setStack(itemStack2);
                f = (float)itemStack2.getCount() / (float)itemStack2.getMaxCount();
            } else {
                itemStack.increment(1);
                f = (float)itemStack.getCount() / (float)itemStack.getMaxCount();
            }
            world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0f, 0.7f + 0.5f * f);
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                serverWorld.spawnParticles((ParticleEffect)ParticleTypes.DUST_PLUME, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
            }
            decoratedPotBlockEntity.markDirty();
            world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DecoratedPotBlockEntity)) {
            return ActionResult.PASS;
        }
        DecoratedPotBlockEntity decoratedPotBlockEntity = (DecoratedPotBlockEntity)blockEntity;
        world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT_FAIL, SoundCategory.BLOCKS, 1.0f, 1.0f);
        decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.NEGATIVE);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
        return ActionResult.SUCCESS;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, WATERLOGGED, CRACKED});
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPotBlockEntity(pos, state);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof DecoratedPotBlockEntity) {
            DecoratedPotBlockEntity decoratedPotBlockEntity = (DecoratedPotBlockEntity)blockEntity;
            builder.addDynamicDrop(SHERDS_DYNAMIC_DROP_ID, lootConsumer -> {
                for (Item item : decoratedPotBlockEntity.getSherds().toList()) {
                    lootConsumer.accept(item.getDefaultStack());
                }
            });
        }
        return super.getDroppedStacks(state, builder);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        BlockState blockState = state;
        if (itemStack.isIn(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasAnyEnchantmentsIn((ItemStack)itemStack, (TagKey)EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
            blockState = (BlockState)state.with((Property)CRACKED, (Comparable)Boolean.valueOf(true));
            world.setBlockState(pos, blockState, 260);
        }
        return super.onBreak(world, pos, blockState, player);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected BlockSoundGroup getSoundGroup(BlockState state) {
        if (((Boolean)state.get((Property)CRACKED)).booleanValue()) {
            return BlockSoundGroup.DECORATED_POT_SHATTER;
        }
        return BlockSoundGroup.DECORATED_POT;
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        ServerWorld serverWorld;
        BlockPos blockPos = hit.getBlockPos();
        if (world instanceof ServerWorld && projectile.canModifyAt(serverWorld = (ServerWorld)world, blockPos) && projectile.canBreakBlocks(serverWorld)) {
            world.setBlockState(blockPos, (BlockState)state.with((Property)CRACKED, (Comparable)Boolean.valueOf(true)), 260);
            world.breakBlock(blockPos, true, (Entity)projectile);
        }
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DecoratedPotBlockEntity) {
            DecoratedPotBlockEntity decoratedPotBlockEntity = (DecoratedPotBlockEntity)blockEntity;
            Sherds sherds = decoratedPotBlockEntity.getSherds();
            return DecoratedPotBlockEntity.getStackWith((Sherds)sherds);
        }
        return super.getPickStack(world, pos, state, includeData);
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((BlockEntity)world.getBlockEntity(pos));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }
}

