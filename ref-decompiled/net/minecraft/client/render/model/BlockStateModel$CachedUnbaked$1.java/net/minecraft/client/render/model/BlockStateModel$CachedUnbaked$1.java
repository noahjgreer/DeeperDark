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

@Environment(value=EnvType.CLIENT)
class BlockStateModel.CachedUnbaked.1
implements Baker.ResolvableCacheKey<BlockStateModel> {
    BlockStateModel.CachedUnbaked.1() {
    }

    @Override
    public BlockStateModel compute(Baker baker) {
        return CachedUnbaked.this.delegate.bake(baker);
    }

    @Override
    public /* synthetic */ Object compute(Baker baker) {
        return this.compute(baker);
    }
}
