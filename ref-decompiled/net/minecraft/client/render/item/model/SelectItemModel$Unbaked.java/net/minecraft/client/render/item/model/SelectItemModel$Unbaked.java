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
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public record SelectItemModel.Unbaked(SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
{
    public static final MapCodec<SelectItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SelectItemModel.UnbakedSwitch.CODEC.forGetter(SelectItemModel.Unbaked::unbakedSwitch), (App)ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(SelectItemModel.Unbaked::fallback)).apply((Applicative)instance, SelectItemModel.Unbaked::new));

    public MapCodec<SelectItemModel.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        ItemModel itemModel = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
        return this.unbakedSwitch.bake(context, itemModel);
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.unbakedSwitch.resolveCases(resolver);
        this.fallback.ifPresent(model -> model.resolve(resolver));
    }
}
