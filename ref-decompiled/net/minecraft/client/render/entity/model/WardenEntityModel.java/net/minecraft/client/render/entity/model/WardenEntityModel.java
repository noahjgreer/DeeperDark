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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.WardenAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WardenEntityModel
extends EntityModel<WardenEntityRenderState> {
    private static final float field_38324 = 13.0f;
    private static final float field_38325 = 1.0f;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart leftRibcage;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;
    protected final ModelPart rightRibcage;
    private final Animation attackingAnimation;
    private final Animation chargingSonicBoomAnimation;
    private final Animation diggingAnimation;
    private final Animation emergingAnimation;
    private final Animation roaringAnimation;
    private final Animation sniffingAnimation;

    public WardenEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityCutoutNoCull);
        this.bone = modelPart.getChild("bone");
        this.body = this.bone.getChild("body");
        this.head = this.body.getChild("head");
        this.rightLeg = this.bone.getChild("right_leg");
        this.leftLeg = this.bone.getChild("left_leg");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
        this.rightRibcage = this.body.getChild("right_ribcage");
        this.leftRibcage = this.body.getChild("left_ribcage");
        this.attackingAnimation = WardenAnimations.ATTACKING.createAnimation(modelPart);
        this.chargingSonicBoomAnimation = WardenAnimations.CHARGING_SONIC_BOOM.createAnimation(modelPart);
        this.diggingAnimation = WardenAnimations.DIGGING.createAnimation(modelPart);
        this.emergingAnimation = WardenAnimations.EMERGING.createAnimation(modelPart);
        this.roaringAnimation = WardenAnimations.ROARING.createAnimation(modelPart);
        this.sniffingAnimation = WardenAnimations.SNIFFING.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), ModelTransform.origin(0.0f, -21.0f, 0.0f));
        modelPartData3.addChild("right_ribcage", ModelPartBuilder.create().uv(90, 11).cuboid(-2.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f), ModelTransform.origin(-7.0f, -2.0f, -4.0f));
        modelPartData3.addChild("left_ribcage", ModelPartBuilder.create().uv(90, 11).mirrored().cuboid(-7.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f).mirrored(false), ModelTransform.origin(7.0f, -2.0f, -4.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f), ModelTransform.origin(0.0f, -13.0f, 0.0f));
        modelPartData4.addChild("right_tendril", ModelPartBuilder.create().uv(52, 32).cuboid(-16.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.origin(-8.0f, -12.0f, 0.0f));
        modelPartData4.addChild("left_tendril", ModelPartBuilder.create().uv(58, 0).cuboid(0.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.origin(8.0f, -12.0f, 0.0f));
        modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(44, 50).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.origin(-13.0f, -13.0f, 1.0f));
        modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(0, 58).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.origin(13.0f, -13.0f, 1.0f));
        modelPartData2.addChild("right_leg", ModelPartBuilder.create().uv(76, 48).cuboid(-3.1f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.origin(-5.9f, -13.0f, 0.0f));
        modelPartData2.addChild("left_leg", ModelPartBuilder.create().uv(76, 76).cuboid(-2.9f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.origin(5.9f, -13.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    public static TexturedModelData getTendrilsTexturedModelData() {
        return WardenEntityModel.getTexturedModelData().transform(modelData -> {
            modelData.getRoot().resetChildrenExceptExact(Set.of("left_tendril", "right_tendril"));
            return modelData;
        });
    }

    public static TexturedModelData getHeartTexturedModelData() {
        return WardenEntityModel.getTexturedModelData().transform(modelData -> {
            modelData.getRoot().resetChildrenExceptExact(Set.of("body"));
            return modelData;
        });
    }

    public static TexturedModelData getBioluminescentTexturedModelData() {
        return WardenEntityModel.getTexturedModelData().transform(modelData -> {
            modelData.getRoot().resetChildrenExceptExact(Set.of("head", "left_arm", "right_arm", "left_leg", "right_leg"));
            return modelData;
        });
    }

    public static TexturedModelData getPulsatingSpotsTexturedModelData() {
        return WardenEntityModel.getTexturedModelData().transform(modelData -> {
            modelData.getRoot().resetChildrenExceptExact(Set.of("body", "head", "left_arm", "right_arm", "left_leg", "right_leg"));
            return modelData;
        });
    }

    @Override
    public void setAngles(WardenEntityRenderState wardenEntityRenderState) {
        super.setAngles(wardenEntityRenderState);
        this.setHeadAngle(wardenEntityRenderState.relativeHeadYaw, wardenEntityRenderState.pitch);
        this.setLimbAngles(wardenEntityRenderState.limbSwingAnimationProgress, wardenEntityRenderState.limbSwingAmplitude);
        this.setHeadAndBodyAngles(wardenEntityRenderState.age);
        this.setTendrilPitches(wardenEntityRenderState, wardenEntityRenderState.age);
        this.attackingAnimation.apply(wardenEntityRenderState.attackingAnimationState, wardenEntityRenderState.age);
        this.chargingSonicBoomAnimation.apply(wardenEntityRenderState.chargingSonicBoomAnimationState, wardenEntityRenderState.age);
        this.diggingAnimation.apply(wardenEntityRenderState.diggingAnimationState, wardenEntityRenderState.age);
        this.emergingAnimation.apply(wardenEntityRenderState.emergingAnimationState, wardenEntityRenderState.age);
        this.roaringAnimation.apply(wardenEntityRenderState.roaringAnimationState, wardenEntityRenderState.age);
        this.sniffingAnimation.apply(wardenEntityRenderState.sniffingAnimationState, wardenEntityRenderState.age);
    }

    private void setHeadAngle(float yaw, float pitch) {
        this.head.pitch = pitch * ((float)Math.PI / 180);
        this.head.yaw = yaw * ((float)Math.PI / 180);
    }

    private void setHeadAndBodyAngles(float animationProgress) {
        float f = animationProgress * 0.1f;
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f);
        this.head.roll += 0.06f * g;
        this.head.pitch += 0.06f * h;
        this.body.roll += 0.025f * h;
        this.body.pitch += 0.025f * g;
    }

    private void setLimbAngles(float angle, float distance) {
        float f = Math.min(0.5f, 3.0f * distance);
        float g = angle * 0.8662f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = Math.min(0.35f, f);
        this.head.roll += 0.3f * i * f;
        this.head.pitch += 1.2f * MathHelper.cos(g + 1.5707964f) * j;
        this.body.roll = 0.1f * i * f;
        this.body.pitch = 1.0f * h * j;
        this.leftLeg.pitch = 1.0f * h * f;
        this.rightLeg.pitch = 1.0f * MathHelper.cos(g + (float)Math.PI) * f;
        this.leftArm.pitch = -(0.8f * h * f);
        this.leftArm.roll = 0.0f;
        this.rightArm.pitch = -(0.8f * i * f);
        this.rightArm.roll = 0.0f;
        this.setArmPivots();
    }

    private void setArmPivots() {
        this.leftArm.yaw = 0.0f;
        this.leftArm.originZ = 1.0f;
        this.leftArm.originX = 13.0f;
        this.leftArm.originY = -13.0f;
        this.rightArm.yaw = 0.0f;
        this.rightArm.originZ = 1.0f;
        this.rightArm.originX = -13.0f;
        this.rightArm.originY = -13.0f;
    }

    private void setTendrilPitches(WardenEntityRenderState state, float animationProgress) {
        float f;
        this.leftTendril.pitch = f = state.tendrilAlpha * (float)(Math.cos((double)animationProgress * 2.25) * Math.PI * (double)0.1f);
        this.rightTendril.pitch = -f;
    }
}
