/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CakeBlock
 *  net.minecraft.block.CandleBlock
 *  net.minecraft.block.CandleCakeBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class CakeBlock
extends Block {
    public static final MapCodec<CakeBlock> CODEC = CakeBlock.createCodec(CakeBlock::new);
    public static final int MAX_BITES = 6;
    public static final IntProperty BITES = Properties.BITES;
    public static final int DEFAULT_COMPARATOR_OUTPUT = CakeBlock.getComparatorOutput((int)0);
    private static final VoxelShape[] SHAPES_BY_BITES = Block.createShapeArray((int)6, bites -> Block.createCuboidShape((double)(1 + bites * 2), (double)0.0, (double)1.0, (double)15.0, (double)8.0, (double)15.0));

    public MapCodec<CakeBlock> getCodec() {
        return CODEC;
    }

    public CakeBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)BITES, (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_BITES[(Integer)state.get((Property)BITES)];
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Block block;
        Item item = stack.getItem();
        if (!stack.isIn(ItemTags.CANDLES) || (Integer)state.get((Property)BITES) != 0 || !((block = Block.getBlockFromItem((Item)item)) instanceof CandleBlock)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        CandleBlock candleBlock = (CandleBlock)block;
        stack.decrementUnlessCreative(1, (LivingEntity)player);
        world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.setBlockState(pos, CandleCakeBlock.getCandleCakeFromCandle((CandleBlock)candleBlock));
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
        player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
        return ActionResult.SUCCESS;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            if (CakeBlock.tryEat((WorldAccess)world, (BlockPos)pos, (BlockState)state, (PlayerEntity)player).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                return ActionResult.CONSUME;
            }
        }
        return CakeBlock.tryEat((WorldAccess)world, (BlockPos)pos, (BlockState)state, (PlayerEntity)player);
    }

    protected static ActionResult tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        player.incrementStat(Stats.EAT_CAKE_SLICE);
        player.getHungerManager().add(2, 0.1f);
        int i = (Integer)state.get((Property)BITES);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.EAT, pos);
        if (i < 6) {
            world.setBlockState(pos, (BlockState)state.with((Property)BITES, (Comparable)Integer.valueOf(i + 1)), 3);
        } else {
            world.removeBlock(pos, false);
            world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_DESTROY, pos);
        }
        return ActionResult.SUCCESS;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BITES});
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return CakeBlock.getComparatorOutput((int)((Integer)state.get((Property)BITES)));
    }

    public static int getComparatorOutput(int bites) {
        return (7 - bites) * 2;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

