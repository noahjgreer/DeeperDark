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
 *  net.minecraft.client.render.entity.model.BeeEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.BeeEntityRenderState
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
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeeEntityModel
extends EntityModel<BeeEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private static final String BONE = "bone";
    private static final String STINGER = "stinger";
    private static final String LEFT_ANTENNA = "left_antenna";
    private static final String RIGHT_ANTENNA = "right_antenna";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.bone = modelPart.getChild(BONE);
        ModelPart modelPart2 = this.bone.getChild("body");
        this.stinger = modelPart2.getChild(STINGER);
        this.leftAntenna = modelPart2.getChild(LEFT_ANTENNA);
        this.rightAntenna = modelPart2.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild("right_wing");
        this.leftWing = this.bone.getChild("left_wing");
        this.frontLegs = this.bone.getChild(FRONT_LEGS);
        this.middleLegs = this.bone.getChild(MIDDLE_LEGS);
        this.backLegs = this.bone.getChild(BACK_LEGS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BONE, ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)19.0f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), ModelTransform.NONE);
        modelPartData3.addChild(STINGER, ModelPartBuilder.create().uv(26, 7).cuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_ANTENNA, ModelPartBuilder.create().uv(2, 0).cuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)-2.0f, (float)-5.0f));
        modelPartData3.addChild(RIGHT_ANTENNA, ModelPartBuilder.create().uv(2, 3).cuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)-2.0f, (float)-5.0f));
        Dilation dilation = new Dilation(0.001f);
        modelPartData2.addChild("right_wing", ModelPartBuilder.create().uv(0, 18).cuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of((float)-1.5f, (float)-4.0f, (float)-3.0f, (float)0.0f, (float)-0.2618f, (float)0.0f));
        modelPartData2.addChild("left_wing", ModelPartBuilder.create().uv(0, 18).mirrored().cuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of((float)1.5f, (float)-4.0f, (float)-3.0f, (float)0.0f, (float)0.2618f, (float)0.0f));
        modelPartData2.addChild(FRONT_LEGS, ModelPartBuilder.create().cuboid(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), ModelTransform.origin((float)1.5f, (float)3.0f, (float)-2.0f));
        modelPartData2.addChild(MIDDLE_LEGS, ModelPartBuilder.create().cuboid(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), ModelTransform.origin((float)1.5f, (float)3.0f, (float)0.0f));
        modelPartData2.addChild(BACK_LEGS, ModelPartBuilder.create().cuboid(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), ModelTransform.origin((float)1.5f, (float)3.0f, (float)2.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(BeeEntityRenderState beeEntityRenderState) {
        float f;
        super.setAngles((Object)beeEntityRenderState);
        this.bodyPitch = beeEntityRenderState.bodyPitch;
        this.stinger.visible = beeEntityRenderState.hasStinger;
        if (!beeEntityRenderState.stoppedOnGround) {
            f = beeEntityRenderState.age * 120.32113f * ((float)Math.PI / 180);
            this.rightWing.yaw = 0.0f;
            this.rightWing.roll = MathHelper.cos((double)f) * (float)Math.PI * 0.15f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982f;
            this.middleLegs.pitch = 0.7853982f;
            this.backLegs.pitch = 0.7853982f;
        }
        if (!beeEntityRenderState.angry && !beeEntityRenderState.stoppedOnGround) {
            f = MathHelper.cos((double)(beeEntityRenderState.age * 0.18f));
            this.bone.pitch = 0.1f + f * (float)Math.PI * 0.025f;
            this.leftAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.rightAntenna.pitch = f * (float)Math.PI * 0.03f;
            this.frontLegs.pitch = -f * (float)Math.PI * 0.1f + 0.3926991f;
            this.backLegs.pitch = -f * (float)Math.PI * 0.05f + 0.7853982f;
            this.bone.originY -= MathHelper.cos((double)(beeEntityRenderState.age * 0.18f)) * 0.9f;
        }
        if (this.bodyPitch > 0.0f) {
            this.bone.pitch = MathHelper.lerpAngleRadians((float)this.bodyPitch, (float)this.bone.pitch, (float)3.0915928f);
        }
    }
}

