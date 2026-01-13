/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.IronGolemEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer
 *  net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.IronGolemEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.IronGolemEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.IronGolemEntity
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
import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityRenderState, IronGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/iron_golem/iron_golem.png");

    public IronGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new IronGolemEntityModel(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature((FeatureRenderer)new IronGolemCrackFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new IronGolemFlowerFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(IronGolemEntityRenderState ironGolemEntityRenderState) {
        return TEXTURE;
    }

    public IronGolemEntityRenderState createRenderState() {
        return new IronGolemEntityRenderState();
    }

    public void updateRenderState(IronGolemEntity ironGolemEntity, IronGolemEntityRenderState ironGolemEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)ironGolemEntity, (LivingEntityRenderState)ironGolemEntityRenderState, f);
        ironGolemEntityRenderState.attackTicksLeft = (float)ironGolemEntity.getAttackTicksLeft() > 0.0f ? (float)ironGolemEntity.getAttackTicksLeft() - f : 0.0f;
        ironGolemEntityRenderState.lookingAtVillagerTicks = ironGolemEntity.getLookingAtVillagerTicks();
        ironGolemEntityRenderState.crackLevel = ironGolemEntity.getCrackLevel();
    }

    protected void setupTransforms(IronGolemEntityRenderState ironGolemEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)ironGolemEntityRenderState, matrixStack, f, g);
        if ((double)ironGolemEntityRenderState.limbSwingAmplitude < 0.01) {
            return;
        }
        float h = 13.0f;
        float i = ironGolemEntityRenderState.limbSwingAnimationProgress + 6.0f;
        float j = (Math.abs(i % 13.0f - 6.5f) - 3.25f) / 3.25f;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(6.5f * j));
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IronGolemEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

