/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.DonkeyEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class DonkeyEntityRenderState
extends LivingHorseEntityRenderState {
    public boolean hasChest;
}

