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
 *  net.minecraft.client.render.entity.DragonEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EnderDragonEntityRenderState
 *  net.minecraft.entity.boss.dragon.EnderDragonFrameTracker$Frame
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.entity.boss.dragon.EnderDragonFrameTracker;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DragonEntityModel
extends EntityModel<EnderDragonEntityRenderState> {
    private static final int NUM_NECK_PARTS = 5;
    private static final int NUM_TAIL_PARTS = 12;
    private final ModelPart head;
    private final ModelPart[] neckParts = new ModelPart[5];
    private final ModelPart[] tailParts = new ModelPart[12];
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftFrontLegTip;
    private final ModelPart leftFrontFoot;
    private final ModelPart leftHindLeg;
    private final ModelPart leftHindLegTip;
    private final ModelPart leftHindFoot;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightFrontLegTip;
    private final ModelPart rightFrontFoot;
    private final ModelPart rightHindLeg;
    private final ModelPart rightHindLegTip;
    private final ModelPart rightHindFoot;

    private static String neck(int id) {
        return "neck" + id;
    }

    private static String tail(int id) {
        return "tail" + id;
    }

    public DragonEntityModel(ModelPart modelPart) {
        super(modelPart);
        int i;
        this.head = modelPart.getChild("head");
        this.jaw = this.head.getChild("jaw");
        for (i = 0; i < this.neckParts.length; ++i) {
            this.neckParts[i] = modelPart.getChild(DragonEntityModel.neck((int)i));
        }
        for (i = 0; i < this.tailParts.length; ++i) {
            this.tailParts[i] = modelPart.getChild(DragonEntityModel.tail((int)i));
        }
        this.body = modelPart.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
        this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.leftHindLegTip = this.leftHindLeg.getChild("left_hind_leg_tip");
        this.leftHindFoot = this.leftHindLegTip.getChild("left_hind_foot");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
        this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.rightHindLegTip = this.rightHindLeg.getChild("right_hind_leg_tip");
        this.rightHindFoot = this.rightHindLegTip.getChild("right_hind_foot");
    }

    public static TexturedModelData createTexturedModelData() {
        int i;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = -16.0f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().cuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).cuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirrored().cuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirrored().cuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), ModelTransform.origin((float)0.0f, (float)20.0f, (float)-62.0f));
        modelPartData2.addChild("jaw", ModelPartBuilder.create().cuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 176, 65), ModelTransform.origin((float)0.0f, (float)4.0f, (float)-8.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().cuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 192, 104).cuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 48, 0);
        for (i = 0; i < 5; ++i) {
            modelPartData.addChild(DragonEntityModel.neck((int)i), modelPartBuilder, ModelTransform.origin((float)0.0f, (float)20.0f, (float)(-12.0f - (float)i * 10.0f)));
        }
        for (i = 0; i < 12; ++i) {
            modelPartData.addChild(DragonEntityModel.tail((int)i), modelPartBuilder, ModelTransform.origin((float)0.0f, (float)10.0f, (float)(60.0f + (float)i * 10.0f)));
        }
        ModelPartData modelPartData3 = modelPartData.addChild("body", ModelPartBuilder.create().cuboid("body", -12.0f, 1.0f, -16.0f, 24, 24, 64, 0, 0).cuboid("scale", -1.0f, -5.0f, -10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 10.0f, 2, 6, 12, 220, 53).cuboid("scale", -1.0f, -5.0f, 30.0f, 2, 6, 12, 220, 53), ModelTransform.origin((float)0.0f, (float)3.0f, (float)8.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("left_wing", ModelPartBuilder.create().mirrored().cuboid("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.origin((float)12.0f, (float)2.0f, (float)-6.0f));
        modelPartData4.addChild("left_wing_tip", ModelPartBuilder.create().mirrored().cuboid("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.origin((float)56.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData5 = modelPartData3.addChild("left_front_leg", ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of((float)12.0f, (float)17.0f, (float)-6.0f, (float)1.3f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData6 = modelPartData5.addChild("left_front_leg_tip", ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of((float)0.0f, (float)20.0f, (float)-1.0f, (float)-0.5f, (float)0.0f, (float)0.0f));
        modelPartData6.addChild("left_front_foot", ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of((float)0.0f, (float)23.0f, (float)0.0f, (float)0.75f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData7 = modelPartData3.addChild("left_hind_leg", ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of((float)16.0f, (float)13.0f, (float)34.0f, (float)1.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData8 = modelPartData7.addChild("left_hind_leg_tip", ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of((float)0.0f, (float)32.0f, (float)-4.0f, (float)0.5f, (float)0.0f, (float)0.0f));
        modelPartData8.addChild("left_hind_foot", ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of((float)0.0f, (float)31.0f, (float)4.0f, (float)0.75f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData9 = modelPartData3.addChild("right_wing", ModelPartBuilder.create().cuboid("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 112, 88).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 88), ModelTransform.origin((float)-12.0f, (float)2.0f, (float)-6.0f));
        modelPartData9.addChild("right_wing_tip", ModelPartBuilder.create().cuboid("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 112, 136).cuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, -56, 144), ModelTransform.origin((float)-56.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData10 = modelPartData3.addChild("right_front_leg", ModelPartBuilder.create().cuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 112, 104), ModelTransform.of((float)-12.0f, (float)17.0f, (float)-6.0f, (float)1.3f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData11 = modelPartData10.addChild("right_front_leg_tip", ModelPartBuilder.create().cuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 226, 138), ModelTransform.of((float)0.0f, (float)20.0f, (float)-1.0f, (float)-0.5f, (float)0.0f, (float)0.0f));
        modelPartData11.addChild("right_front_foot", ModelPartBuilder.create().cuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 144, 104), ModelTransform.of((float)0.0f, (float)23.0f, (float)0.0f, (float)0.75f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData12 = modelPartData3.addChild("right_hind_leg", ModelPartBuilder.create().cuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0, 0), ModelTransform.of((float)-16.0f, (float)13.0f, (float)34.0f, (float)1.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData13 = modelPartData12.addChild("right_hind_leg_tip", ModelPartBuilder.create().cuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 196, 0), ModelTransform.of((float)0.0f, (float)32.0f, (float)-4.0f, (float)0.5f, (float)0.0f, (float)0.0f));
        modelPartData13.addChild("right_hind_foot", ModelPartBuilder.create().cuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 112, 0), ModelTransform.of((float)0.0f, (float)31.0f, (float)4.0f, (float)0.75f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)256, (int)256);
    }

    public void setAngles(EnderDragonEntityRenderState enderDragonEntityRenderState) {
        super.setAngles((Object)enderDragonEntityRenderState);
        float f = enderDragonEntityRenderState.wingPosition * ((float)Math.PI * 2);
        this.jaw.pitch = (MathHelper.sin((double)f) + 1.0f) * 0.2f;
        float g = MathHelper.sin((double)(f - 1.0f)) + 1.0f;
        g = (g * g + g * 2.0f) * 0.05f;
        this.root.originY = (g - 2.0f) * 16.0f;
        this.root.originZ = -48.0f;
        this.root.pitch = g * 2.0f * ((float)Math.PI / 180);
        float h = this.neckParts[0].originX;
        float i = this.neckParts[0].originY;
        float j = this.neckParts[0].originZ;
        float k = 1.5f;
        EnderDragonFrameTracker.Frame frame = enderDragonEntityRenderState.getLerpedFrame(6);
        float l = MathHelper.wrapDegrees((float)(enderDragonEntityRenderState.getLerpedFrame(5).yRot() - enderDragonEntityRenderState.getLerpedFrame(10).yRot()));
        float m = MathHelper.wrapDegrees((float)(enderDragonEntityRenderState.getLerpedFrame(5).yRot() + l / 2.0f));
        for (int n = 0; n < 5; ++n) {
            ModelPart modelPart = this.neckParts[n];
            EnderDragonFrameTracker.Frame frame2 = enderDragonEntityRenderState.getLerpedFrame(5 - n);
            float o = MathHelper.cos((double)((float)n * 0.45f + f)) * 0.15f;
            modelPart.yaw = MathHelper.wrapDegrees((float)(frame2.yRot() - frame.yRot())) * ((float)Math.PI / 180) * 1.5f;
            modelPart.pitch = o + enderDragonEntityRenderState.getNeckPartPitchOffset(n, frame, frame2) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            modelPart.roll = -MathHelper.wrapDegrees((float)(frame2.yRot() - m)) * ((float)Math.PI / 180) * 1.5f;
            modelPart.originY = i;
            modelPart.originZ = j;
            modelPart.originX = h;
            h -= MathHelper.sin((double)modelPart.yaw) * MathHelper.cos((double)modelPart.pitch) * 10.0f;
            i += MathHelper.sin((double)modelPart.pitch) * 10.0f;
            j -= MathHelper.cos((double)modelPart.yaw) * MathHelper.cos((double)modelPart.pitch) * 10.0f;
        }
        this.head.originY = i;
        this.head.originZ = j;
        this.head.originX = h;
        EnderDragonFrameTracker.Frame frame3 = enderDragonEntityRenderState.getLerpedFrame(0);
        this.head.yaw = MathHelper.wrapDegrees((float)(frame3.yRot() - frame.yRot())) * ((float)Math.PI / 180);
        this.head.pitch = MathHelper.wrapDegrees((float)enderDragonEntityRenderState.getNeckPartPitchOffset(6, frame, frame3)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
        this.head.roll = -MathHelper.wrapDegrees((float)(frame3.yRot() - m)) * ((float)Math.PI / 180);
        this.body.roll = -l * 1.5f * ((float)Math.PI / 180);
        this.leftWing.pitch = 0.125f - MathHelper.cos((double)f) * 0.2f;
        this.leftWing.yaw = -0.25f;
        this.leftWing.roll = -(MathHelper.sin((double)f) + 0.125f) * 0.8f;
        this.leftWingTip.roll = (MathHelper.sin((double)(f + 2.0f)) + 0.5f) * 0.75f;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.roll = -this.leftWing.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.setLegAngles(g, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftHindLeg, this.leftHindLegTip, this.leftHindFoot);
        this.setLegAngles(g, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightHindLeg, this.rightHindLegTip, this.rightHindFoot);
        float p = 0.0f;
        i = this.tailParts[0].originY;
        j = this.tailParts[0].originZ;
        h = this.tailParts[0].originX;
        frame = enderDragonEntityRenderState.getLerpedFrame(11);
        for (int q = 0; q < 12; ++q) {
            EnderDragonFrameTracker.Frame frame4 = enderDragonEntityRenderState.getLerpedFrame(12 + q);
            ModelPart modelPart2 = this.tailParts[q];
            modelPart2.yaw = (MathHelper.wrapDegrees((float)(frame4.yRot() - frame.yRot())) * 1.5f + 180.0f) * ((float)Math.PI / 180);
            modelPart2.pitch = (p += MathHelper.sin((double)((float)q * 0.45f + f)) * 0.05f) + (float)(frame4.y() - frame.y()) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            modelPart2.roll = MathHelper.wrapDegrees((float)(frame4.yRot() - m)) * ((float)Math.PI / 180) * 1.5f;
            modelPart2.originY = i;
            modelPart2.originZ = j;
            modelPart2.originX = h;
            i += MathHelper.sin((double)modelPart2.pitch) * 10.0f;
            j -= MathHelper.cos((double)modelPart2.yaw) * MathHelper.cos((double)modelPart2.pitch) * 10.0f;
            h -= MathHelper.sin((double)modelPart2.yaw) * MathHelper.cos((double)modelPart2.pitch) * 10.0f;
        }
    }

    private void setLegAngles(float offset, ModelPart frontLeg, ModelPart frontLegTip, ModelPart frontFoot, ModelPart hindLeg, ModelPart hindLegTip, ModelPart hindFoot) {
        hindLeg.pitch = 1.0f + offset * 0.1f;
        hindLegTip.pitch = 0.5f + offset * 0.1f;
        hindFoot.pitch = 0.75f + offset * 0.1f;
        frontLeg.pitch = 1.3f + offset * 0.1f;
        frontLegTip.pitch = -0.5f - offset * 0.1f;
        frontFoot.pitch = 0.75f + offset * 0.1f;
    }
}

