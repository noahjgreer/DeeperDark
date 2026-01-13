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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.QuadrupedEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends LivingEntityRenderState>
extends EntityModel<T> {
    protected final ModelPart head;
    protected final ModelPart body;
    protected final ModelPart rightHindLeg;
    protected final ModelPart leftHindLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart leftFrontLeg;

    protected QuadrupedEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static ModelData getModelData(int stanceWidth, boolean leftMirrored, boolean rightMirrored, Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin((float)0.0f, (float)(18 - stanceWidth), (float)-6.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(28, 8).cuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, dilation), ModelTransform.of((float)0.0f, (float)(17 - stanceWidth), (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        QuadrupedEntityModel.addLegs((ModelPartData)modelPartData, (boolean)leftMirrored, (boolean)rightMirrored, (int)stanceWidth, (Dilation)dilation);
        return modelData;
    }

    static void addLegs(ModelPartData root, boolean leftMirrored, boolean rightMirrored, int stanceWidth, Dilation dilation) {
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().mirrored(rightMirrored).uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)stanceWidth, 4.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().mirrored(leftMirrored).uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)stanceWidth, 4.0f, dilation);
        root.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin((float)-3.0f, (float)(24 - stanceWidth), (float)7.0f));
        root.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.origin((float)3.0f, (float)(24 - stanceWidth), (float)7.0f));
        root.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin((float)-3.0f, (float)(24 - stanceWidth), (float)-5.0f));
        root.addChild("left_front_leg", modelPartBuilder2, ModelTransform.origin((float)3.0f, (float)(24 - stanceWidth), (float)-5.0f));
    }

    public void setAngles(T livingEntityRenderState) {
        super.setAngles(livingEntityRenderState);
        this.head.pitch = ((LivingEntityRenderState)livingEntityRenderState).pitch * ((float)Math.PI / 180);
        this.head.yaw = ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw * ((float)Math.PI / 180);
        float f = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress;
        float g = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude;
        this.rightHindLeg.pitch = MathHelper.cos((double)(f * 0.6662f)) * 1.4f * g;
        this.leftHindLeg.pitch = MathHelper.cos((double)(f * 0.6662f + (float)Math.PI)) * 1.4f * g;
        this.rightFrontLeg.pitch = MathHelper.cos((double)(f * 0.6662f + (float)Math.PI)) * 1.4f * g;
        this.leftFrontLeg.pitch = MathHelper.cos((double)(f * 0.6662f)) * 1.4f * g;
    }
}

