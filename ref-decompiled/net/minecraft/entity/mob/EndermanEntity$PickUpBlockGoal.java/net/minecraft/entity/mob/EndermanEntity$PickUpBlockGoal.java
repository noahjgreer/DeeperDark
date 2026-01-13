/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;

static class EndermanEntity.PickUpBlockGoal
extends Goal {
    private final EndermanEntity enderman;

    public EndermanEntity.PickUpBlockGoal(EndermanEntity enderman) {
        this.enderman = enderman;
    }

    @Override
    public boolean canStart() {
        if (this.enderman.getCarriedBlock() != null) {
            return false;
        }
        if (!EndermanEntity.PickUpBlockGoal.getServerWorld(this.enderman).getGameRules().getValue(GameRules.DO_MOB_GRIEFING).booleanValue()) {
            return false;
        }
        return this.enderman.getRandom().nextInt(EndermanEntity.PickUpBlockGoal.toGoalTicks(20)) == 0;
    }

    @Override
    public void tick() {
        Random random = this.enderman.getRandom();
        World world = this.enderman.getEntityWorld();
        int i = MathHelper.floor(this.enderman.getX() - 2.0 + random.nextDouble() * 4.0);
        int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0);
        int k = MathHelper.floor(this.enderman.getZ() - 2.0 + random.nextDouble() * 4.0);
        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = world.getBlockState(blockPos);
        Vec3d vec3d = new Vec3d((double)this.enderman.getBlockX() + 0.5, (double)j + 0.5, (double)this.enderman.getBlockZ() + 0.5);
        Vec3d vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.enderman));
        boolean bl = blockHitResult.getBlockPos().equals(blockPos);
        if (blockState.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl) {
            world.removeBlock(blockPos, false);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Emitter.of(this.enderman, blockState));
            this.enderman.setCarriedBlock(blockState.getBlock().getDefaultState());
        }
    }
}
