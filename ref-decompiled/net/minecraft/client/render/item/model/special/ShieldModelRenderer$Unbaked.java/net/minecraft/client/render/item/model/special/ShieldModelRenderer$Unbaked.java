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
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.ShieldModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;

@Environment(value=EnvType.CLIENT)
public record ShieldModelRenderer.Unbaked() implements SpecialModelRenderer.Unbaked
{
    public static final ShieldModelRenderer.Unbaked INSTANCE = new ShieldModelRenderer.Unbaked();
    public static final MapCodec<ShieldModelRenderer.Unbaked> CODEC = MapCodec.unit((Object)INSTANCE);

    public MapCodec<ShieldModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new ShieldModelRenderer(context.spriteHolder(), new ShieldEntityModel(context.entityModelSet().getModelPart(EntityModelLayers.SHIELD)));
    }
}
