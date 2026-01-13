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
import net.minecraft.client.util.SpriteIdentifier;

@Environment(value=EnvType.CLIENT)
record ModelTextures.SpriteEntry(SpriteIdentifier material) implements ModelTextures.Entry
{
}
