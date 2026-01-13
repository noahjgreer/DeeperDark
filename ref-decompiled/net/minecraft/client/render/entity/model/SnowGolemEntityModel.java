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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.SnowGolemEntityModel
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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityModel
extends EntityModel<LivingEntityRenderState> {
    private static final String UPPER_BODY = "upper_body";
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public SnowGolemEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.leftArm = modelPart.getChild("left_arm");
        this.rightArm = modelPart.getChild("right_arm");
        this.upperBody = modelPart.getChild(UPPER_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 4.0f;
        Dilation dilation = new Dilation(-0.5f);
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin((float)0.0f, (float)4.0f, (float)0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, dilation);
        modelPartData.addChild("left_arm", modelPartBuilder, ModelTransform.of((float)5.0f, (float)6.0f, (float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f));
        modelPartData.addChild("right_arm", modelPartBuilder, ModelTransform.of((float)-5.0f, (float)6.0f, (float)-1.0f, (float)0.0f, (float)((float)Math.PI), (float)-1.0f));
        modelPartData.addChild(UPPER_BODY, ModelPartBuilder.create().uv(0, 16).cuboid(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, dilation), ModelTransform.origin((float)0.0f, (float)13.0f, (float)0.0f));
        modelPartData.addChild("lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, dilation), ModelTransform.origin((float)0.0f, (float)24.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles((Object)livingEntityRenderState);
        this.head.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = livingEntityRenderState.pitch * ((float)Math.PI / 180);
        this.upperBody.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin((double)this.upperBody.yaw);
        float g = MathHelper.cos((double)this.upperBody.yaw);
        this.leftArm.yaw = this.upperBody.yaw;
        this.rightArm.yaw = this.upperBody.yaw + (float)Math.PI;
        this.leftArm.originX = g * 5.0f;
        this.leftArm.originZ = -f * 5.0f;
        this.rightArm.originX = -g * 5.0f;
        this.rightArm.originZ = f * 5.0f;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

