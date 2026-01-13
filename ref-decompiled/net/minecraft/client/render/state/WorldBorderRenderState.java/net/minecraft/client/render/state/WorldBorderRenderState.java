/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 */
package net.minecraft.client.render.state;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class WorldBorderRenderState
implements FabricRenderState {
    public double minX;
    public double maxX;
    public double minZ;
    public double maxZ;
    public int tint;
    public double alpha;

    public List<Distance> nearestBorder(double x, double z) {
        Distance[] distances = new Distance[]{new Distance(Direction.NORTH, z - this.minZ), new Distance(Direction.SOUTH, this.maxZ - z), new Distance(Direction.WEST, x - this.minX), new Distance(Direction.EAST, this.maxX - x)};
        return Arrays.stream(distances).sorted(Comparator.comparingDouble(d -> d.value)).toList();
    }

    public void clear() {
        this.alpha = 0.0;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Distance
    extends Record {
        private final Direction direction;
        final double value;

        public Distance(Direction direction, double value) {
            this.direction = direction;
            this.value = value;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Distance.class, "direction;distance", "direction", "value"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Distance.class, "direction;distance", "direction", "value"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Distance.class, "direction;distance", "direction", "value"}, this, object);
        }

        public Direction direction() {
            return this.direction;
        }

        public double value() {
            return this.value;
        }
    }
}
