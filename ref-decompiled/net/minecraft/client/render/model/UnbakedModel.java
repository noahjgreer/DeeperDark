/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.Geometry
 *  net.minecraft.client.render.model.ModelTextures$Textures
 *  net.minecraft.client.render.model.UnbakedModel
 *  net.minecraft.client.render.model.json.ModelTransformation
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnbakedModel {
    public static final String PARTICLE_TEXTURE = "particle";

    default public @Nullable Boolean ambientOcclusion() {
        return null;
    }

    default public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable UnbakedModel.GuiLight guiLight() {
        return null;
    }

    default public @Nullable ModelTransformation transformations() {
        return null;
    }

    default public ModelTextures.Textures textures() {
        return ModelTextures.Textures.EMPTY;
    }

    default public @Nullable Geometry geometry() {
        return null;
    }

    default public @Nullable Identifier parent() {
        return null;
    }
}

