/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public record CompositeItemModel.Unbaked(List<ItemModel.Unbaked> models) implements ItemModel.Unbaked
{
    public static final MapCodec<CompositeItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ItemModelTypes.CODEC.listOf().fieldOf("models").forGetter(CompositeItemModel.Unbaked::models)).apply((Applicative)instance, CompositeItemModel.Unbaked::new));

    public MapCodec<CompositeItemModel.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        for (ItemModel.Unbaked unbaked : this.models) {
            unbaked.resolve(resolver);
        }
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        return new CompositeItemModel(this.models.stream().map(model -> model.bake(context)).toList());
    }
}
