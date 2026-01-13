/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.TridentModelRenderer;

@Environment(value=EnvType.CLIENT)
public record TridentModelRenderer.Unbaked() implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<TridentModelRenderer.Unbaked> CODEC = MapCodec.unit((Object)new TridentModelRenderer.Unbaked());

    public MapCodec<TridentModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new TridentModelRenderer(new TridentEntityModel(context.entityModelSet().getModelPart(EntityModelLayers.TRIDENT)));
    }
}
