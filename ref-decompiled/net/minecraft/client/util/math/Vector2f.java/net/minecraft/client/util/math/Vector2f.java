/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.math;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record Vector2f(float x, float y) {
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public static long toLong(float x, float y) {
        long l = (long)Float.floatToIntBits(x) & 0xFFFFFFFFL;
        long m = (long)Float.floatToIntBits(y) & 0xFFFFFFFFL;
        return l << 32 | m;
    }

    public static float getX(long x) {
        int i = (int)(x >> 32);
        return Float.intBitsToFloat(i);
    }

    public static float getY(long y) {
        return Float.intBitsToFloat((int)y);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Vector2f.class, "u;v", "x", "y"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Vector2f.class, "u;v", "x", "y"}, this, object);
    }
}
