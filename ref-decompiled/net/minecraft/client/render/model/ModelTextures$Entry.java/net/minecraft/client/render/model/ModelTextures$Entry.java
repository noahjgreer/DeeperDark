/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;

@Environment(value=EnvType.CLIENT)
public static sealed interface ModelTextures.Entry
permits ModelTextures.SpriteEntry, ModelTextures.TextureReferenceEntry {
}
