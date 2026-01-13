/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GlowSquidEntityRenderer
 *  net.minecraft.client.render.entity.SquidEntityRenderer
 *  net.minecraft.client.render.entity.model.SquidEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SquidEntityRenderState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.GlowSquidEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SquidEntityRenderer;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class GlowSquidEntityRenderer
extends SquidEntityRenderer<GlowSquidEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/squid/glow_squid.png");

    public GlowSquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel squidEntityModel, SquidEntityModel squidEntityModel2) {
        super(context, squidEntityModel, squidEntityModel2);
    }

    public Identifier getTexture(SquidEntityRenderState squidEntityRenderState) {
        return TEXTURE;
    }

    protected int getBlockLight(GlowSquidEntity glowSquidEntity, BlockPos blockPos) {
        int i = (int)MathHelper.clampedLerp((float)(1.0f - (float)glowSquidEntity.getDarkTicksRemaining() / 10.0f), (float)0.0f, (float)15.0f);
        if (i == 15) {
            return 15;
        }
        return Math.max(i, super.getBlockLight((Entity)glowSquidEntity, blockPos));
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SquidEntityRenderState)state);
    }
}

