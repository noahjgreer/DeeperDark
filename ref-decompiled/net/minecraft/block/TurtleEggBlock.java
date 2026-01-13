/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TurtleEggBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.mob.ZombieEntity
 *  net.minecraft.entity.passive.BatEntity
 *  net.minecraft.entity.passive.TurtleEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TurtleEggBlock
extends Block {
    public static final MapCodec<TurtleEggBlock> CODEC = TurtleEggBlock.createCodec(TurtleEggBlock::new);
    public static final IntProperty HATCH = Properties.HATCH;
    public static final IntProperty EGGS = Properties.EGGS;
    public static final int field_31272 = 2;
    public static final int field_31273 = 1;
    public static final int field_31274 = 4;
    private static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape((double)3.0, (double)0.0, (double)3.0, (double)12.0, (double)7.0, (double)12.0);
    private static final VoxelShape MULTIPLE_SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)7.0);

    public MapCodec<TurtleEggBlock> getCodec() {
        return CODEC;
    }

    public TurtleEggBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HATCH, (Comparable)Integer.valueOf(0))).with((Property)EGGS, (Comparable)Integer.valueOf(1)));
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects()) {
            this.tryBreakEgg(world, state, pos, entity, 100);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, state, pos, entity, 3);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    private void tryBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance) {
        ServerWorld serverWorld;
        if (state.isOf(Blocks.TURTLE_EGG) && world instanceof ServerWorld && this.breaksEgg(serverWorld = (ServerWorld)world, entity) && world.random.nextInt(inverseChance) == 0) {
            this.breakEgg((World)serverWorld, pos, state);
        }
    }

    private void breakEgg(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        int i = (Integer)state.get((Property)EGGS);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, (BlockState)state.with((Property)EGGS, (Comparable)Integer.valueOf(i - 1)), 2);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of((BlockState)state));
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState((BlockState)state));
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress((World)world, pos) && TurtleEggBlock.isSandBelow((BlockView)world, (BlockPos)pos)) {
            int i = (Integer)state.get((Property)HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.setBlockState(pos, (BlockState)state.with((Property)HATCH, (Comparable)Integer.valueOf(i + 1)), 2);
                world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)state));
            } else {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                world.removeBlock(pos, false);
                world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of((BlockState)state));
                for (int j = 0; j < (Integer)state.get((Property)EGGS); ++j) {
                    world.syncWorldEvent(2001, pos, Block.getRawIdFromState((BlockState)state));
                    TurtleEntity turtleEntity = (TurtleEntity)EntityType.TURTLE.create((World)world, SpawnReason.BREEDING);
                    if (turtleEntity == null) continue;
                    turtleEntity.setBreedingAge(-24000);
                    turtleEntity.setHomePos(pos);
                    turtleEntity.refreshPositionAndAngles((double)pos.getX() + 0.3 + (double)j * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0f, 0.0f);
                    world.spawnEntity((Entity)turtleEntity);
                }
            }
        }
    }

    public static boolean isSandBelow(BlockView world, BlockPos pos) {
        return TurtleEggBlock.isSand((BlockView)world, (BlockPos)pos.down());
    }

    public static boolean isSand(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).isIn(BlockTags.SAND);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (TurtleEggBlock.isSandBelow((BlockView)world, (BlockPos)pos) && !world.isClient()) {
            world.syncWorldEvent(2012, pos, 15);
        }
    }

    private boolean shouldHatchProgress(World world, BlockPos pos) {
        float f = ((Float)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.TURTLE_EGG_HATCH_CHANCE_GAMEPLAY, pos)).floatValue();
        return f > 0.0f && world.random.nextFloat() < f;
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        this.breakEgg(world, pos, state);
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && (Integer)state.get((Property)EGGS) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block)this)) {
            return (BlockState)blockState.with((Property)EGGS, (Comparable)Integer.valueOf(Math.min(4, (Integer)blockState.get((Property)EGGS) + 1)));
        }
        return super.getPlacementState(ctx);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Integer)state.get((Property)EGGS) == 1 ? SINGLE_SHAPE : MULTIPLE_SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HATCH, EGGS});
    }

    private boolean breaksEgg(ServerWorld world, Entity entity) {
        if (entity instanceof TurtleEntity || entity instanceof BatEntity) {
            return false;
        }
        if (entity instanceof LivingEntity) {
            return entity instanceof PlayerEntity || (Boolean)world.getGameRules().getValue(GameRules.DO_MOB_GRIEFING) != false;
        }
        return false;
    }
}

