/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityRenderState
extends BlockEntityRenderState {
    public DyeColor dyeColor;
    public BannerPatternsComponent bannerPatterns;
    public float pitch;
    public float yaw;
    public boolean standing;
}
