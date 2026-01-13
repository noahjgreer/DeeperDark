/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.EyeblossomBlock$EyeblossomState
 *  net.minecraft.block.FlowerPotBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.stat.Stats
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.TriState
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EyeblossomBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TriState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class FlowerPotBlock
extends Block {
    public static final MapCodec<FlowerPotBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("potted").forGetter(block -> block.content), (App)FlowerPotBlock.createSettingsCodec()).apply((Applicative)instance, FlowerPotBlock::new));
    private static final Map<Block, Block> CONTENT_TO_POTTED = Maps.newHashMap();
    private static final VoxelShape SHAPE = Block.createColumnShape((double)6.0, (double)0.0, (double)6.0);
    private final Block content;

    public MapCodec<FlowerPotBlock> getCodec() {
        return CODEC;
    }

    public FlowerPotBlock(Block content, AbstractBlock.Settings settings) {
        super(settings);
        this.content = content;
        CONTENT_TO_POTTED.put(content, this);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Block block;
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            block = CONTENT_TO_POTTED.getOrDefault(blockItem.getBlock(), Blocks.AIR);
        } else {
            block = Blocks.AIR;
        }
        BlockState blockState = block.getDefaultState();
        if (blockState.isAir()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!this.isEmpty()) {
            return ActionResult.CONSUME;
        }
        world.setBlockState(pos, blockState, 3);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
        player.incrementStat(Stats.POT_FLOWER);
        stack.decrementUnlessCreative(1, (LivingEntity)player);
        return ActionResult.SUCCESS;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (this.isEmpty()) {
            return ActionResult.CONSUME;
        }
        ItemStack itemStack = new ItemStack((ItemConvertible)this.content);
        if (!player.giveItemStack(itemStack)) {
            player.dropItem(itemStack, false);
        }
        world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
        return ActionResult.SUCCESS;
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        if (this.isEmpty()) {
            return super.getPickStack(world, pos, state, includeData);
        }
        return new ItemStack((ItemConvertible)this.content);
    }

    private boolean isEmpty() {
        return this.content == Blocks.AIR;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public Block getContent() {
        return this.content;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected boolean hasRandomTicks(BlockState state) {
        return state.isOf(Blocks.POTTED_OPEN_EYEBLOSSOM) || state.isOf(Blocks.POTTED_CLOSED_EYEBLOSSOM);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl2;
        boolean bl;
        if (this.hasRandomTicks(state) && (bl = this.content == Blocks.OPEN_EYEBLOSSOM) != (bl2 = ((TriState)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.EYEBLOSSOM_OPEN_GAMEPLAY, pos)).asBoolean(bl))) {
            world.setBlockState(pos, this.getToggledState(state), 3);
            EyeblossomBlock.EyeblossomState eyeblossomState = EyeblossomBlock.EyeblossomState.of((boolean)bl).getOpposite();
            eyeblossomState.spawnTrailParticle(world, pos, random);
            world.playSound(null, pos, eyeblossomState.getLongSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.randomTick(state, world, pos, random);
    }

    public BlockState getToggledState(BlockState state) {
        if (state.isOf(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
            return Blocks.POTTED_CLOSED_EYEBLOSSOM.getDefaultState();
        }
        if (state.isOf(Blocks.POTTED_CLOSED_EYEBLOSSOM)) {
            return Blocks.POTTED_OPEN_EYEBLOSSOM.getDefaultState();
        }
        return state;
    }
}

