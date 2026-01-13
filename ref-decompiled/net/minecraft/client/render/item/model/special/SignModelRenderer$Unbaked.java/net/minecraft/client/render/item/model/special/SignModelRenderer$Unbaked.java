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
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.SignModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record SignModelRenderer.Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<SignModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(SignModelRenderer.Unbaked::woodType), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(SignModelRenderer.Unbaked::texture)).apply((Applicative)instance, SignModelRenderer.Unbaked::new));

    public SignModelRenderer.Unbaked(WoodType woodType) {
        this(woodType, Optional.empty());
    }

    public MapCodec<SignModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        Model.SinglePartModel singlePartModel = SignBlockEntityRenderer.createSignModel(context.entityModelSet(), this.woodType, true);
        SpriteIdentifier spriteIdentifier = this.texture.map(TexturedRenderLayers.SIGN_SPRITE_MAPPER::map).orElseGet(() -> TexturedRenderLayers.getSignTextureId(this.woodType));
        return new SignModelRenderer(context.spriteHolder(), singlePartModel, spriteIdentifier);
    }
}
