/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.CopperChestBlock
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.enums.ChestType
 *  net.minecraft.item.HoneycombItem
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.collect.BiMap;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class CopperChestBlock
extends ChestBlock {
    public static final MapCodec<CopperChestBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(CopperChestBlock::getOxidationLevel), (App)Registries.SOUND_EVENT.getCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenSound), (App)Registries.SOUND_EVENT.getCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseSound), (App)CopperChestBlock.createSettingsCodec()).apply((Applicative)instance, CopperChestBlock::new));
    private static final Map<Block, Supplier<Block>> FROM_COPPER_BLOCK = Map.of(Blocks.COPPER_BLOCK, () -> Blocks.COPPER_CHEST, Blocks.EXPOSED_COPPER, () -> Blocks.EXPOSED_COPPER_CHEST, Blocks.WEATHERED_COPPER, () -> Blocks.WEATHERED_COPPER_CHEST, Blocks.OXIDIZED_COPPER, () -> Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_COPPER_BLOCK, () -> Blocks.COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER, () -> Blocks.EXPOSED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER, () -> Blocks.WEATHERED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER, () -> Blocks.OXIDIZED_COPPER_CHEST);
    private final Oxidizable.OxidationLevel oxidationLevel;

    public MapCodec<? extends CopperChestBlock> getCodec() {
        return CODEC;
    }

    public CopperChestBlock(Oxidizable.OxidationLevel oxidationLevel, SoundEvent openSound, SoundEvent closeSound, AbstractBlock.Settings settings) {
        super(() -> BlockEntityType.CHEST, openSound, closeSound, settings);
        this.oxidationLevel = oxidationLevel;
    }

    public boolean canMergeWith(BlockState state) {
        return state.isIn(BlockTags.COPPER_CHESTS) && state.contains((Property)ChestBlock.CHEST_TYPE);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        return CopperChestBlock.getNewState((BlockState)blockState, (World)ctx.getWorld(), (BlockPos)ctx.getBlockPos());
    }

    private static BlockState getNewState(BlockState state, World world, BlockPos pos) {
        Block block;
        BlockState blockState = world.getBlockState(pos.offset(CopperChestBlock.getFacing((BlockState)state)));
        if (!((ChestType)state.get((Property)ChestBlock.CHEST_TYPE)).equals((Object)ChestType.SINGLE) && (block = state.getBlock()) instanceof CopperChestBlock) {
            CopperChestBlock copperChestBlock = (CopperChestBlock)block;
            block = blockState.getBlock();
            if (block instanceof CopperChestBlock) {
                CopperChestBlock copperChestBlock2 = (CopperChestBlock)block;
                BlockState blockState2 = state;
                BlockState blockState3 = blockState;
                if (copperChestBlock.isWaxed() != copperChestBlock2.isWaxed()) {
                    blockState2 = CopperChestBlock.getUnwaxed((CopperChestBlock)copperChestBlock, (BlockState)state).orElse(blockState2);
                    blockState3 = CopperChestBlock.getUnwaxed((CopperChestBlock)copperChestBlock2, (BlockState)blockState).orElse(blockState3);
                }
                Block block2 = copperChestBlock.oxidationLevel.ordinal() <= copperChestBlock2.oxidationLevel.ordinal() ? blockState2.getBlock() : blockState3.getBlock();
                return block2.getStateWithProperties(blockState2);
            }
        }
        return state;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        ChestType chestType;
        BlockState blockState = super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        if (this.canMergeWith(neighborState) && !(chestType = (ChestType)blockState.get((Property)ChestBlock.CHEST_TYPE)).equals((Object)ChestType.SINGLE) && CopperChestBlock.getFacing((BlockState)blockState) == direction) {
            return neighborState.getBlock().getStateWithProperties(blockState);
        }
        return blockState;
    }

    private static Optional<BlockState> getUnwaxed(CopperChestBlock block, BlockState state) {
        if (!block.isWaxed()) {
            return Optional.of(state);
        }
        return Optional.ofNullable((Block)((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get((Object)state.getBlock())).map(waxedState -> waxedState.getStateWithProperties(state));
    }

    public Oxidizable.OxidationLevel getOxidationLevel() {
        return this.oxidationLevel;
    }

    public static BlockState fromCopperBlock(Block block, Direction facing, World world, BlockPos pos) {
        CopperChestBlock copperChestBlock = (CopperChestBlock)FROM_COPPER_BLOCK.getOrDefault(block, () -> ((Block)Blocks.COPPER_CHEST).asBlock()).get();
        ChestType chestType = copperChestBlock.getChestType(world, pos, facing);
        BlockState blockState = (BlockState)((BlockState)copperChestBlock.getDefaultState().with((Property)FACING, (Comparable)facing)).with((Property)CHEST_TYPE, (Comparable)chestType);
        return CopperChestBlock.getNewState((BlockState)blockState, (World)world, (BlockPos)pos);
    }

    public boolean isWaxed() {
        return true;
    }

    public boolean keepBlockEntityWhenReplacedWith(BlockState state) {
        return state.isIn(BlockTags.COPPER_CHESTS);
    }
}

