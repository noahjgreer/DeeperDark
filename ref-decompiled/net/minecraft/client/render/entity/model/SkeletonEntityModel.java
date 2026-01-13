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
 *  net.minecraft.client.render.entity.model.ArmPosing
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.SkeletonEntityModel
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
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
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SkeletonEntityModel<S extends SkeletonEntityRenderState>
extends BipedEntityModel<S> {
    public SkeletonEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)Dilation.NONE, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        SkeletonEntityModel.addLimbs((ModelPartData)modelPartData);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    protected static void addLimbs(ModelPartData data) {
        data.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin((float)-5.0f, (float)2.0f, (float)0.0f));
        data.addChild("left_arm", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin((float)5.0f, (float)2.0f, (float)0.0f));
        data.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin((float)-2.0f, (float)12.0f, (float)0.0f));
        data.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.origin((float)2.0f, (float)12.0f, (float)0.0f));
    }

    public static TexturedModelData getParchedTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f).uv(28, 0).cuboid(-4.0f, 10.0f, -2.0f, 8.0f, 1.0f, 4.0f).uv(16, 48).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, new Dilation(0.025f)), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f).uv(0, 32).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.2f)), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f)).addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f).uv(42, 33).cuboid(-1.55f, -2.025f, -1.5f, 3.0f, 12.0f, 3.0f), ModelTransform.origin((float)-5.5f, (float)2.0f, (float)0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(56, 16).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f).uv(40, 48).cuboid(-1.45f, -2.025f, -1.5f, 3.0f, 12.0f, 3.0f), ModelTransform.origin((float)5.5f, (float)2.0f, (float)0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f).uv(0, 49).cuboid(-1.5f, -0.0f, -1.5f, 3.0f, 12.0f, 3.0f), ModelTransform.origin((float)-2.0f, (float)12.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f).uv(4, 49).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 12.0f, 3.0f), ModelTransform.origin((float)2.0f, (float)12.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(S skeletonEntityRenderState) {
        super.setAngles(skeletonEntityRenderState);
        if (((SkeletonEntityRenderState)skeletonEntityRenderState).attacking && !((SkeletonEntityRenderState)skeletonEntityRenderState).holdingBow) {
            float f = ((SkeletonEntityRenderState)skeletonEntityRenderState).handSwingProgress;
            float g = MathHelper.sin((double)(f * (float)Math.PI));
            float h = MathHelper.sin((double)((1.0f - (1.0f - f) * (1.0f - f)) * (float)Math.PI));
            this.rightArm.roll = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - g * 0.6f);
            this.leftArm.yaw = 0.1f - g * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            this.rightArm.pitch -= g * 1.2f - h * 0.4f;
            this.leftArm.pitch -= g * 1.2f - h * 0.4f;
            ArmPosing.swingArms((ModelPart)this.rightArm, (ModelPart)this.leftArm, (float)((SkeletonEntityRenderState)skeletonEntityRenderState).age);
        }
    }

    public void setArmAngle(SkeletonEntityRenderState skeletonEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.getRootPart().applyTransform(matrixStack);
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        ModelPart modelPart = this.getArm(arm);
        modelPart.originX += f;
        modelPart.applyTransform(matrixStack);
        modelPart.originX -= f;
    }
}

