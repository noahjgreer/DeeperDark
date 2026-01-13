/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.PandaEntityModel
 *  net.minecraft.client.render.entity.model.QuadrupedEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PandaEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaEntityModel
extends QuadrupedEntityModel<PandaEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 23.0f, 4.8f, 2.7f, 3.0f, 49.0f, Set.of("head"));

    public PandaEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 6).cuboid(-6.5f, -5.0f, -4.0f, 13.0f, 10.0f, 9.0f).uv(45, 16).cuboid("nose", -3.5f, 0.0f, -6.0f, 7.0f, 5.0f, 2.0f).uv(52, 25).cuboid("left_ear", 3.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f).uv(52, 25).cuboid("right_ear", -8.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)11.5f, (float)-17.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 25).cuboid(-9.5f, -13.0f, -6.5f, 19.0f, 26.0f, 13.0f), ModelTransform.of((float)0.0f, (float)10.0f, (float)0.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        int i = 9;
        int j = 6;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(40, 0).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 9.0f, 6.0f);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin((float)-5.5f, (float)15.0f, (float)9.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin((float)5.5f, (float)15.0f, (float)9.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin((float)-5.5f, (float)15.0f, (float)-9.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin((float)5.5f, (float)15.0f, (float)-9.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(PandaEntityRenderState pandaEntityRenderState) {
        super.setAngles((LivingEntityRenderState)pandaEntityRenderState);
        if (pandaEntityRenderState.askingForBamboo) {
            this.head.yaw = 0.35f * MathHelper.sin((double)(0.6f * pandaEntityRenderState.age));
            this.head.roll = 0.35f * MathHelper.sin((double)(0.6f * pandaEntityRenderState.age));
            this.rightFrontLeg.pitch = -0.75f * MathHelper.sin((double)(0.3f * pandaEntityRenderState.age));
            this.leftFrontLeg.pitch = 0.75f * MathHelper.sin((double)(0.3f * pandaEntityRenderState.age));
        } else {
            this.head.roll = 0.0f;
        }
        if (pandaEntityRenderState.sneezing) {
            if (pandaEntityRenderState.sneezeProgress < 15) {
                this.head.pitch = -0.7853982f * (float)pandaEntityRenderState.sneezeProgress / 14.0f;
            } else if (pandaEntityRenderState.sneezeProgress < 20) {
                float f = (pandaEntityRenderState.sneezeProgress - 15) / 5;
                this.head.pitch = -0.7853982f + 0.7853982f * f;
            }
        }
        if (pandaEntityRenderState.sittingAnimationProgress > 0.0f) {
            this.body.pitch = MathHelper.lerpAngleRadians((float)pandaEntityRenderState.sittingAnimationProgress, (float)this.body.pitch, (float)1.7407963f);
            this.head.pitch = MathHelper.lerpAngleRadians((float)pandaEntityRenderState.sittingAnimationProgress, (float)this.head.pitch, (float)1.5707964f);
            this.rightFrontLeg.roll = -0.27079642f;
            this.leftFrontLeg.roll = 0.27079642f;
            this.rightHindLeg.roll = 0.5707964f;
            this.leftHindLeg.roll = -0.5707964f;
            if (pandaEntityRenderState.eating) {
                this.head.pitch = 1.5707964f + 0.2f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.6f));
                this.rightFrontLeg.pitch = -0.4f - 0.2f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.6f));
                this.leftFrontLeg.pitch = -0.4f - 0.2f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.6f));
            }
            if (pandaEntityRenderState.scaredByThunderstorm) {
                this.head.pitch = 2.1707964f;
                this.rightFrontLeg.pitch = -0.9f;
                this.leftFrontLeg.pitch = -0.9f;
            }
        } else {
            this.rightHindLeg.roll = 0.0f;
            this.leftHindLeg.roll = 0.0f;
            this.rightFrontLeg.roll = 0.0f;
            this.leftFrontLeg.roll = 0.0f;
        }
        if (pandaEntityRenderState.lieOnBackAnimationProgress > 0.0f) {
            this.rightHindLeg.pitch = -0.6f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.15f));
            this.leftHindLeg.pitch = 0.6f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.15f));
            this.rightFrontLeg.pitch = 0.3f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.25f));
            this.leftFrontLeg.pitch = -0.3f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.25f));
            this.head.pitch = MathHelper.lerpAngleRadians((float)pandaEntityRenderState.lieOnBackAnimationProgress, (float)this.head.pitch, (float)1.5707964f);
        }
        if (pandaEntityRenderState.rollOverAnimationProgress > 0.0f) {
            this.head.pitch = MathHelper.lerpAngleRadians((float)pandaEntityRenderState.rollOverAnimationProgress, (float)this.head.pitch, (float)2.0561945f);
            this.rightHindLeg.pitch = -0.5f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.5f));
            this.leftHindLeg.pitch = 0.5f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.5f));
            this.rightFrontLeg.pitch = 0.5f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.5f));
            this.leftFrontLeg.pitch = -0.5f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.5f));
        }
    }
}

