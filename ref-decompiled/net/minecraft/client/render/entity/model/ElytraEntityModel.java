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
 *  net.minecraft.client.render.entity.model.ElytraEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
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
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ElytraEntityModel
extends EntityModel<BipedEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ElytraEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftWing = modelPart.getChild("left_wing");
        this.rightWing = modelPart.getChild("right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(1.0f);
        modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(22, 0).cuboid(-10.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of((float)5.0f, (float)0.0f, (float)0.0f, (float)0.2617994f, (float)0.0f, (float)-0.2617994f));
        modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(22, 0).mirrored().cuboid(0.0f, 0.0f, 0.0f, 10.0f, 20.0f, 2.0f, dilation), ModelTransform.of((float)-5.0f, (float)0.0f, (float)0.0f, (float)0.2617994f, (float)0.0f, (float)0.2617994f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(BipedEntityRenderState bipedEntityRenderState) {
        super.setAngles((Object)bipedEntityRenderState);
        this.leftWing.originY = bipedEntityRenderState.isInSneakingPose ? 3.0f : 0.0f;
        this.leftWing.pitch = bipedEntityRenderState.leftWingPitch;
        this.leftWing.roll = bipedEntityRenderState.leftWingRoll;
        this.leftWing.yaw = bipedEntityRenderState.leftWingYaw;
        this.rightWing.yaw = -this.leftWing.yaw;
        this.rightWing.originY = this.leftWing.originY;
        this.rightWing.pitch = this.leftWing.pitch;
        this.rightWing.roll = -this.leftWing.roll;
    }
}

