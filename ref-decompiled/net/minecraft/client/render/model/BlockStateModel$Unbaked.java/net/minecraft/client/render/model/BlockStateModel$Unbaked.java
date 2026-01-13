/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public static interface BlockStateModel.Unbaked
extends ResolvableModel {
    public static final Codec<Weighted<ModelVariant>> WEIGHTED_VARIANT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ModelVariant.MAP_CODEC.forGetter(Weighted::value), (App)Codecs.POSITIVE_INT.optionalFieldOf("weight", (Object)1).forGetter(Weighted::weight)).apply((Applicative)instance, Weighted::new));
    public static final Codec<WeightedBlockStateModel.Unbaked> WEIGHTED_CODEC = Codecs.nonEmptyList(WEIGHTED_VARIANT_CODEC.listOf()).flatComapMap(variants -> new WeightedBlockStateModel.Unbaked(Pool.of(Lists.transform((List)variants, weighted -> weighted.transform(SimpleBlockStateModel.Unbaked::new)))), unbaked -> {
        List<Weighted<BlockStateModel.Unbaked>> list = unbaked.entries().getEntries();
        ArrayList<Weighted<ModelVariant>> list2 = new ArrayList<Weighted<ModelVariant>>(list.size());
        for (Weighted<BlockStateModel.Unbaked> weighted : list) {
            BlockStateModel.Unbaked object = weighted.value();
            if (object instanceof SimpleBlockStateModel.Unbaked) {
                SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)object;
                list2.add(new Weighted<ModelVariant>(unbaked2.variant(), weighted.weight()));
                continue;
            }
            return DataResult.error(() -> "Only single variants are supported");
        }
        return DataResult.success(list2);
    });
    public static final Codec<BlockStateModel.Unbaked> CODEC = Codec.either(WEIGHTED_CODEC, SimpleBlockStateModel.Unbaked.CODEC).flatComapMap(either -> (BlockStateModel.Unbaked)either.map(left -> left, right -> right), variant -> {
        BlockStateModel.Unbaked unbaked = variant;
        Objects.requireNonNull(unbaked);
        BlockStateModel.Unbaked unbaked2 = unbaked;
        int i = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{SimpleBlockStateModel.Unbaked.class, WeightedBlockStateModel.Unbaked.class}, (Object)unbaked2, i)) {
            case 0 -> {
                SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)unbaked2;
                yield DataResult.success((Object)Either.right((Object)unbaked2));
            }
            case 1 -> {
                WeightedBlockStateModel.Unbaked unbaked3 = (WeightedBlockStateModel.Unbaked)unbaked2;
                yield DataResult.success((Object)Either.left((Object)unbaked3));
            }
            default -> DataResult.error(() -> "Only a single variant or a list of variants are supported");
        };
    });

    public BlockStateModel bake(Baker var1);

    default public BlockStateModel.UnbakedGrouped cached() {
        return new BlockStateModel.CachedUnbaked(this);
    }
}
