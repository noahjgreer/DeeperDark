/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityModel
extends EntityModel<LivingEntityRenderState> {
    private static final String BODY0 = "body0";
    private static final String BODY1 = "body1";
    private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
    private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
    private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
    private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleLeg;
    private final ModelPart leftMiddleLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public SpiderEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightMiddleLeg = modelPart.getChild(RIGHT_MIDDLE_HIND_LEG);
        this.leftMiddleLeg = modelPart.getChild(LEFT_MIDDLE_HIND_LEG);
        this.rightMiddleFrontLeg = modelPart.getChild(RIGHT_MIDDLE_FRONT_LEG);
        this.leftMiddleFrontLeg = modelPart.getChild(LEFT_MIDDLE_FRONT_LEG);
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 15;
        modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 4).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f), ModelTransform.origin(0.0f, 15.0f, -3.0f));
        modelPartData.addChild(BODY0, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.origin(0.0f, 15.0f, 0.0f));
        modelPartData.addChild(BODY1, ModelPartBuilder.create().uv(0, 12).cuboid(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f), ModelTransform.origin(0.0f, 15.0f, 9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).mirrored().cuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        float f = 0.7853982f;
        float g = 0.3926991f;
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.of(-4.0f, 15.0f, 2.0f, 0.0f, 0.7853982f, -0.7853982f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.of(4.0f, 15.0f, 2.0f, 0.0f, -0.7853982f, 0.7853982f));
        modelPartData.addChild(RIGHT_MIDDLE_HIND_LEG, modelPartBuilder, ModelTransform.of(-4.0f, 15.0f, 1.0f, 0.0f, 0.3926991f, -0.58119464f));
        modelPartData.addChild(LEFT_MIDDLE_HIND_LEG, modelPartBuilder2, ModelTransform.of(4.0f, 15.0f, 1.0f, 0.0f, -0.3926991f, 0.58119464f));
        modelPartData.addChild(RIGHT_MIDDLE_FRONT_LEG, modelPartBuilder, ModelTransform.of(-4.0f, 15.0f, 0.0f, 0.0f, -0.3926991f, -0.58119464f));
        modelPartData.addChild(LEFT_MIDDLE_FRONT_LEG, modelPartBuilder2, ModelTransform.of(4.0f, 15.0f, 0.0f, 0.0f, 0.3926991f, 0.58119464f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.of(-4.0f, 15.0f, -1.0f, 0.0f, -0.7853982f, -0.7853982f));
        modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.of(4.0f, 15.0f, -1.0f, 0.0f, 0.7853982f, 0.7853982f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles(livingEntityRenderState);
        this.head.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = livingEntityRenderState.pitch * ((float)Math.PI / 180);
        float f = livingEntityRenderState.limbSwingAnimationProgress * 0.6662f;
        float g = livingEntityRenderState.limbSwingAmplitude;
        float h = -(MathHelper.cos(f * 2.0f + 0.0f) * 0.4f) * g;
        float i = -(MathHelper.cos(f * 2.0f + (float)Math.PI) * 0.4f) * g;
        float j = -(MathHelper.cos(f * 2.0f + 1.5707964f) * 0.4f) * g;
        float k = -(MathHelper.cos(f * 2.0f + 4.712389f) * 0.4f) * g;
        float l = Math.abs(MathHelper.sin(f + 0.0f) * 0.4f) * g;
        float m = Math.abs(MathHelper.sin(f + (float)Math.PI) * 0.4f) * g;
        float n = Math.abs(MathHelper.sin(f + 1.5707964f) * 0.4f) * g;
        float o = Math.abs(MathHelper.sin(f + 4.712389f) * 0.4f) * g;
        this.rightHindLeg.yaw += h;
        this.leftHindLeg.yaw -= h;
        this.rightMiddleLeg.yaw += i;
        this.leftMiddleLeg.yaw -= i;
        this.rightMiddleFrontLeg.yaw += j;
        this.leftMiddleFrontLeg.yaw -= j;
        this.rightFrontLeg.yaw += k;
        this.leftFrontLeg.yaw -= k;
        this.rightHindLeg.roll += l;
        this.leftHindLeg.roll -= l;
        this.rightMiddleLeg.roll += m;
        this.leftMiddleLeg.roll -= m;
        this.rightMiddleFrontLeg.roll += n;
        this.leftMiddleFrontLeg.roll -= n;
        this.rightFrontLeg.roll += o;
        this.leftFrontLeg.roll -= o;
    }
}
