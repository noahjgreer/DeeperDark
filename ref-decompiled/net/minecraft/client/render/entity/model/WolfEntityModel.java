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
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.WolfEntityModel
 *  net.minecraft.client.render.entity.state.WolfEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel
extends EntityModel<WolfEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(Set.of("head"));
    private static final String REAL_HEAD = "real_head";
    private static final String UPPER_BODY = "upper_body";
    private static final String REAL_TAIL = "real_tail";
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart torso;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart neck;
    private static final int field_32580 = 8;

    public WolfEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.realHead = this.head.getChild(REAL_HEAD);
        this.torso = modelPart.getChild("body");
        this.neck = modelPart.getChild(UPPER_BODY);
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
        this.tail = modelPart.getChild("tail");
        this.realTail = this.tail.getChild(REAL_TAIL);
    }

    public static ModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 13.5f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.origin((float)-1.0f, (float)13.5f, (float)-7.0f));
        modelPartData2.addChild(REAL_HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, dilation).uv(16, 14).cuboid(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, dilation).uv(16, 14).cuboid(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f, dilation).uv(0, 10).cuboid(-0.5f, -0.001f, -5.0f, 3.0f, 3.0f, 4.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 14).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f, dilation), ModelTransform.of((float)0.0f, (float)14.0f, (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild(UPPER_BODY, ModelPartBuilder.create().uv(21, 0).cuboid(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f, dilation), ModelTransform.of((float)-1.0f, (float)14.0f, (float)-3.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().mirrored().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder2, ModelTransform.origin((float)-2.5f, (float)16.0f, (float)7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin((float)0.5f, (float)16.0f, (float)7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.origin((float)-2.5f, (float)16.0f, (float)-4.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin((float)0.5f, (float)16.0f, (float)-4.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("tail", ModelPartBuilder.create(), ModelTransform.of((float)-1.0f, (float)12.0f, (float)8.0f, (float)0.62831855f, (float)0.0f, (float)0.0f));
        modelPartData3.addChild(REAL_TAIL, ModelPartBuilder.create().uv(9, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.NONE);
        return modelData;
    }

    public void setAngles(WolfEntityRenderState wolfEntityRenderState) {
        super.setAngles((Object)wolfEntityRenderState);
        float f = wolfEntityRenderState.limbSwingAnimationProgress;
        float g = wolfEntityRenderState.limbSwingAmplitude;
        this.tail.yaw = wolfEntityRenderState.angerTime ? 0.0f : MathHelper.cos((double)(f * 0.6662f)) * 1.4f * g;
        if (wolfEntityRenderState.inSittingPose) {
            float h = wolfEntityRenderState.ageScale;
            this.neck.originY += 2.0f * h;
            this.neck.pitch = 1.2566371f;
            this.neck.yaw = 0.0f;
            this.torso.originY += 4.0f * h;
            this.torso.originZ -= 2.0f * h;
            this.torso.pitch = 0.7853982f;
            this.tail.originY += 9.0f * h;
            this.tail.originZ -= 2.0f * h;
            this.rightHindLeg.originY += 6.7f * h;
            this.rightHindLeg.originZ -= 5.0f * h;
            this.rightHindLeg.pitch = 4.712389f;
            this.leftHindLeg.originY += 6.7f * h;
            this.leftHindLeg.originZ -= 5.0f * h;
            this.leftHindLeg.pitch = 4.712389f;
            this.rightFrontLeg.pitch = 5.811947f;
            this.rightFrontLeg.originX += 0.01f * h;
            this.rightFrontLeg.originY += 1.0f * h;
            this.leftFrontLeg.pitch = 5.811947f;
            this.leftFrontLeg.originX -= 0.01f * h;
            this.leftFrontLeg.originY += 1.0f * h;
        } else {
            this.rightHindLeg.pitch = MathHelper.cos((double)(f * 0.6662f)) * 1.4f * g;
            this.leftHindLeg.pitch = MathHelper.cos((double)(f * 0.6662f + (float)Math.PI)) * 1.4f * g;
            this.rightFrontLeg.pitch = MathHelper.cos((double)(f * 0.6662f + (float)Math.PI)) * 1.4f * g;
            this.leftFrontLeg.pitch = MathHelper.cos((double)(f * 0.6662f)) * 1.4f * g;
        }
        this.realHead.roll = wolfEntityRenderState.begAnimationProgress + wolfEntityRenderState.getRoll(0.0f);
        this.neck.roll = wolfEntityRenderState.getRoll(-0.08f);
        this.torso.roll = wolfEntityRenderState.getRoll(-0.16f);
        this.realTail.roll = wolfEntityRenderState.getRoll(-0.2f);
        this.head.pitch = wolfEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = wolfEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.tail.pitch = wolfEntityRenderState.tailAngle;
    }
}

