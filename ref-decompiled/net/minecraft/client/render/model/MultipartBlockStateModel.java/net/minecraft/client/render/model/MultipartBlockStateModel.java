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
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultipartBlockStateModel
implements BlockStateModel {
    private final MultipartBakedModel bakedModels;
    private final BlockState state;
    private @Nullable List<BlockStateModel> models;

    MultipartBlockStateModel(MultipartBakedModel bakedModels, BlockState state) {
        this.bakedModels = bakedModels;
        this.state = state;
    }

    @Override
    public Sprite particleSprite() {
        return this.bakedModels.particleSprite;
    }

    @Override
    public void addParts(Random random, List<BlockModelPart> parts) {
        if (this.models == null) {
            this.models = this.bakedModels.build(this.state);
        }
        long l = random.nextLong();
        for (BlockStateModel blockStateModel : this.models) {
            random.setSeed(l);
            blockStateModel.addParts(random, parts);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class MultipartBakedModel {
        private final List<Selector<BlockStateModel>> selectors;
        final Sprite particleSprite;
        private final Map<BitSet, List<BlockStateModel>> map = new ConcurrentHashMap<BitSet, List<BlockStateModel>>();

        private static BlockStateModel getFirst(List<Selector<BlockStateModel>> selectors) {
            if (selectors.isEmpty()) {
                throw new IllegalArgumentException("Model must have at least one selector");
            }
            return selectors.getFirst().model();
        }

        public MultipartBakedModel(List<Selector<BlockStateModel>> selectors) {
            this.selectors = selectors;
            BlockStateModel blockStateModel = MultipartBakedModel.getFirst(selectors);
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

    @Environment(value=EnvType.CLIENT)
    public static class MultipartUnbaked
    implements BlockStateModel.UnbakedGrouped {
        final List<Selector<BlockStateModel.Unbaked>> selectors;
        private final Baker.ResolvableCacheKey<MultipartBakedModel> bakerCache = new Baker.ResolvableCacheKey<MultipartBakedModel>(){

            @Override
            public MultipartBakedModel compute(Baker baker) {
                ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)selectors.size());
                for (Selector<BlockStateModel.Unbaked> selector : selectors) {
                    builder.add(selector.build(((BlockStateModel.Unbaked)selector.model).bake(baker)));
                }
                return new MultipartBakedModel((List<Selector<BlockStateModel>>)builder.build());
            }

            @Override
            public /* synthetic */ Object compute(Baker baker) {
                return this.compute(baker);
            }
        };

        public MultipartUnbaked(List<Selector<BlockStateModel.Unbaked>> selectors) {
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
            record EqualityGroup(MultipartUnbaked model, IntList selectors) {
            }
            return new EqualityGroup(this, (IntList)intList);
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.selectors.forEach(selector -> ((BlockStateModel.Unbaked)selector.model).resolve(resolver));
        }

        @Override
        public BlockStateModel bake(BlockState state, Baker baker) {
            MultipartBakedModel multipartBakedModel = baker.compute(this.bakerCache);
            return new MultipartBlockStateModel(multipartBakedModel, state);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Selector<T>
    extends Record {
        final Predicate<BlockState> condition;
        final T model;

        public Selector(Predicate<BlockState> condition, T model) {
            this.condition = condition;
            this.model = model;
        }

        public <S> Selector<S> build(S model) {
            return new Selector<S>(this.condition, model);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Selector.class, "condition;model", "condition", "model"}, this, object);
        }

        public Predicate<BlockState> condition() {
            return this.condition;
        }

        public T model() {
            return this.model;
        }
    }
}
