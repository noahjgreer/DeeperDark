/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;

@Environment(value=EnvType.CLIENT)
static class NeedleAngleState.2
implements NeedleAngleState.Angler {
    private float angle;

    NeedleAngleState.2() {
    }

    @Override
    public float getAngle() {
        return this.angle;
    }

    @Override
    public boolean shouldUpdate(long time) {
        return true;
    }

    @Override
    public void update(long time, float target) {
        this.angle = target;
    }
}
