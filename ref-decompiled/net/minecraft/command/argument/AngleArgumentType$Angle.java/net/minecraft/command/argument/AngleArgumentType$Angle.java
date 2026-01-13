/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.MathHelper;

public static final class AngleArgumentType.Angle {
    private final float angle;
    private final boolean relative;

    AngleArgumentType.Angle(float angle, boolean relative) {
        this.angle = angle;
        this.relative = relative;
    }

    public float getAngle(ServerCommandSource source) {
        return MathHelper.wrapDegrees(this.relative ? this.angle + source.getRotation().y : this.angle);
    }
}
