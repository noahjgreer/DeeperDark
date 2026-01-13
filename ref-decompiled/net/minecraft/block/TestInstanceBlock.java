/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.OperatorBlock
 *  net.minecraft.block.TestInstanceBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.ActionResult
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
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class TestInstanceBlock
extends BlockWithEntity
implements OperatorBlock {
    public static final MapCodec<TestInstanceBlock> CODEC = TestInstanceBlock.createCodec(TestInstanceBlock::new);

    public TestInstanceBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TestInstanceBlockEntity(pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TestInstanceBlockEntity)) {
            return ActionResult.PASS;
        }
        TestInstanceBlockEntity testInstanceBlockEntity = (TestInstanceBlockEntity)blockEntity;
        if (!player.isCreativeLevelTwoOp()) {
            return ActionResult.PASS;
        }
        if (player.getEntityWorld().isClient()) {
            player.openTestInstanceBlockScreen(testInstanceBlockEntity);
        }
        return ActionResult.SUCCESS;
    }

    protected MapCodec<TestInstanceBlock> getCodec() {
        return CODEC;
    }
}

