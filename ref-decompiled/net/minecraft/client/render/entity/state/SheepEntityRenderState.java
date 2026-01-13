/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SheepEntityRenderState
 *  net.minecraft.client.util.ColorLerper
 *  net.minecraft.client.util.ColorLerper$Type
 *  net.minecraft.util.DyeColor
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public class SheepEntityRenderState
extends LivingEntityRenderState {
    public float neckAngle;
    public float headAngle;
    public boolean sheared;
    public DyeColor color = DyeColor.WHITE;
    public boolean rainbow;

    public int getRgbColor() {
        if (this.rainbow) {
            return ColorLerper.lerpColor((ColorLerper.Type)ColorLerper.Type.SHEEP, (float)this.age);
        }
        return ColorLerper.Type.SHEEP.getArgb(this.color);
    }
}

