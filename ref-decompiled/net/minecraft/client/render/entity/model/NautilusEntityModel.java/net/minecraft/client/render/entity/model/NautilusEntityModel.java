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
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.NautilusAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.NautilusEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class NautilusEntityModel
extends EntityModel<NautilusEntityRenderState> {
    private static final float field_63546 = 2.0f;
    private static final float field_63547 = 3.0f;
    private static final float field_63548 = 0.2f;
    private static final float field_63549 = 5.0f;
    protected final ModelPart body;
    protected final ModelPart nautilusRoot;
    private final Animation animation;

    public NautilusEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.nautilusRoot = modelPart.getChild("root");
        this.body = this.nautilusRoot.getChild("body");
        this.animation = NautilusAnimations.ANIMATION.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(NautilusEntityModel.getModelData(), 128, 128);
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 29.0f, -6.0f));
        modelPartData2.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0f, -10.0f, -7.0f, 14.0f, 10.0f, 16.0f, new Dilation(0.0f)).uv(0, 26).cuboid(-7.0f, 0.0f, -7.0f, 14.0f, 8.0f, 20.0f, new Dilation(0.0f)).uv(48, 26).cuboid(-7.0f, 0.0f, 6.0f, 14.0f, 8.0f, 0.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -13.0f, 5.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 54).cuboid(-5.0f, -4.51f, -3.0f, 10.0f, 8.0f, 14.0f, new Dilation(0.0f)).uv(0, 76).cuboid(-5.0f, -4.51f, 7.0f, 10.0f, 8.0f, 0.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -8.5f, 12.3f));
        modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(54, 54).cuboid(-5.0f, -2.0f, 0.0f, 10.0f, 4.0f, 4.0f, new Dilation(-0.001f)), ModelTransform.origin(0.0f, -2.51f, 7.0f));
        modelPartData3.addChild("inner_mouth", ModelPartBuilder.create().uv(54, 70).cuboid(-3.0f, -2.0f, -0.5f, 6.0f, 4.0f, 4.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -0.51f, 7.5f));
        modelPartData3.addChild("lower_mouth", ModelPartBuilder.create().uv(54, 62).cuboid(-5.0f, -1.98f, 0.0f, 10.0f, 4.0f, 4.0f, new Dilation(-0.001f)), ModelTransform.origin(0.0f, 1.49f, 7.0f));
        return modelData;
    }

    public static TexturedModelData getBabyTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(-0.5f, 28.0f, -0.5f));
        modelPartData2.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -4.0f, -1.0f, 7.0f, 4.0f, 7.0f, new Dilation(0.0f)).uv(0, 11).cuboid(-6.0f, 0.0f, -1.0f, 7.0f, 4.0f, 9.0f, new Dilation(0.0f)).uv(23, 11).cuboid(-6.0f, 0.0f, 5.0f, 7.0f, 4.0f, 0.0f, new Dilation(0.0f)), ModelTransform.origin(3.0f, -8.0f, -2.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 24).cuboid(-2.5f, -3.01f, -1.0f, 5.0f, 4.0f, 7.0f, new Dilation(0.0f)).uv(0, 35).cuboid(-2.5f, -3.01f, 4.1f, 5.0f, 4.0f, 0.0f, new Dilation(0.0f)), ModelTransform.origin(0.5f, -5.0f, 3.0f));
        modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(24, 24).cuboid(-2.5f, -1.0f, 0.0f, 5.0f, 2.0f, 2.0f, new Dilation(-0.001f)), ModelTransform.origin(0.0f, -2.01f, 3.9f));
        modelPartData3.addChild("inner_mouth", ModelPartBuilder.create().uv(24, 32).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 2.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -1.01f, 4.9f));
        modelPartData3.addChild("lower_mouth", ModelPartBuilder.create().uv(24, 28).cuboid(-2.5f, -1.0f, 0.0f, 5.0f, 2.0f, 2.0f, new Dilation(-0.001f)), ModelTransform.origin(0.0f, -0.01f, 3.9f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(NautilusEntityRenderState nautilusEntityRenderState) {
        super.setAngles(nautilusEntityRenderState);
        this.setHeadAngles(nautilusEntityRenderState.relativeHeadYaw, nautilusEntityRenderState.pitch);
        this.animation.applyWalking(nautilusEntityRenderState.limbSwingAnimationProgress + nautilusEntityRenderState.age / 5.0f, nautilusEntityRenderState.limbSwingAmplitude + 0.2f, 2.0f, 3.0f);
    }

    private void setHeadAngles(float yaw, float pitch) {
        yaw = MathHelper.clamp(yaw, -10.0f, 10.0f);
        pitch = MathHelper.clamp(pitch, -10.0f, 10.0f);
        this.body.yaw = yaw * ((float)Math.PI / 180);
        this.body.pitch = pitch * ((float)Math.PI / 180);
    }
}
