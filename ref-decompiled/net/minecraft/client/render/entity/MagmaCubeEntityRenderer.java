/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MagmaCubeEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.MagmaCubeEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SlimeEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.MagmaCubeEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MagmaCubeEntityRenderer
extends MobEntityRenderer<MagmaCubeEntity, SlimeEntityRenderState, MagmaCubeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/slime/magmacube.png");

    public MagmaCubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new MagmaCubeEntityModel(context.getPart(EntityModelLayers.MAGMA_CUBE)), 0.25f);
    }

    protected int getBlockLight(MagmaCubeEntity magmaCubeEntity, BlockPos blockPos) {
        return 15;
    }

    public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
        return TEXTURE;
    }

    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    public void updateRenderState(MagmaCubeEntity magmaCubeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)magmaCubeEntity, (LivingEntityRenderState)slimeEntityRenderState, f);
        slimeEntityRenderState.stretch = MathHelper.lerp((float)f, (float)magmaCubeEntity.lastStretch, (float)magmaCubeEntity.stretch);
        slimeEntityRenderState.size = magmaCubeEntity.getSize();
    }

    protected float getShadowRadius(SlimeEntityRenderState slimeEntityRenderState) {
        return (float)slimeEntityRenderState.size * 0.25f;
    }

    protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
        int i = slimeEntityRenderState.size;
        float f = slimeEntityRenderState.stretch / ((float)i * 0.5f + 1.0f);
        float g = 1.0f / (f + 1.0f);
        matrixStack.scale(g * (float)i, 1.0f / g * (float)i, g * (float)i);
    }

    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeEntityRenderState)livingEntityRenderState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SlimeEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}

