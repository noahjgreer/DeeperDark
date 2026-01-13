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
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.model.special.ChestModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ChestModelRenderer.Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<ChestModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(ChestModelRenderer.Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(ChestModelRenderer.Unbaked::openness)).apply((Applicative)instance, ChestModelRenderer.Unbaked::new));

    public ChestModelRenderer.Unbaked(Identifier texture) {
        this(texture, 0.0f);
    }

    public MapCodec<ChestModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        ChestBlockModel chestBlockModel = new ChestBlockModel(context.entityModelSet().getModelPart(EntityModelLayers.CHEST));
        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.CHEST_SPRITE_MAPPER.map(this.texture);
        return new ChestModelRenderer(context.spriteHolder(), chestBlockModel, spriteIdentifier, this.openness);
    }
}
