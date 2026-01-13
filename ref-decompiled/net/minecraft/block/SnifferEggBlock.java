/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SnifferEggBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.passive.SnifferEntity
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
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.SnifferEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public class SnifferEggBlock
extends Block {
    public static final MapCodec<SnifferEggBlock> CODEC = SnifferEggBlock.createCodec(SnifferEggBlock::new);
    public static final int FINAL_HATCH_STAGE = 2;
    public static final IntProperty HATCH = Properties.HATCH;
    private static final int HATCHING_TIME = 24000;
    private static final int BOOSTED_HATCHING_TIME = 12000;
    private static final int MAX_RANDOM_CRACK_TIME_OFFSET = 300;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)12.0, (double)0.0, (double)16.0);

    public MapCodec<SnifferEggBlock> getCodec() {
        return CODEC;
    }

    public SnifferEggBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HATCH, (Comparable)Integer.valueOf(0)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HATCH});
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public int getHatchStage(BlockState state) {
        return (Integer)state.get((Property)HATCH);
    }

    private boolean isReadyToHatch(BlockState state) {
        return this.getHatchStage(state) == 2;
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isReadyToHatch(state)) {
            world.playSound(null, pos, SoundEvents.BLOCK_SNIFFER_EGG_CRACK, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
            world.setBlockState(pos, (BlockState)state.with((Property)HATCH, (Comparable)Integer.valueOf(this.getHatchStage(state) + 1)), 2);
            return;
        }
        world.playSound(null, pos, SoundEvents.BLOCK_SNIFFER_EGG_HATCH, SoundCategory.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
        world.breakBlock(pos, false);
        SnifferEntity snifferEntity = (SnifferEntity)EntityType.SNIFFER.create((World)world, SpawnReason.BREEDING);
        if (snifferEntity != null) {
            Vec3d vec3d = pos.toCenterPos();
            snifferEntity.setBaby(true);
            snifferEntity.refreshPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), MathHelper.wrapDegrees((float)(world.random.nextFloat() * 360.0f)), 0.0f);
            world.spawnEntity((Entity)snifferEntity);
        }
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        boolean bl = SnifferEggBlock.isAboveHatchBooster((BlockView)world, (BlockPos)pos);
        if (!world.isClient() && bl) {
            world.syncWorldEvent(3009, pos, 0);
        }
        int i = bl ? 12000 : 24000;
        int j = i / 3;
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of((BlockState)state));
        world.scheduleBlockTick(pos, (Block)this, j + world.random.nextInt(300));
    }

    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public static boolean isAboveHatchBooster(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(BlockTags.SNIFFER_EGG_HATCH_BOOST);
    }
}

