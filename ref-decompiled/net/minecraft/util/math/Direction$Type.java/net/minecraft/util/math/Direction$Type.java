/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static final class Direction.Type
extends Enum<Direction.Type>
implements Iterable<Direction>,
Predicate<Direction> {
    public static final /* enum */ Direction.Type HORIZONTAL = new Direction.Type(new Direction[]{NORTH, EAST, SOUTH, WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z});
    public static final /* enum */ Direction.Type VERTICAL = new Direction.Type(new Direction[]{UP, DOWN}, new Direction.Axis[]{Direction.Axis.Y});
    private final Direction[] facingArray;
    private final Direction.Axis[] axisArray;
    private static final /* synthetic */ Direction.Type[] field_11063;

    public static Direction.Type[] values() {
        return (Direction.Type[])field_11063.clone();
    }

    public static Direction.Type valueOf(String string) {
        return Enum.valueOf(Direction.Type.class, string);
    }

    private Direction.Type(Direction[] facingArray, Direction.Axis[] axisArray) {
        this.facingArray = facingArray;
        this.axisArray = axisArray;
    }

    public Direction random(Random random) {
        return Util.getRandom(this.facingArray, random);
    }

    public Direction.Axis randomAxis(Random random) {
        return Util.getRandom(this.axisArray, random);
    }

    @Override
    public boolean test(@Nullable Direction direction) {
        return direction != null && direction.getAxis().getType() == this;
    }

    @Override
    public Iterator<Direction> iterator() {
        return Iterators.forArray((Object[])this.facingArray);
    }

    public Stream<Direction> stream() {
        return Arrays.stream(this.facingArray);
    }

    public List<Direction> getShuffled(Random random) {
        return Util.copyShuffled(this.facingArray, random);
    }

    public int getFacingCount() {
        return this.facingArray.length;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object direction) {
        return this.test((Direction)direction);
    }

    private static /* synthetic */ Direction.Type[] method_36934() {
        return new Direction.Type[]{HORIZONTAL, VERTICAL};
    }

    static {
        field_11063 = Direction.Type.method_36934();
    }
}
