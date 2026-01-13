/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DoubleBlockProperties
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.DoubleBlockProperties$PropertySource
 *  net.minecraft.block.DoubleBlockProperties$PropertySource$Pair
 *  net.minecraft.block.DoubleBlockProperties$PropertySource$Single
 *  net.minecraft.block.DoubleBlockProperties$Type
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class DoubleBlockProperties {
    public static <S extends BlockEntity> PropertySource<S> toPropertySource(BlockEntityType<S> blockEntityType, Function<BlockState, Type> typeMapper, Function<BlockState, Direction> directionMapper, Property<Direction> facingProperty, BlockState state, WorldAccess world, BlockPos pos, BiPredicate<WorldAccess, BlockPos> fallbackTester) {
        Type type2;
        boolean bl2;
        BlockEntity blockEntity = blockEntityType.get((BlockView)world, pos);
        if (blockEntity == null) {
            return PropertyRetriever::getFallback;
        }
        if (fallbackTester.test(world, pos)) {
            return PropertyRetriever::getFallback;
        }
        Type type = typeMapper.apply(state);
        boolean bl = type == Type.SINGLE;
        boolean bl3 = bl2 = type == Type.FIRST;
        if (bl) {
            return new PropertySource.Single((Object)blockEntity);
        }
        BlockPos blockPos = pos.offset(directionMapper.apply(state));
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(state.getBlock()) && (type2 = typeMapper.apply(blockState)) != Type.SINGLE && type != type2 && blockState.get(facingProperty) == state.get(facingProperty)) {
            if (fallbackTester.test(world, blockPos)) {
                return PropertyRetriever::getFallback;
            }
            BlockEntity blockEntity2 = blockEntityType.get((BlockView)world, blockPos);
            if (blockEntity2 != null) {
                BlockEntity blockEntity3 = bl2 ? blockEntity : blockEntity2;
                BlockEntity blockEntity4 = bl2 ? blockEntity2 : blockEntity;
                return new PropertySource.Pair((Object)blockEntity3, (Object)blockEntity4);
            }
        }
        return new PropertySource.Single((Object)blockEntity);
    }
}

