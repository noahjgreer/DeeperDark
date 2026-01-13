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
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.BedModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record BedModelRenderer.Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<BedModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(BedModelRenderer.Unbaked::texture)).apply((Applicative)instance, BedModelRenderer.Unbaked::new));

    public BedModelRenderer.Unbaked(DyeColor color) {
        this(TexturedRenderLayers.createColorId(color));
    }

    public MapCodec<BedModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        return new BedModelRenderer(new BedBlockEntityRenderer(context), TexturedRenderLayers.BED_SPRITE_MAPPER.map(this.texture));
    }
}
