/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedGeometry
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.render.model.BakedSimpleModel
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.GeometryBakedModel
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ModelTextures
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Direction
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
    private final BakedGeometry quads;
    private final boolean useAmbientOcclusion;
    private final Sprite particleSprite;
    private static final Logger LOGGER = LogUtils.getLogger();

    public GeometryBakedModel(BakedGeometry quads, boolean useAmbientOcclusion, Sprite particleSprite) {
        this.quads = quads;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.particleSprite = particleSprite;
    }

    public static BlockModelPart create(Baker baker, Identifier id, ModelBakeSettings bakeSettings) {
        BakedSimpleModel bakedSimpleModel = baker.getModel(id);
        ModelTextures modelTextures = bakedSimpleModel.getTextures();
        boolean bl = bakedSimpleModel.getAmbientOcclusion();
        Sprite sprite = bakedSimpleModel.getParticleTexture(modelTextures, baker);
        BakedGeometry bakedGeometry = bakedSimpleModel.bakeGeometry(modelTextures, baker, bakeSettings);
        HashMultimap multimap = null;
        for (BakedQuad bakedQuad : bakedGeometry.getAllQuads()) {
            Sprite sprite2 = bakedQuad.sprite();
            if (sprite2.getAtlasId().equals((Object)SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) continue;
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

    public BakedGeometry quads() {
        return this.quads;
    }

    public boolean useAmbientOcclusion() {
        return this.useAmbientOcclusion;
    }

    public Sprite particleSprite() {
        return this.particleSprite;
    }
}

