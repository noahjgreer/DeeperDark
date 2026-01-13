/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.MathHelper;

record DisplayEntity.IntLerperImpl(int previous, int current) implements DisplayEntity.IntLerper
{
    @Override
    public int lerp(float delta) {
        return MathHelper.lerp(delta, this.previous, this.current);
    }
}
