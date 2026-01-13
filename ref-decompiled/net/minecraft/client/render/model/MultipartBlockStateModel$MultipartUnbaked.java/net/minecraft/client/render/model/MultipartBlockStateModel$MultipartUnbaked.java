/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public static class MultipartBlockStateModel.MultipartUnbaked
implements BlockStateModel.UnbakedGrouped {
    final List<MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>> selectors;
    private final Baker.ResolvableCacheKey<MultipartBlockStateModel.MultipartBakedModel> bakerCache = new Baker.ResolvableCacheKey<MultipartBlockStateModel.MultipartBakedModel>(){

        @Override
        public MultipartBlockStateModel.MultipartBakedModel compute(Baker baker) {
            ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)selectors.size());
            for (MultipartBlockStateModel.Selector<BlockStateModel.Unbaked> selector : selectors) {
                builder.add(selector.build(((BlockStateModel.Unbaked)selector.model).bake(baker)));
            }
            return new MultipartBlockStateModel.MultipartBakedModel((List<MultipartBlockStateModel.Selector<BlockStateModel>>)builder.build());
        }

        @Override
        public /* synthetic */ Object compute(Baker baker) {
            return this.compute(baker);
        }
    };

    public MultipartBlockStateModel.MultipartUnbaked(List<MultipartBlockStateModel.Selector<BlockStateModel.Unbaked>> selectors) {
        this.selectors = selectors;
    }

    @Override
    public Object getEqualityGroup(BlockState state) {
        IntArrayList intList = new IntArrayList();
        for (int i = 0; i < this.selectors.size(); ++i) {
            if (!this.selectors.get((int)i).condition.test(state)) continue;
            intList.add(i);
        }
        @Environment(value=EnvType.CLIENT)
        record EqualityGroup(MultipartBlockStateModel.MultipartUnbaked model, IntList selectors) {
        }
        return new EqualityGroup(this, (IntList)intList);
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.selectors.forEach(selector -> ((BlockStateModel.Unbaked)selector.model).resolve(resolver));
    }

    @Override
    public BlockStateModel bake(BlockState state, Baker baker) {
        MultipartBlockStateModel.MultipartBakedModel multipartBakedModel = baker.compute(this.bakerCache);
        return new MultipartBlockStateModel(multipartBakedModel, state);
    }
}
