/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityRenderState, SlimeEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SlimeEntityModel(context.getPart(EntityModelLayers.SLIME)), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    protected float getShadowRadius(SlimeEntityRenderState slimeEntityRenderState) {
        return (float)slimeEntityRenderState.size * 0.25f;
    }

    @Override
    protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
        float f = 0.999f;
        matrixStack.scale(0.999f, 0.999f, 0.999f);
        matrixStack.translate(0.0f, 0.001f, 0.0f);
        float g = slimeEntityRenderState.size;
        float h = slimeEntityRenderState.stretch / (g * 0.5f + 1.0f);
        float i = 1.0f / (h + 1.0f);
        matrixStack.scale(i * g, 1.0f / i * g, i * g);
    }

    @Override
    public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    @Override
    public void updateRenderState(SlimeEntity slimeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
        super.updateRenderState(slimeEntity, slimeEntityRenderState, f);
        slimeEntityRenderState.stretch = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch);
        slimeEntityRenderState.size = slimeEntity.getSize();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeEntityRenderState)livingEntityRenderState);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}
