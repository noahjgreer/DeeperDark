/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public record RangeDispatchItemModel.Unbaked(NumericProperty property, float scale, List<RangeDispatchItemModel.Entry> entries, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked
{
    public static final MapCodec<RangeDispatchItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)NumericProperties.CODEC.forGetter(RangeDispatchItemModel.Unbaked::property), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(RangeDispatchItemModel.Unbaked::scale), (App)RangeDispatchItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(RangeDispatchItemModel.Unbaked::entries), (App)ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(RangeDispatchItemModel.Unbaked::fallback)).apply((Applicative)instance, RangeDispatchItemModel.Unbaked::new));

    public MapCodec<RangeDispatchItemModel.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        float[] fs = new float[this.entries.size()];
        ItemModel[] itemModels = new ItemModel[this.entries.size()];
        ArrayList<RangeDispatchItemModel.Entry> list = new ArrayList<RangeDispatchItemModel.Entry>(this.entries);
        list.sort(RangeDispatchItemModel.Entry.COMPARATOR);
        for (int i = 0; i < list.size(); ++i) {
            RangeDispatchItemModel.Entry entry = (RangeDispatchItemModel.Entry)list.get(i);
            fs[i] = entry.threshold;
            itemModels[i] = entry.model.bake(context);
        }
        ItemModel itemModel = this.fallback.map(model -> model.bake(context)).orElse(context.missingItemModel());
        return new RangeDispatchItemModel(this.property, this.scale, fs, itemModels, itemModel);
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        this.fallback.ifPresent(model -> model.resolve(resolver));
        this.entries.forEach(entry -> entry.model.resolve(resolver));
    }
}
