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
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
static final class MultipartBlockStateModel.MultipartBakedModel {
    private final List<MultipartBlockStateModel.Selector<BlockStateModel>> selectors;
    final Sprite particleSprite;
    private final Map<BitSet, List<BlockStateModel>> map = new ConcurrentHashMap<BitSet, List<BlockStateModel>>();

    private static BlockStateModel getFirst(List<MultipartBlockStateModel.Selector<BlockStateModel>> selectors) {
        if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
        }
        return selectors.getFirst().model();
    }

    public MultipartBlockStateModel.MultipartBakedModel(List<MultipartBlockStateModel.Selector<BlockStateModel>> selectors) {
        this.selectors = selectors;
        BlockStateModel blockStateModel = MultipartBlockStateModel.MultipartBakedModel.getFirst(selectors);
        this.particleSprite = blockStateModel.particleSprite();
    }

    public List<BlockStateModel> build(BlockState state) {
        BitSet bitSet2 = new BitSet();
        for (int i = 0; i < this.selectors.size(); ++i) {
            if (!this.selectors.get((int)i).condition.test(state)) continue;
            bitSet2.set(i);
        }
        return this.map.computeIfAbsent(bitSet2, bitSet -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (int i = 0; i < this.selectors.size(); ++i) {
                if (!bitSet.get(i)) continue;
                builder.add((Object)((BlockStateModel)this.selectors.get((int)i).model));
            }
            return builder.build();
        });
    }
}
