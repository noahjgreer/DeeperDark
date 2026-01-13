/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCauldronBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LavaCauldronBlock
 *  net.minecraft.block.cauldron.CauldronBehavior
 *  net.minecraft.entity.CollisionEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LavaCauldronBlock
extends AbstractCauldronBlock {
    public static final MapCodec<LavaCauldronBlock> CODEC = LavaCauldronBlock.createCodec(LavaCauldronBlock::new);
    private static final VoxelShape LAVA_SHAPE = Block.createColumnShape((double)12.0, (double)4.0, (double)15.0);
    private static final VoxelShape INSIDE_COLLISION_SHAPE = VoxelShapes.union((VoxelShape)AbstractCauldronBlock.OUTLINE_SHAPE, (VoxelShape)LAVA_SHAPE);

    public MapCodec<LavaCauldronBlock> getCodec() {
        return CODEC;
    }

    public LavaCauldronBlock(AbstractBlock.Settings settings) {
        super(settings, CauldronBehavior.LAVA_CAULDRON_BEHAVIOR);
    }

    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    public boolean isFull(BlockState state) {
        return true;
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return INSIDE_COLLISION_SHAPE;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        handler.addEvent(CollisionEvent.CLEAR_FREEZE);
        handler.addEvent(CollisionEvent.LAVA_IGNITE);
        handler.addPostCallback(CollisionEvent.LAVA_IGNITE, Entity::setOnFireFromLava);
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return 3;
    }
}

