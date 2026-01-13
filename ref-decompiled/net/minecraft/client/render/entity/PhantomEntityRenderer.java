/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.PhantomEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PhantomEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PhantomEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.PhantomEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PhantomEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityRenderer
extends MobEntityRenderer<PhantomEntity, PhantomEntityRenderState, PhantomEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/phantom.png");

    public PhantomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new PhantomEntityModel(context.getPart(EntityModelLayers.PHANTOM)), 0.75f);
        this.addFeature((FeatureRenderer)new PhantomEyesFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(PhantomEntityRenderState phantomEntityRenderState) {
        return TEXTURE;
    }

    public PhantomEntityRenderState createRenderState() {
        return new PhantomEntityRenderState();
    }

    public void updateRenderState(PhantomEntity phantomEntity, PhantomEntityRenderState phantomEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)phantomEntity, (LivingEntityRenderState)phantomEntityRenderState, f);
        phantomEntityRenderState.wingFlapProgress = (float)phantomEntity.getWingFlapTickOffset() + phantomEntityRenderState.age;
        phantomEntityRenderState.size = phantomEntity.getPhantomSize();
    }

    protected void scale(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack) {
        float f = 1.0f + 0.15f * (float)phantomEntityRenderState.size;
        matrixStack.scale(f, f, f);
        matrixStack.translate(0.0f, 1.3125f, 0.1875f);
    }

    protected void setupTransforms(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)phantomEntityRenderState, matrixStack, f, g);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(phantomEntityRenderState.pitch));
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PhantomEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

