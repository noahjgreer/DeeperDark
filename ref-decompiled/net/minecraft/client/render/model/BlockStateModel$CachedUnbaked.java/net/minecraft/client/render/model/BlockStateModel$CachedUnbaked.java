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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public static class BlockStateModel.CachedUnbaked
implements BlockStateModel.UnbakedGrouped {
    final BlockStateModel.Unbaked delegate;
    private final Baker.ResolvableCacheKey<BlockStateModel> cacheKey = new Baker.ResolvableCacheKey<BlockStateModel>(){

        @Override
        public BlockStateModel compute(Baker baker) {
            return delegate.bake(baker);
        }

        @Override
        public /* synthetic */ Object compute(Baker baker) {
            return this.compute(baker);
        }
    };

    public BlockStateModel.CachedUnbaked(BlockStateModel.Unbaked delegate) {
        this.delegate = delegate;
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.delegate.resolve(resolver);
    }

    @Override
    public BlockStateModel bake(BlockState state, Baker baker) {
        return baker.compute(this.cacheKey);
    }

    @Override
    public Object getEqualityGroup(BlockState state) {
        return this;
    }
}
