/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.ChickenVariant;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderState
extends LivingEntityRenderState {
    public float flapProgress;
    public float maxWingDeviation;
    public @Nullable ChickenVariant variant;
}
