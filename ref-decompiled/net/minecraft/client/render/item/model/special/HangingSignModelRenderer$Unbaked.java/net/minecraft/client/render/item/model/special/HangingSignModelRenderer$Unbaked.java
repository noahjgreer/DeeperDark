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
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.HangingSignModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record HangingSignModelRenderer.Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<HangingSignModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(HangingSignModelRenderer.Unbaked::woodType), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(HangingSignModelRenderer.Unbaked::texture)).apply((Applicative)instance, HangingSignModelRenderer.Unbaked::new));

    public HangingSignModelRenderer.Unbaked(WoodType woodType) {
        this(woodType, Optional.empty());
    }

    public MapCodec<HangingSignModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        Model.SinglePartModel singlePartModel = HangingSignBlockEntityRenderer.createModel(context.entityModelSet(), this.woodType, HangingSignBlockEntityRenderer.AttachmentType.CEILING_MIDDLE);
        SpriteIdentifier spriteIdentifier = this.texture.map(TexturedRenderLayers.HANGING_SIGN_SPRITE_MAPPER::map).orElseGet(() -> TexturedRenderLayers.getHangingSignTextureId(this.woodType));
        return new HangingSignModelRenderer(context.spriteHolder(), singlePartModel, spriteIdentifier);
    }
}
