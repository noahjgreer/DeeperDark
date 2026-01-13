/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;

static class RabbitEntity.EatCarrotCropGoal
extends MoveToTargetPosGoal {
    private final RabbitEntity rabbit;
    private boolean wantsCarrots;
    private boolean hasTarget;

    public RabbitEntity.EatCarrotCropGoal(RabbitEntity rabbit) {
        super(rabbit, 0.7f, 16);
        this.rabbit = rabbit;
    }

    @Override
    public boolean canStart() {
        if (this.cooldown <= 0) {
            if (!RabbitEntity.EatCarrotCropGoal.getServerWorld(this.rabbit).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
                return false;
            }
            this.hasTarget = false;
            this.wantsCarrots = this.rabbit.wantsCarrots();
        }
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return this.hasTarget && super.shouldContinue();
    }

    @Override
    public void tick() {
        super.tick();
        this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, 10.0f, this.rabbit.getMaxLookPitchChange());
        if (this.hasReached()) {
            World world = this.rabbit.getEntityWorld();
            BlockPos blockPos = this.targetPos.up();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (this.hasTarget && block instanceof CarrotsBlock) {
                int i = blockState.get(CarrotsBlock.AGE);
                if (i == 0) {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                    world.breakBlock(blockPos, true, this.rabbit);
                } else {
                    world.setBlockState(blockPos, (BlockState)blockState.with(CarrotsBlock.AGE, i - 1), 2);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(this.rabbit));
                    world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
                }
                this.rabbit.moreCarrotTicks = 40;
            }
            this.hasTarget = false;
            this.cooldown = 10;
        }
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isOf(Blocks.FARMLAND) && this.wantsCarrots && !this.hasTarget && (blockState = world.getBlockState(pos.up())).getBlock() instanceof CarrotsBlock && ((CarrotsBlock)blockState.getBlock()).isMature(blockState)) {
            this.hasTarget = true;
            return true;
        }
        return false;
    }
}
