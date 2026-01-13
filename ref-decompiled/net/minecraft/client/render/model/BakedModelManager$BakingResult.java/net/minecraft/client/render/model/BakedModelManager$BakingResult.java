/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ModelBaker;

@Environment(value=EnvType.CLIENT)
static final class BakedModelManager.BakingResult
extends Record {
    final ModelBaker.BakedModels bakedModels;
    final Object2IntMap<BlockState> modelGroups;
    final Map<BlockState, BlockStateModel> modelCache;
    final LoadedEntityModels entityModelSet;
    final LoadedBlockEntityModels specialBlockModelRenderer;

    BakedModelManager.BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BlockStateModel> modelCache, LoadedEntityModels entityModelSet, LoadedBlockEntityModels specialBlockModelRenderer) {
        this.bakedModels = bakedModels;
        this.modelGroups = modelGroups;
        this.modelCache = modelCache;
        this.entityModelSet = entityModelSet;
        this.specialBlockModelRenderer = specialBlockModelRenderer;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedModelManager.BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedModelManager.BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedModelManager.BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this, object);
    }

    public ModelBaker.BakedModels bakedModels() {
        return this.bakedModels;
    }

    public Object2IntMap<BlockState> modelGroups() {
        return this.modelGroups;
    }

    public Map<BlockState, BlockStateModel> modelCache() {
        return this.modelCache;
    }

    public LoadedEntityModels entityModelSet() {
        return this.entityModelSet;
    }

    public LoadedBlockEntityModels specialBlockModelRenderer() {
        return this.specialBlockModelRenderer;
    }
}
