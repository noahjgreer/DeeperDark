/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public static class BlockPos.Mutable
extends BlockPos {
    public BlockPos.Mutable() {
        this(0, 0, 0);
    }

    public BlockPos.Mutable(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPos.Mutable(double x, double y, double z) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    @Override
    public BlockPos add(int i, int j, int k) {
        return super.add(i, j, k).toImmutable();
    }

    @Override
    public BlockPos multiply(int i) {
        return super.multiply(i).toImmutable();
    }

    @Override
    public BlockPos offset(Direction direction, int i) {
        return super.offset(direction, i).toImmutable();
    }

    @Override
    public BlockPos offset(Direction.Axis axis, int i) {
        return super.offset(axis, i).toImmutable();
    }

    @Override
    public BlockPos rotate(BlockRotation rotation) {
        return super.rotate(rotation).toImmutable();
    }

    public BlockPos.Mutable set(int x, int y, int z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        return this;
    }

    public BlockPos.Mutable set(double x, double y, double z) {
        return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    public BlockPos.Mutable set(Vec3i pos) {
        return this.set(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos.Mutable set(long pos) {
        return this.set(BlockPos.Mutable.unpackLongX(pos), BlockPos.Mutable.unpackLongY(pos), BlockPos.Mutable.unpackLongZ(pos));
    }

    public BlockPos.Mutable set(AxisCycleDirection axis, int x, int y, int z) {
        return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
    }

    public BlockPos.Mutable set(Vec3i pos, Direction direction) {
        return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
    }

    public BlockPos.Mutable set(Vec3i pos, int x, int y, int z) {
        return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    public BlockPos.Mutable set(Vec3i vec1, Vec3i vec2) {
        return this.set(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
    }

    public BlockPos.Mutable move(Direction direction) {
        return this.move(direction, 1);
    }

    public BlockPos.Mutable move(Direction direction, int distance) {
        return this.set(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
    }

    public BlockPos.Mutable move(int dx, int dy, int dz) {
        return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
    }

    public BlockPos.Mutable move(Vec3i vec) {
        return this.set(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    public BlockPos.Mutable clamp(Direction.Axis axis, int min, int max) {
        return switch (axis) {
            default -> throw new MatchException(null, null);
            case Direction.Axis.X -> this.set(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
            case Direction.Axis.Y -> this.set(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
            case Direction.Axis.Z -> this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
        };
    }

    @Override
    public BlockPos.Mutable setX(int i) {
        super.setX(i);
        return this;
    }

    @Override
    public BlockPos.Mutable setY(int i) {
        super.setY(i);
        return this;
    }

    @Override
    public BlockPos.Mutable setZ(int i) {
        super.setZ(i);
        return this;
    }

    @Override
    public BlockPos toImmutable() {
        return new BlockPos(this);
    }

    @Override
    public /* synthetic */ Vec3i crossProduct(Vec3i vec) {
        return super.crossProduct(vec);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction.Axis axis, int distance) {
        return this.offset(axis, distance);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction direction, int distance) {
        return this.offset(direction, distance);
    }

    @Override
    public /* synthetic */ Vec3i offset(Direction direction) {
        return super.offset(direction);
    }

    @Override
    public /* synthetic */ Vec3i east(int distance) {
        return super.east(distance);
    }

    @Override
    public /* synthetic */ Vec3i east() {
        return super.east();
    }

    @Override
    public /* synthetic */ Vec3i west(int distance) {
        return super.west(distance);
    }

    @Override
    public /* synthetic */ Vec3i west() {
        return super.west();
    }

    @Override
    public /* synthetic */ Vec3i south(int distance) {
        return super.south(distance);
    }

    @Override
    public /* synthetic */ Vec3i south() {
        return super.south();
    }

    @Override
    public /* synthetic */ Vec3i north(int distance) {
        return super.north(distance);
    }

    @Override
    public /* synthetic */ Vec3i north() {
        return super.north();
    }

    @Override
    public /* synthetic */ Vec3i down(int distance) {
        return super.down(distance);
    }

    @Override
    public /* synthetic */ Vec3i down() {
        return super.down();
    }

    @Override
    public /* synthetic */ Vec3i up(int distance) {
        return super.up(distance);
    }

    @Override
    public /* synthetic */ Vec3i up() {
        return super.up();
    }

    @Override
    public /* synthetic */ Vec3i multiply(int scale) {
        return this.multiply(scale);
    }

    @Override
    public /* synthetic */ Vec3i subtract(Vec3i vec) {
        return super.subtract(vec);
    }

    @Override
    public /* synthetic */ Vec3i add(Vec3i vec) {
        return super.add(vec);
    }

    @Override
    public /* synthetic */ Vec3i add(int x, int y, int z) {
        return this.add(x, y, z);
    }

    @Override
    public /* synthetic */ Vec3i setZ(int z) {
        return this.setZ(z);
    }

    @Override
    public /* synthetic */ Vec3i setY(int y) {
        return this.setY(y);
    }

    @Override
    public /* synthetic */ Vec3i setX(int x) {
        return this.setX(x);
    }
}
