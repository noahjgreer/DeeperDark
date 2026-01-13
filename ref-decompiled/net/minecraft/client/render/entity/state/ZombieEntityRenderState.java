/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LancerEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LancerEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ZombieEntityRenderState
extends LancerEntityRenderState {
    public boolean attacking;
    public boolean convertingInWater;
}

