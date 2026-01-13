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

@Environment(value=EnvType.CLIENT)
public static interface NeedleAngleState.Angler {
    public float getAngle();

    public boolean shouldUpdate(long var1);

    public void update(long var1, float var3);
}
