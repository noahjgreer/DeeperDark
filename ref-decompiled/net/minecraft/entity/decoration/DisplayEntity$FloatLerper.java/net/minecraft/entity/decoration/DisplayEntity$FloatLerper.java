/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

@FunctionalInterface
public static interface DisplayEntity.FloatLerper {
    public static DisplayEntity.FloatLerper constant(float value) {
        return delta -> value;
    }

    public float lerp(float var1);
}
