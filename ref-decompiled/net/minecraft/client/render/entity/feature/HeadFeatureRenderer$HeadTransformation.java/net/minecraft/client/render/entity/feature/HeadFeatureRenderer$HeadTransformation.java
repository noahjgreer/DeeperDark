/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record HeadFeatureRenderer.HeadTransformation(float yOffset, float skullYOffset, float horizontalScale) {
    public static final HeadFeatureRenderer.HeadTransformation DEFAULT = new HeadFeatureRenderer.HeadTransformation(0.0f, 0.0f, 1.0f);
}
