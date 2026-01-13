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
import net.minecraft.client.render.item.model.special.ConduitModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;

@Environment(value=EnvType.CLIENT)
public record ConduitModelRenderer.Unbaked() implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<ConduitModelRenderer.Unbaked> CODEC = MapCodec.unit((Object)new ConduitModelRenderer.Unbaked());

    public MapCodec<ConduitModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new ConduitModelRenderer(context.spriteHolder(), context.entityModelSet().getModelPart(EntityModelLayers.CONDUIT_SHELL));
    }
}
