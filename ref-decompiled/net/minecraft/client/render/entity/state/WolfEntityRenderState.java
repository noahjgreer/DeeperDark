/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WolfEntityRenderState
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderState
extends LivingEntityRenderState {
    private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla((String)"textures/entity/wolf/wolf.png");
    public boolean angerTime;
    public boolean inSittingPose;
    public float tailAngle = 0.62831855f;
    public float begAnimationProgress;
    public float shakeProgress;
    public float furWetBrightnessMultiplier = 1.0f;
    public Identifier texture = DEFAULT_TEXTURE;
    public @Nullable DyeColor collarColor;
    public ItemStack bodyArmor = ItemStack.EMPTY;

    public float getRoll(float shakeOffset) {
        float f = (this.shakeProgress + shakeOffset) / 1.8f;
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        return MathHelper.sin((double)(f * (float)Math.PI)) * MathHelper.sin((double)(f * (float)Math.PI * 11.0f)) * 0.15f * (float)Math.PI;
    }
}

