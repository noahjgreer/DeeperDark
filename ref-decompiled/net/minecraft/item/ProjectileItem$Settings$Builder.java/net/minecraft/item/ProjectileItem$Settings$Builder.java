/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.OptionalInt;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Vec3d;

public static class ProjectileItem.Settings.Builder {
    private ProjectileItem.PositionFunction positionFunction = (pointer, direction) -> DispenserBlock.getOutputLocation(pointer, 0.7, new Vec3d(0.0, 0.1, 0.0));
    private float uncertainty = 6.0f;
    private float power = 1.1f;
    private OptionalInt overrideDispenserEvent = OptionalInt.empty();

    public ProjectileItem.Settings.Builder positionFunction(ProjectileItem.PositionFunction positionFunction) {
        this.positionFunction = positionFunction;
        return this;
    }

    public ProjectileItem.Settings.Builder uncertainty(float uncertainty) {
        this.uncertainty = uncertainty;
        return this;
    }

    public ProjectileItem.Settings.Builder power(float power) {
        this.power = power;
        return this;
    }

    public ProjectileItem.Settings.Builder overrideDispenseEvent(int overrideDispenseEvent) {
        this.overrideDispenserEvent = OptionalInt.of(overrideDispenseEvent);
        return this;
    }

    public ProjectileItem.Settings build() {
        return new ProjectileItem.Settings(this.positionFunction, this.uncertainty, this.power, this.overrideDispenserEvent);
    }
}
