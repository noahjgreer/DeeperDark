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
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record BasicItemModel.Unbaked(Identifier model, List<TintSource> tints) implements ItemModel.Unbaked
{
    public static final MapCodec<BasicItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("model").forGetter(BasicItemModel.Unbaked::model), (App)TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(BasicItemModel.Unbaked::tints)).apply((Applicative)instance, BasicItemModel.Unbaked::new));

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.model);
    }

    @Override
    public ItemModel bake(ItemModel.BakeContext context) {
        Baker baker = context.blockModelBaker();
        BakedSimpleModel bakedSimpleModel = baker.getModel(this.model);
        ModelTextures modelTextures = bakedSimpleModel.getTextures();
        List<BakedQuad> list = bakedSimpleModel.bakeGeometry(modelTextures, baker, ModelRotation.IDENTITY).getAllQuads();
        ModelSettings modelSettings = ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
        Function<ItemStack, RenderLayer> function = BasicItemModel.findRenderLayerGetter(list);
        return new BasicItemModel(this.tints, list, modelSettings, function);
    }

    public MapCodec<BasicItemModel.Unbaked> getCodec() {
        return CODEC;
    }
}
