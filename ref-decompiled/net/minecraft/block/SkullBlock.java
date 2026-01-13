/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractSkullBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SkullBlock
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RotationPropertyHelper
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/*
 * Exception performing whole class analysis ignored.
 */
public class SkullBlock
extends AbstractSkullBlock {
    public static final MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), (App)SkullBlock.createSettingsCodec()).apply((Applicative)instance, SkullBlock::new));
    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)8.0, (double)0.0, (double)8.0);
    private static final VoxelShape PIGLIN_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)8.0);

    public MapCodec<? extends SkullBlock> getCodec() {
        return CODEC;
    }

    public SkullBlock(SkullType skullType, AbstractBlock.Settings settings) {
        super(skullType, settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)ROTATION, (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getSkullType() == Type.PIGLIN ? PIGLIN_SHAPE : SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)super.getPlacementState(ctx).with((Property)ROTATION, (Comparable)Integer.valueOf(RotationPropertyHelper.fromYaw((float)ctx.getPlayerYaw())));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(rotation.rotate(((Integer)state.get((Property)ROTATION)).intValue(), MAX_ROTATIONS)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(mirror.mirror(((Integer)state.get((Property)ROTATION)).intValue(), MAX_ROTATIONS)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(new Property[]{ROTATION});
    }
}

