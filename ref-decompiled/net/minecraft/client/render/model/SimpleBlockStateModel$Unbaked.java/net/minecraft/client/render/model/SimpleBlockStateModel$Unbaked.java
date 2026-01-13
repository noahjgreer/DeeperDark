/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.json.ModelVariant;

@Environment(value=EnvType.CLIENT)
public record SimpleBlockStateModel.Unbaked(ModelVariant variant) implements BlockStateModel.Unbaked
{
    public static final Codec<SimpleBlockStateModel.Unbaked> CODEC = ModelVariant.CODEC.xmap(SimpleBlockStateModel.Unbaked::new, SimpleBlockStateModel.Unbaked::variant);

    @Override
    public BlockStateModel bake(Baker baker) {
        return new SimpleBlockStateModel(this.variant.bake(baker));
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.variant.resolve(resolver);
    }
}
