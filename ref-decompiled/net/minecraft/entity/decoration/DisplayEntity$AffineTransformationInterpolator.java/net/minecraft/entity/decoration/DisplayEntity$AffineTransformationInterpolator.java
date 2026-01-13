/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;

record DisplayEntity.AffineTransformationInterpolator(AffineTransformation previous, AffineTransformation current) implements DisplayEntity.AbstractInterpolator<AffineTransformation>
{
    @Override
    public AffineTransformation interpolate(float f) {
        if ((double)f >= 1.0) {
            return this.current;
        }
        return this.previous.interpolate(this.current, f);
    }

    @Override
    public /* synthetic */ Object interpolate(float delta) {
        return this.interpolate(delta);
    }
}
