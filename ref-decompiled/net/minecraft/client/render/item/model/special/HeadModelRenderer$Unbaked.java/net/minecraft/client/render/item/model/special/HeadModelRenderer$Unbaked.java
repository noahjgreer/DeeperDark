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
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.HeadModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record HeadModelRenderer.Unbaked(SkullBlock.SkullType kind, Optional<Identifier> textureOverride, float animation) implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<HeadModelRenderer.Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(HeadModelRenderer.Unbaked::kind), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(HeadModelRenderer.Unbaked::textureOverride), (App)Codec.FLOAT.optionalFieldOf("animation", (Object)Float.valueOf(0.0f)).forGetter(HeadModelRenderer.Unbaked::animation)).apply((Applicative)instance, HeadModelRenderer.Unbaked::new));

    public HeadModelRenderer.Unbaked(SkullBlock.SkullType kind) {
        this(kind, Optional.empty(), 0.0f);
    }

    public MapCodec<HeadModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(context.entityModelSet(), this.kind);
        Identifier identifier = this.textureOverride.map(id -> id.withPath(texture -> "textures/entity/" + texture + ".png")).orElse(null);
        if (skullBlockEntityModel == null) {
            return null;
        }
        RenderLayer renderLayer = SkullBlockEntityRenderer.getCutoutRenderLayer(this.kind, identifier);
        return new HeadModelRenderer(skullBlockEntityModel, this.animation, renderLayer);
    }
}
