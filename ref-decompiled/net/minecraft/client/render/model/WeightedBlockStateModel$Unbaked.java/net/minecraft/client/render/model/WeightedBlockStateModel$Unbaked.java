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
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.util.collection.Pool;

@Environment(value=EnvType.CLIENT)
public record WeightedBlockStateModel.Unbaked(Pool<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked
{
    @Override
    public BlockStateModel bake(Baker baker) {
        return new WeightedBlockStateModel(this.entries.transform(model -> model.bake(baker)));
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.entries.getEntries().forEach(entry -> ((BlockStateModel.Unbaked)entry.value()).resolve(resolver));
    }
}
