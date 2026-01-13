/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.InteractibleSlotContainer
 *  net.minecraft.block.InteractibleSlotContainer$1
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.block.InteractibleSlotContainer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

/*
 * Exception performing whole class analysis ignored.
 */
public interface InteractibleSlotContainer {
    public int getRows();

    public int getColumns();

    default public OptionalInt getHitSlot(BlockHitResult hitResult, Direction facing) {
        return InteractibleSlotContainer.getHitPosOnFront((BlockHitResult)hitResult, (Direction)facing).map(vec2f -> {
            int i = InteractibleSlotContainer.getSlotAlongAxis((float)(1.0f - vec2f.y), (int)this.getRows());
            int j = InteractibleSlotContainer.getSlotAlongAxis((float)vec2f.x, (int)this.getColumns());
            return OptionalInt.of(j + i * this.getColumns());
        }).orElseGet(OptionalInt::empty);
    }

    private static Optional<Vec2f> getHitPosOnFront(BlockHitResult hitResult, Direction facing) {
        Direction direction = hitResult.getSide();
        if (facing != direction) {
            return Optional.empty();
        }
        BlockPos blockPos = hitResult.getBlockPos().offset(direction);
        Vec3d vec3d = hitResult.getPos().subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        return switch (1.field_61422[direction.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> Optional.of(new Vec2f((float)(1.0 - d), (float)e));
            case 2 -> Optional.of(new Vec2f((float)d, (float)e));
            case 3 -> Optional.of(new Vec2f((float)f, (float)e));
            case 4 -> Optional.of(new Vec2f((float)(1.0 - f), (float)e));
            case 5, 6 -> Optional.empty();
        };
    }

    private static int getSlotAlongAxis(float pos, int max) {
        float f = pos * 16.0f;
        float g = 16.0f / (float)max;
        return MathHelper.clamp((int)MathHelper.floor((float)(f / g)), (int)0, (int)(max - 1));
    }
}

