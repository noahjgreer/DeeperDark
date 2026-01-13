/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel
extends EntityModel<HoglinEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f, Set.of("head"));
    private static final float HEAD_PITCH_START = 0.87266463f;
    private static final float HEAD_PITCH_END = -0.34906584f;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart mane;

    public HoglinEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild("body");
        this.mane = this.body.getChild("mane");
        this.head = modelPart.getChild("head");
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
    }

    private static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(1, 1).cuboid(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f), ModelTransform.origin(0.0f, 7.0f, 0.0f));
        modelPartData2.addChild("mane", ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.origin(0.0f, -14.0f, -7.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("head", ModelPartBuilder.create().uv(61, 1).cuboid(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f), ModelTransform.of(0.0f, 2.0f, -12.0f, 0.87266463f, 0.0f, 0.0f));
        modelPartData3.addChild("right_ear", ModelPartBuilder.create().uv(1, 1).cuboid(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(-6.0f, -2.0f, -3.0f, 0.0f, 0.0f, -0.6981317f));
        modelPartData3.addChild("left_ear", ModelPartBuilder.create().uv(1, 6).cuboid(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(6.0f, -2.0f, -3.0f, 0.0f, 0.0f, 0.6981317f));
        modelPartData3.addChild("right_horn", ModelPartBuilder.create().uv(10, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.origin(-7.0f, 2.0f, -12.0f));
        modelPartData3.addChild("left_horn", ModelPartBuilder.create().uv(1, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.origin(7.0f, 2.0f, -12.0f));
        int i = 14;
        int j = 11;
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(66, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.origin(-4.0f, 10.0f, -8.5f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(41, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.origin(4.0f, 10.0f, -8.5f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(21, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.origin(-5.0f, 13.0f, 10.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(0, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.origin(5.0f, 13.0f, 10.0f));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = HoglinEntityModel.getModelData();
        return TexturedModelData.of(modelData, 128, 64);
    }

    public static TexturedModelData getBabyTexturedModelData() {
        ModelData modelData = HoglinEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot().getChild("body");
        modelPartData.addChild("mane", ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.origin(0.0f, -14.0f, -3.0f));
        return TexturedModelData.of(modelData, 128, 64).transform(BABY_TRANSFORMER);
    }

    @Override
    public void setAngles(HoglinEntityRenderState hoglinEntityRenderState) {
        super.setAngles(hoglinEntityRenderState);
        float f = hoglinEntityRenderState.limbSwingAmplitude;
        float g = hoglinEntityRenderState.limbSwingAnimationProgress;
        this.rightEar.roll = -0.6981317f - f * MathHelper.sin(g);
        this.leftEar.roll = 0.6981317f + f * MathHelper.sin(g);
        this.head.yaw = hoglinEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float h = 1.0f - (float)MathHelper.abs(10 - 2 * hoglinEntityRenderState.movementCooldownTicks) / 10.0f;
        this.head.pitch = MathHelper.lerp(h, 0.87266463f, -0.34906584f);
        if (hoglinEntityRenderState.baby) {
            this.head.originY += h * 2.5f;
        }
        float i = 1.2f;
        this.rightFrontLeg.pitch = MathHelper.cos(g) * 1.2f * f;
        this.rightHindLeg.pitch = this.leftFrontLeg.pitch = MathHelper.cos(g + (float)Math.PI) * 1.2f * f;
        this.leftHindLeg.pitch = this.rightFrontLeg.pitch;
    }
}
