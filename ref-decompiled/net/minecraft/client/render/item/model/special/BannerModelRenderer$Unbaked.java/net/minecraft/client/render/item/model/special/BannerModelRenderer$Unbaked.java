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
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.BannerModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.util.DyeColor;

@Environment(value=EnvType.CLIENT)
public record BannerModelRenderer.Unbaked(DyeColor baseColor) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<BannerModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(BannerModelRenderer.Unbaked::baseColor)).apply((Applicative)instance, BannerModelRenderer.Unbaked::new));

    public MapCodec<BannerModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new BannerModelRenderer(this.baseColor, new BannerBlockEntityRenderer(context));
    }
}
