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
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.DecoratedPotModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;

@Environment(value=EnvType.CLIENT)
public record DecoratedPotModelRenderer.Unbaked() implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<DecoratedPotModelRenderer.Unbaked> CODEC = MapCodec.unit((Object)new DecoratedPotModelRenderer.Unbaked());

    public MapCodec<DecoratedPotModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new DecoratedPotModelRenderer(new DecoratedPotBlockEntityRenderer(context));
    }
}
