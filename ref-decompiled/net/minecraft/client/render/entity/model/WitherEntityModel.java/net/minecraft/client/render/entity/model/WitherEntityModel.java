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
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel
extends EntityModel<WitherEntityRenderState> {
    private static final String RIBCAGE = "ribcage";
    private static final String CENTER_HEAD = "center_head";
    private static final String RIGHT_HEAD = "right_head";
    private static final String LEFT_HEAD = "left_head";
    private static final float RIBCAGE_PITCH_OFFSET = 0.065f;
    private static final float TAIL_PITCH_OFFSET = 0.265f;
    private final ModelPart centerHead;
    private final ModelPart rightHead;
    private final ModelPart leftHead;
    private final ModelPart ribcage;
    private final ModelPart tail;

    public WitherEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.ribcage = modelPart.getChild(RIBCAGE);
        this.tail = modelPart.getChild("tail");
        this.centerHead = modelPart.getChild(CENTER_HEAD);
        this.rightHead = modelPart.getChild(RIGHT_HEAD);
        this.leftHead = modelPart.getChild(LEFT_HEAD);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shoulders", ModelPartBuilder.create().uv(0, 16).cuboid(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, dilation), ModelTransform.NONE);
        float f = 0.20420352f;
        modelPartData.addChild(RIBCAGE, ModelPartBuilder.create().uv(0, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, dilation).uv(24, 22).cuboid(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation), ModelTransform.of(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(12, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, dilation), ModelTransform.of(-2.0f, 6.9f + MathHelper.cos(0.2042035162448883) * 10.0f, -0.5f + MathHelper.sin(0.2042035162448883) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        modelPartData.addChild(CENTER_HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, dilation);
        modelPartData.addChild(RIGHT_HEAD, modelPartBuilder, ModelTransform.origin(-8.0f, 4.0f, 0.0f));
        modelPartData.addChild(LEFT_HEAD, modelPartBuilder, ModelTransform.origin(10.0f, 4.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(WitherEntityRenderState witherEntityRenderState) {
        super.setAngles(witherEntityRenderState);
        WitherEntityModel.rotateHead(witherEntityRenderState, this.rightHead, 0);
        WitherEntityModel.rotateHead(witherEntityRenderState, this.leftHead, 1);
        float f = MathHelper.cos(witherEntityRenderState.age * 0.1f);
        this.ribcage.pitch = (0.065f + 0.05f * f) * (float)Math.PI;
        this.tail.setOrigin(-2.0f, 6.9f + MathHelper.cos(this.ribcage.pitch) * 10.0f, -0.5f + MathHelper.sin(this.ribcage.pitch) * 10.0f);
        this.tail.pitch = (0.265f + 0.1f * f) * (float)Math.PI;
        this.centerHead.yaw = witherEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.centerHead.pitch = witherEntityRenderState.pitch * ((float)Math.PI / 180);
    }

    private static void rotateHead(WitherEntityRenderState witherEntityRenderState, ModelPart head, int sigma) {
        head.yaw = (witherEntityRenderState.sideHeadYaws[sigma] - witherEntityRenderState.bodyYaw) * ((float)Math.PI / 180);
        head.pitch = witherEntityRenderState.sideHeadPitches[sigma] * ((float)Math.PI / 180);
    }
}
