/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BlockStateModel$Unbaked
 *  net.minecraft.client.render.model.SimpleBlockStateModel$Unbaked
 *  net.minecraft.client.render.model.WeightedBlockStateModel$Unbaked
 *  net.minecraft.client.render.model.json.ModelVariant
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.client.render.model.json.WeightedVariant
 *  net.minecraft.util.collection.Pool
 *  net.minecraft.util.collection.Weighted
 */
package net.minecraft.client.render.model.json;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

@Environment(value=EnvType.CLIENT)
public record WeightedVariant(Pool<ModelVariant> variants) {
    private final Pool<ModelVariant> variants;

    public WeightedVariant(Pool<ModelVariant> variants) {
        if (variants.isEmpty()) {
            throw new IllegalArgumentException("Variant list must contain at least one element");
        }
        this.variants = variants;
    }

    public WeightedVariant apply(ModelVariantOperator operator) {
        return new WeightedVariant(this.variants.transform((Function)operator));
    }

    public BlockStateModel.Unbaked toModel() {
        List list = this.variants.getEntries();
        return list.size() == 1 ? new SimpleBlockStateModel.Unbaked((ModelVariant)((Weighted)list.getFirst()).value()) : new WeightedBlockStateModel.Unbaked(this.variants.transform(SimpleBlockStateModel.Unbaked::new));
    }

    public Pool<ModelVariant> variants() {
        return this.variants;
    }
}

