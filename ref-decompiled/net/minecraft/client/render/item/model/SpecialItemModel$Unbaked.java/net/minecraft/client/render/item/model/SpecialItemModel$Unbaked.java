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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record SpecialItemModel.Unbaked(Identifier base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked
{
    public static final MapCodec<SpecialItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("base").forGetter(SpecialItemModel.Unbaked::base), (App)SpecialModelTypes.CODEC.fieldOf("model").forGetter(SpecialItemModel.Unbaked::specialModel)).apply((Applicative)instance, SpecialItemModel.Unbaked::new));

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.base);
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        SpecialModelRenderer<?> specialModelRenderer = this.specialModel.bake(context);
        if (specialModelRenderer == null) {
            return context.missingItemModel();
        }
        ModelSettings modelSettings = this.getSettings(context);
        return new SpecialItemModel(specialModelRenderer, modelSettings);
    }

    private ModelSettings getSettings(ItemModel.BakeContext context) {
        Baker baker = context.blockModelBaker();
        BakedSimpleModel bakedSimpleModel = baker.getModel(this.base);
        ModelTextures modelTextures = bakedSimpleModel.getTextures();
        return ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
    }

    public MapCodec<SpecialItemModel.Unbaked> getCodec() {
        return CODEC;
    }
}
