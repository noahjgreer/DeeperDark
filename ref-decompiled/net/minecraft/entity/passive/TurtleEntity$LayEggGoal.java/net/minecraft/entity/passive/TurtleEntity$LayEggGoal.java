/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

static class TurtleEntity.LayEggGoal
extends MoveToTargetPosGoal {
    private final TurtleEntity turtle;

    TurtleEntity.LayEggGoal(TurtleEntity turtle, double speed) {
        super(turtle, speed, 16);
        this.turtle = turtle;
    }

    @Override
    public boolean canStart() {
        if (this.turtle.hasEgg() && this.turtle.homePos.isWithinDistance(this.turtle.getEntityPos(), 9.0)) {
            return super.canStart();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.homePos.isWithinDistance(this.turtle.getEntityPos(), 9.0);
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos blockPos = this.turtle.getBlockPos();
        if (!this.turtle.isTouchingWater() && this.hasReached()) {
            if (this.turtle.sandDiggingCounter < 1) {
                this.turtle.setDiggingSand(true);
            } else if (this.turtle.sandDiggingCounter > this.getTickCount(200)) {
                World world = this.turtle.getEntityWorld();
                world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3f, 0.9f + world.random.nextFloat() * 0.2f);
                BlockPos blockPos2 = this.targetPos.up();
                BlockState blockState = (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1);
                world.setBlockState(blockPos2, blockState, 3);
                world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos2, GameEvent.Emitter.of(this.turtle, blockState));
                this.turtle.setHasEgg(false);
                this.turtle.setDiggingSand(false);
                this.turtle.setLoveTicks(600);
            }
            if (this.turtle.isDiggingSand()) {
                ++this.turtle.sandDiggingCounter;
            }
        }
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        if (!world.isAir(pos.up())) {
            return false;
        }
        return TurtleEggBlock.isSand(world, pos);
    }
}
