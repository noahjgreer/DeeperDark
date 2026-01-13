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
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class ModelBaker.BlockItemModels.1
implements Baker {
    final /* synthetic */ ErrorCollectingSpriteGetter field_56987;
    final /* synthetic */ Baker.Vec3fInterner field_64596;

    ModelBaker.BlockItemModels.1() {
        this.field_56987 = errorCollectingSpriteGetter;
        this.field_64596 = vec3fInterner;
    }

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
        return this.field_56987;
    }

    @Override
    public Baker.Vec3fInterner getVec3fInterner() {
        return this.field_64596;
    }
}
