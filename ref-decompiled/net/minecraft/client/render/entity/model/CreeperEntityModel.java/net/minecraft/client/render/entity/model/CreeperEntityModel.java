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
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CreeperEntityModel
extends EntityModel<CreeperEntityRenderState> {
    private final ModelPart head;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private static final int HEAD_AND_BODY_Y_PIVOT = 6;

    public CreeperEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.origin(0.0f, 6.0f, 0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(0.0f, 6.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin(-2.0f, 18.0f, 4.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin(2.0f, 18.0f, 4.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin(-2.0f, 18.0f, -4.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin(2.0f, 18.0f, -4.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(CreeperEntityRenderState creeperEntityRenderState) {
        super.setAngles(creeperEntityRenderState);
        this.head.yaw = creeperEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = creeperEntityRenderState.pitch * ((float)Math.PI / 180);
        float f = creeperEntityRenderState.limbSwingAmplitude;
        float g = creeperEntityRenderState.limbSwingAnimationProgress;
        this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
        this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662f + (float)Math.PI) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662f) * 1.4f * f;
    }
}
