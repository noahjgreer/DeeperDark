/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LancerEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LancerEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ZombifiedPiglinEntityRenderState
extends LancerEntityRenderState {
    public boolean attacking;
}

