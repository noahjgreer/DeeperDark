/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class ModelBaker.BlockItemModels
extends Record {
    final BlockModelPart blockPart;
    private final BlockStateModel block;
    final ItemModel item;

    public ModelBaker.BlockItemModels(BlockModelPart blockPart, BlockStateModel block, ItemModel item) {
        this.blockPart = blockPart;
        this.block = block;
        this.item = item;
    }

    public static ModelBaker.BlockItemModels bake(BakedSimpleModel model, final ErrorCollectingSpriteGetter errorCollectingSpriteGetter, final Baker.Vec3fInterner vec3fInterner) {
        Baker baker = new Baker(){

            @Override
            public BakedSimpleModel getModel(Identifier id) {
                throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(id));
            }

            @Override
            public BlockModelPart getBlockPart() {
                throw new IllegalStateException();
            }

            @Override
            public <T> T compute(Baker.ResolvableCacheKey<T> key) {
                return key.compute(this);
            }

            @Override
            public ErrorCollectingSpriteGetter getSpriteGetter() {
                return errorCollectingSpriteGetter;
            }

            @Override
            public Baker.Vec3fInterner getVec3fInterner() {
                return vec3fInterner;
            }
        };
        ModelTextures modelTextures = model.getTextures();
        boolean bl = model.getAmbientOcclusion();
        boolean bl2 = model.getGuiLight().isSide();
        ModelTransformation modelTransformation = model.getTransformations();
        BakedGeometry bakedGeometry = model.bakeGeometry(modelTextures, baker, ModelRotation.IDENTITY);
        Sprite sprite = model.getParticleTexture(modelTextures, baker);
        GeometryBakedModel geometryBakedModel = new GeometryBakedModel(bakedGeometry, bl, sprite);
        SimpleBlockStateModel blockStateModel = new SimpleBlockStateModel(geometryBakedModel);
        MissingItemModel itemModel = new MissingItemModel(bakedGeometry.getAllQuads(), new ModelSettings(bl2, sprite, modelTransformation));
        return new ModelBaker.BlockItemModels(geometryBakedModel, blockStateModel, itemModel);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelBaker.BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelBaker.BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelBaker.BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this, object);
    }

    public BlockModelPart blockPart() {
        return this.blockPart;
    }

    public BlockStateModel block() {
        return this.block;
    }

    public ItemModel item() {
        return this.item;
    }
}
