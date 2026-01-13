/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;

@Environment(value=EnvType.CLIENT)
class MultipartBlockStateModel.MultipartUnbaked.1
implements Baker.ResolvableCacheKey<MultipartBlockStateModel.MultipartBakedModel> {
    MultipartBlockStateModel.MultipartUnbaked.1() {
    }

    @Override
    public MultipartBlockStateModel.MultipartBakedModel compute(Baker baker) {
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)MultipartUnbaked.this.selectors.size());
        for (MultipartBlockStateModel.Selector<BlockStateModel.Unbaked> selector : MultipartUnbaked.this.selectors) {
            builder.add(selector.build(((BlockStateModel.Unbaked)selector.model).bake(baker)));
        }
        return new MultipartBlockStateModel.MultipartBakedModel((List<MultipartBlockStateModel.Selector<BlockStateModel>>)builder.build());
    }

    @Override
    public /* synthetic */ Object compute(Baker baker) {
        return this.compute(baker);
    }
}
