/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.function.BooleanSupplier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jspecify.annotations.Nullable;

public static class GhastEntity.GhastMoveControl
extends MoveControl {
    private final MobEntity ghast;
    private int collisionCheckCooldown;
    private final boolean happy;
    private final BooleanSupplier shouldStayStill;

    public GhastEntity.GhastMoveControl(MobEntity ghast, boolean happy, BooleanSupplier shouldStayStill) {
        super(ghast);
        this.ghast = ghast;
        this.happy = happy;
        this.shouldStayStill = shouldStayStill;
    }

    @Override
    public void tick() {
        if (this.shouldStayStill.getAsBoolean()) {
            this.state = MoveControl.State.WAIT;
            this.ghast.stopMovement();
        }
        if (this.state != MoveControl.State.MOVE_TO) {
            return;
        }
        if (this.collisionCheckCooldown-- <= 0) {
            this.collisionCheckCooldown += this.ghast.getRandom().nextInt(5) + 2;
            Vec3d vec3d = new Vec3d(this.targetX - this.ghast.getX(), this.targetY - this.ghast.getY(), this.targetZ - this.ghast.getZ());
            if (this.willCollide(vec3d)) {
                this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d.normalize().multiply(this.ghast.getAttributeValue(EntityAttributes.FLYING_SPEED) * 5.0 / 3.0)));
            } else {
                this.state = MoveControl.State.WAIT;
            }
        }
    }

    private boolean willCollide(Vec3d movement) {
        Box box = this.ghast.getBoundingBox();
        Box box2 = box.offset(movement);
        if (this.happy) {
            for (BlockPos blockPos : BlockPos.iterate(box2.expand(1.0))) {
                if (this.canPassThrough(this.ghast.getEntityWorld(), null, null, blockPos, false, false)) continue;
                return false;
            }
        }
        boolean bl = this.ghast.isTouchingWater();
        boolean bl2 = this.ghast.isInLava();
        Vec3d vec3d = this.ghast.getEntityPos();
        Vec3d vec3d2 = vec3d.add(movement);
        return BlockView.collectCollisionsBetween(vec3d, vec3d2, box2, (pos, version) -> {
            if (box.contains(pos)) {
                return true;
            }
            return this.canPassThrough(this.ghast.getEntityWorld(), vec3d, vec3d2, pos, bl, bl2);
        });
    }

    private boolean canPassThrough(BlockView world, @Nullable Vec3d oldPos, @Nullable Vec3d newPos, BlockPos blockPos, boolean waterAllowed, boolean lavaAllowed) {
        boolean bl2;
        boolean bl;
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isAir()) {
            return true;
        }
        boolean bl3 = bl = oldPos != null && newPos != null;
        boolean bl4 = bl ? !this.ghast.collides(oldPos, newPos, blockState.getCollisionShape(world, blockPos).offset(new Vec3d(blockPos)).getBoundingBoxes()) : (bl2 = blockState.getCollisionShape(world, blockPos).isEmpty());
        if (!this.happy) {
            return bl2;
        }
        if (blockState.isIn(BlockTags.HAPPY_GHAST_AVOIDS)) {
            return false;
        }
        FluidState fluidState = world.getFluidState(blockPos);
        if (!(fluidState.isEmpty() || bl && !this.ghast.collidesWithFluid(fluidState, blockPos, oldPos, newPos))) {
            if (fluidState.isIn(FluidTags.WATER)) {
                return waterAllowed;
            }
            if (fluidState.isIn(FluidTags.LAVA)) {
                return lavaAllowed;
            }
        }
        return bl2;
    }
}
