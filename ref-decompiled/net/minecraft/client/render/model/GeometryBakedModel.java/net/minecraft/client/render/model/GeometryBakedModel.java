/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record GeometryBakedModel(BakedGeometry quads, boolean useAmbientOcclusion, Sprite particleSprite) implements BlockModelPart
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static BlockModelPart create(Baker baker, Identifier id, ModelBakeSettings bakeSettings) {
        BakedSimpleModel bakedSimpleModel = baker.getModel(id);
        ModelTextures modelTextures = bakedSimpleModel.getTextures();
        boolean bl = bakedSimpleModel.getAmbientOcclusion();
        Sprite sprite = bakedSimpleModel.getParticleTexture(modelTextures, baker);
        BakedGeometry bakedGeometry = bakedSimpleModel.bakeGeometry(modelTextures, baker, bakeSettings);
        HashMultimap multimap = null;
        for (BakedQuad bakedQuad : bakedGeometry.getAllQuads()) {
            Sprite sprite2 = bakedQuad.sprite();
            if (sprite2.getAtlasId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) continue;
            if (multimap == null) {
                multimap = HashMultimap.create();
            }
            multimap.put((Object)sprite2.getAtlasId(), (Object)sprite2.getContents().getId());
        }
        if (multimap != null) {
            LOGGER.warn("Rejecting block model {}, since it contains sprites from outside of supported atlas: {}", (Object)id, multimap);
            return baker.getBlockPart();
        }
        return new GeometryBakedModel(bakedGeometry, bl, sprite);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side) {
        return this.quads.getQuads(side);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GeometryBakedModel.class, "quads;useAmbientOcclusion;particleIcon", "quads", "useAmbientOcclusion", "particleSprite"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GeometryBakedModel.class, "quads;useAmbientOcclusion;particleIcon", "quads", "useAmbientOcclusion", "particleSprite"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GeometryBakedModel.class, "quads;useAmbientOcclusion;particleIcon", "quads", "useAmbientOcclusion", "particleSprite"}, this, object);
    }
}
