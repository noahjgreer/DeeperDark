/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;

@Environment(value=EnvType.CLIENT)
public static interface AtlasSource.SpriteRegion
extends AtlasSource.SpriteSource {
    default public void close() {
    }
}
