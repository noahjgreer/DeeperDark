/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;

static class EndermanEntity.PlaceBlockGoal
extends Goal {
    private final EndermanEntity enderman;

    public EndermanEntity.PlaceBlockGoal(EndermanEntity enderman) {
        this.enderman = enderman;
    }

    @Override
    public boolean canStart() {
        if (this.enderman.getCarriedBlock() == null) {
            return false;
        }
        if (!EndermanEntity.PlaceBlockGoal.getServerWorld(this.enderman).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
            return false;
        }
        return this.enderman.getRandom().nextInt(EndermanEntity.PlaceBlockGoal.toGoalTicks(2000)) == 0;
    }

    @Override
    public void tick() {
        Random random = this.enderman.getRandom();
        World world = this.enderman.getEntityWorld();
        int i = MathHelper.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
        int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0);
        int k = MathHelper.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = world.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.down();
        BlockState blockState2 = world.getBlockState(blockPos2);
        BlockState blockState3 = this.enderman.getCarriedBlock();
        if (blockState3 == null) {
            return;
        }
        if (this.canPlaceOn(world, blockPos, blockState3 = Block.postProcessState(blockState3, this.enderman.getEntityWorld(), blockPos), blockState, blockState2, blockPos2)) {
            world.setBlockState(blockPos, blockState3, 3);
            world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this.enderman, blockState3));
            this.enderman.setCarriedBlock(null);
        }
    }

    private boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos) {
        return stateAbove.isAir() && !state.isAir() && !state.isOf(Blocks.BEDROCK) && state.isFullCube(world, pos) && carriedState.canPlaceAt(world, posAbove) && world.getOtherEntities(this.enderman, Box.from(Vec3d.of(posAbove))).isEmpty();
    }
}
