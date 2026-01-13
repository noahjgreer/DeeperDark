/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.item.model.special.PlayerHeadModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record PlayerHeadModelRenderer.Unbaked() implements SpecialModelRenderer.Unbaked
{
    public static final MapCodec<PlayerHeadModelRenderer.Unbaked> CODEC = MapCodec.unit(PlayerHeadModelRenderer.Unbaked::new);

    public MapCodec<PlayerHeadModelRenderer.Unbaked> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
        SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(context.entityModelSet(), SkullBlock.Type.PLAYER);
        if (skullBlockEntityModel == null) {
            return null;
        }
        return new PlayerHeadModelRenderer(context.playerSkinRenderCache(), skullBlockEntityModel);
    }
}
