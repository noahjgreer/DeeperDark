/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BeaconBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Stainable
 *  net.minecraft.block.entity.BeaconBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.stat.Stats
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BeaconBlock
extends BlockWithEntity
implements Stainable {
    public static final MapCodec<BeaconBlock> CODEC = BeaconBlock.createCodec(BeaconBlock::new);

    public MapCodec<BeaconBlock> getCodec() {
        return CODEC;
    }

    public BeaconBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeaconBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BeaconBlock.validateTicker(type, (BlockEntityType)BlockEntityType.BEACON, BeaconBlockEntity::tick);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient() && (blockEntity = world.getBlockEntity(pos)) instanceof BeaconBlockEntity) {
            BeaconBlockEntity beaconBlockEntity = (BeaconBlockEntity)blockEntity;
            player.openHandledScreen((NamedScreenHandlerFactory)beaconBlockEntity);
            player.incrementStat(Stats.INTERACT_WITH_BEACON);
        }
        return ActionResult.SUCCESS;
    }
}

