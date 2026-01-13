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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
static class NeedleAngleState.1
implements NeedleAngleState.Angler {
    private float angle;
    private float speed;
    private long lastUpdateTime;
    final /* synthetic */ float field_55402;

    NeedleAngleState.1(float f) {
        this.field_55402 = f;
    }

    @Override
    public float getAngle() {
        return this.angle;
    }

    @Override
    public boolean shouldUpdate(long time) {
        return this.lastUpdateTime != time;
    }

    @Override
    public void update(long time, float target) {
        this.lastUpdateTime = time;
        float f = MathHelper.floorMod(target - this.angle + 0.5f, 1.0f) - 0.5f;
        this.speed += f * 0.1f;
        this.speed *= this.field_55402;
        this.angle = MathHelper.floorMod(this.angle + this.speed, 1.0f);
    }
}
