/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

@FunctionalInterface
public static interface DisplayEntity.IntLerper {
    public static DisplayEntity.IntLerper constant(int value) {
        return delta -> value;
    }

    public int lerp(float var1);
}
