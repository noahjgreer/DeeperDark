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
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityModel
extends EntityModel<IronGolemEntityRenderState> {
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public IronGolemEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightArm = modelPart.getChild("right_arm");
        this.leftArm = modelPart.getChild("left_arm");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f).uv(24, 0).cuboid(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f), ModelTransform.origin(0.0f, -7.0f, -2.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 40).cuboid(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f).uv(0, 70).cuboid(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, new Dilation(0.5f)), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(60, 21).cuboid(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(60, 58).cuboid(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), ModelTransform.origin(0.0f, -7.0f, 0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(37, 0).cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), ModelTransform.origin(-4.0f, 11.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(60, 0).mirrored().cuboid(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), ModelTransform.origin(5.0f, 11.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(IronGolemEntityRenderState ironGolemEntityRenderState) {
        super.setAngles(ironGolemEntityRenderState);
        float f = ironGolemEntityRenderState.attackTicksLeft;
        float g = ironGolemEntityRenderState.limbSwingAmplitude;
        float h = ironGolemEntityRenderState.limbSwingAnimationProgress;
        if (f > 0.0f) {
            this.rightArm.pitch = -2.0f + 1.5f * MathHelper.wrap(f, 10.0f);
            this.leftArm.pitch = -2.0f + 1.5f * MathHelper.wrap(f, 10.0f);
        } else {
            int i = ironGolemEntityRenderState.lookingAtVillagerTicks;
            if (i > 0) {
                this.rightArm.pitch = -0.8f + 0.025f * MathHelper.wrap(i, 70.0f);
                this.leftArm.pitch = 0.0f;
            } else {
                this.rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(h, 13.0f)) * g;
                this.leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(h, 13.0f)) * g;
            }
        }
        this.head.yaw = ironGolemEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = ironGolemEntityRenderState.pitch * ((float)Math.PI / 180);
        this.rightLeg.pitch = -1.5f * MathHelper.wrap(h, 13.0f) * g;
        this.leftLeg.pitch = 1.5f * MathHelper.wrap(h, 13.0f) * g;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    public ModelPart getRightArm() {
        return this.rightArm;
    }
}
