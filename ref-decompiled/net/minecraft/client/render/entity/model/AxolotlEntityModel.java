/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.AxolotlEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.AxolotlEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel
extends EntityModel<AxolotlEntityRenderState> {
    public static final float MOVING_IN_WATER_LEG_PITCH = 1.8849558f;
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private final ModelPart tail;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topGills;
    private final ModelPart leftGills;
    private final ModelPart rightGills;

    public AxolotlEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild("body");
        this.head = this.body.getChild("head");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
        this.topGills = this.head.getChild("top_gills");
        this.leftGills = this.head.getChild("left_gills");
        this.rightGills = this.head.getChild("right_gills");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).uv(2, 17).cuboid(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)5.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 1).cuboid(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, dilation), ModelTransform.origin((float)0.0f, (float)0.0f, (float)-9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        modelPartData3.addChild("top_gills", modelPartBuilder, ModelTransform.origin((float)0.0f, (float)-3.0f, (float)-1.0f));
        modelPartData3.addChild("left_gills", modelPartBuilder2, ModelTransform.origin((float)-4.0f, (float)0.0f, (float)-1.0f));
        modelPartData3.addChild("right_gills", modelPartBuilder3, ModelTransform.origin((float)4.0f, (float)0.0f, (float)-1.0f));
        ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        modelPartData2.addChild("right_hind_leg", modelPartBuilder5, ModelTransform.origin((float)-3.5f, (float)1.0f, (float)-1.0f));
        modelPartData2.addChild("left_hind_leg", modelPartBuilder4, ModelTransform.origin((float)3.5f, (float)1.0f, (float)-1.0f));
        modelPartData2.addChild("right_front_leg", modelPartBuilder5, ModelTransform.origin((float)-3.5f, (float)1.0f, (float)-8.0f));
        modelPartData2.addChild("left_front_leg", modelPartBuilder4, ModelTransform.origin((float)3.5f, (float)1.0f, (float)-8.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(2, 19).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)1.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(AxolotlEntityRenderState axolotlEntityRenderState) {
        super.setAngles((Object)axolotlEntityRenderState);
        float f = axolotlEntityRenderState.playingDeadValue;
        float g = axolotlEntityRenderState.inWaterValue;
        float h = axolotlEntityRenderState.onGroundValue;
        float i = axolotlEntityRenderState.isMovingValue;
        float j = 1.0f - i;
        float k = 1.0f - Math.min(h, i);
        this.body.yaw += axolotlEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.setMovingInWaterAngles(axolotlEntityRenderState.age, axolotlEntityRenderState.pitch, Math.min(i, g));
        this.setStandingInWaterAngles(axolotlEntityRenderState.age, Math.min(j, g));
        this.setMovingOnGroundAngles(axolotlEntityRenderState.age, Math.min(i, h));
        this.setStandingOnGroundAngles(axolotlEntityRenderState.age, Math.min(j, h));
        this.setPlayingDeadAngles(f);
        this.copyLegAngles(k);
    }

    private void setStandingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float f = animationProgress * 0.09f;
        float g = MathHelper.sin((double)f);
        float h = MathHelper.cos((double)f);
        float i = g * g - 2.0f * g;
        float j = h * h - 3.0f * g;
        this.head.pitch += -0.09f * i * headYaw;
        this.head.roll += -0.2f * headYaw;
        this.tail.yaw += (-0.1f + 0.1f * i) * headYaw;
        float k = (0.6f + 0.05f * j) * headYaw;
        this.topGills.pitch += k;
        this.leftGills.yaw -= k;
        this.rightGills.yaw += k;
        this.leftHindLeg.pitch += 1.1f * headYaw;
        this.leftHindLeg.yaw += 1.0f * headYaw;
        this.leftFrontLeg.pitch += 0.8f * headYaw;
        this.leftFrontLeg.yaw += 2.3f * headYaw;
        this.leftFrontLeg.roll -= 0.5f * headYaw;
    }

    private void setMovingOnGroundAngles(float animationProgress, float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        float f = animationProgress * 0.11f;
        float g = MathHelper.cos((double)f);
        float h = (g * g - 2.0f * g) / 5.0f;
        float i = 0.7f * g;
        float j = 0.09f * g * headYaw;
        this.head.yaw += j;
        this.tail.yaw += j;
        float k = (0.6f - 0.08f * (g * g + 2.0f * MathHelper.sin((double)f))) * headYaw;
        this.topGills.pitch += k;
        this.leftGills.yaw -= k;
        this.rightGills.yaw += k;
        float l = 0.9424779f * headYaw;
        float m = 1.0995574f * headYaw;
        this.leftHindLeg.pitch += l;
        this.leftHindLeg.yaw += (1.5f - h) * headYaw;
        this.leftHindLeg.roll += -0.1f * headYaw;
        this.leftFrontLeg.pitch += m;
        this.leftFrontLeg.yaw += (1.5707964f - i) * headYaw;
        this.rightHindLeg.pitch += l;
        this.rightHindLeg.yaw += (-1.0f - h) * headYaw;
        this.rightFrontLeg.pitch += m;
        this.rightFrontLeg.yaw += (-1.5707964f - i) * headYaw;
    }

    private void setStandingInWaterAngles(float f, float g) {
        if (g <= 1.0E-5f) {
            return;
        }
        float h = f * 0.075f;
        float i = MathHelper.cos((double)h);
        float j = MathHelper.sin((double)h) * 0.15f;
        float k = (-0.15f + 0.075f * i) * g;
        this.body.pitch += k;
        this.body.originY -= j * g;
        this.head.pitch -= k;
        this.topGills.pitch += 0.2f * i * g;
        float l = (-0.3f * i - 0.19f) * g;
        this.leftGills.yaw += l;
        this.rightGills.yaw -= l;
        this.leftHindLeg.pitch += (2.3561945f - i * 0.11f) * g;
        this.leftHindLeg.yaw += 0.47123894f * g;
        this.leftHindLeg.roll += 1.7278761f * g;
        this.leftFrontLeg.pitch += (0.7853982f - i * 0.2f) * g;
        this.leftFrontLeg.yaw += 2.042035f * g;
        this.tail.yaw += 0.5f * i * g;
    }

    private void setMovingInWaterAngles(float f, float headPitch, float g) {
        if (g <= 1.0E-5f) {
            return;
        }
        float h = f * 0.33f;
        float i = MathHelper.sin((double)h);
        float j = MathHelper.cos((double)h);
        float k = 0.13f * i;
        this.body.pitch += (headPitch * ((float)Math.PI / 180) + k) * g;
        this.head.pitch -= k * 1.8f * g;
        this.body.originY -= 0.45f * j * g;
        this.topGills.pitch += (-0.5f * i - 0.8f) * g;
        float l = (0.3f * i + 0.9f) * g;
        this.leftGills.yaw += l;
        this.rightGills.yaw -= l;
        this.tail.yaw += 0.3f * MathHelper.cos((double)(h * 0.9f)) * g;
        this.leftHindLeg.pitch += 1.8849558f * g;
        this.leftHindLeg.yaw += -0.4f * i * g;
        this.leftHindLeg.roll += 1.5707964f * g;
        this.leftFrontLeg.pitch += 1.8849558f * g;
        this.leftFrontLeg.yaw += (-0.2f * j - 0.1f) * g;
        this.leftFrontLeg.roll += 1.5707964f * g;
    }

    private void setPlayingDeadAngles(float headYaw) {
        if (headYaw <= 1.0E-5f) {
            return;
        }
        this.leftHindLeg.pitch += 1.4137167f * headYaw;
        this.leftHindLeg.yaw += 1.0995574f * headYaw;
        this.leftHindLeg.roll += 0.7853982f * headYaw;
        this.leftFrontLeg.pitch += 0.7853982f * headYaw;
        this.leftFrontLeg.yaw += 2.042035f * headYaw;
        this.body.pitch += -0.15f * headYaw;
        this.body.roll += 0.35f * headYaw;
    }

    private void copyLegAngles(float f) {
        if (f <= 1.0E-5f) {
            return;
        }
        this.rightHindLeg.pitch += this.leftHindLeg.pitch * f;
        ModelPart modelPart = this.rightHindLeg;
        modelPart.yaw = modelPart.yaw + -this.leftHindLeg.yaw * f;
        modelPart = this.rightHindLeg;
        modelPart.roll = modelPart.roll + -this.leftHindLeg.roll * f;
        this.rightFrontLeg.pitch += this.leftFrontLeg.pitch * f;
        modelPart = this.rightFrontLeg;
        modelPart.yaw = modelPart.yaw + -this.leftFrontLeg.yaw * f;
        modelPart = this.rightFrontLeg;
        modelPart.roll = modelPart.roll + -this.leftFrontLeg.roll * f;
    }
}

