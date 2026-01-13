/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class ModelBaker.BakerImpl
implements Baker {
    private final ErrorCollectingSpriteGetter spriteGetter;
    private final Baker.Vec3fInterner interner;
    private final ModelBaker.BlockItemModels blockItemModels;
    private final Map<Baker.ResolvableCacheKey<Object>, Object> cache = new ConcurrentHashMap<Baker.ResolvableCacheKey<Object>, Object>();
    private final Function<Baker.ResolvableCacheKey<Object>, Object> cacheValueFunction = key -> key.compute(this);

    ModelBaker.BakerImpl(ErrorCollectingSpriteGetter spriteGetter, Baker.Vec3fInterner interner, ModelBaker.BlockItemModels blockItemModels) {
        this.spriteGetter = spriteGetter;
        this.interner = interner;
        this.blockItemModels = blockItemModels;
    }

    @Override
    public BlockModelPart getBlockPart() {
        return this.blockItemModels.blockPart;
    }

    @Override
    public ErrorCollectingSpriteGetter getSpriteGetter() {
        return this.spriteGetter;
    }

    @Override
    public Baker.Vec3fInterner getVec3fInterner() {
        return this.interner;
    }

    @Override
    public BakedSimpleModel getModel(Identifier id) {
        BakedSimpleModel bakedSimpleModel = ModelBaker.this.simpleModels.get(id);
        if (bakedSimpleModel == null) {
            LOGGER.warn("Requested a model that was not discovered previously: {}", (Object)id);
            return ModelBaker.this.missingModel;
        }
        return bakedSimpleModel;
    }

    @Override
    public <T> T compute(Baker.ResolvableCacheKey<T> key) {
        return (T)this.cache.computeIfAbsent(key, this.cacheValueFunction);
    }
}
