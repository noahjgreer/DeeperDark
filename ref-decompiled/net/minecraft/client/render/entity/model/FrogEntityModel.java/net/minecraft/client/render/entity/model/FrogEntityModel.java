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
import net.minecraft.client.render.entity.animation.FrogAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class FrogEntityModel
extends EntityModel<FrogEntityRenderState> {
    private static final float WALKING_LIMB_ANGLE_SCALE = 1.5f;
    private static final float SWIMMING_LIMB_ANGLE_SCALE = 1.0f;
    private static final float LIMB_DISTANCE_SCALE = 2.5f;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart tongue;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart croakingBody;
    private final Animation longJumpingAnimation;
    private final Animation croakingAnimation;
    private final Animation usingTongueAnimation;
    private final Animation swimmingAnimation;
    private final Animation walkingAnimation;
    private final Animation idlingInWaterAnimation;

    public FrogEntityModel(ModelPart modelPart) {
        super(modelPart.getChild("root"));
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.tongue = this.body.getChild("tongue");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
        this.croakingBody = this.body.getChild("croaking_body");
        this.longJumpingAnimation = FrogAnimations.LONG_JUMPING.createAnimation(modelPart);
        this.croakingAnimation = FrogAnimations.CROAKING.createAnimation(modelPart);
        this.usingTongueAnimation = FrogAnimations.USING_TONGUE.createAnimation(modelPart);
        this.swimmingAnimation = FrogAnimations.SWIMMING.createAnimation(modelPart);
        this.walkingAnimation = FrogAnimations.WALKING.createAnimation(modelPart);
        this.idlingInWaterAnimation = FrogAnimations.IDLING_IN_WATER.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(3, 1).cuboid(-3.5f, -2.0f, -8.0f, 7.0f, 3.0f, 9.0f).uv(23, 22).cuboid(-3.5f, -1.0f, -8.0f, 7.0f, 0.0f, 9.0f), ModelTransform.origin(0.0f, -2.0f, 4.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(23, 13).cuboid(-3.5f, -1.0f, -7.0f, 7.0f, 0.0f, 9.0f).uv(0, 13).cuboid(-3.5f, -2.0f, -7.0f, 7.0f, 3.0f, 9.0f), ModelTransform.origin(0.0f, -2.0f, -1.0f));
        ModelPartData modelPartData5 = modelPartData4.addChild("eyes", ModelPartBuilder.create(), ModelTransform.origin(-0.5f, 0.0f, 2.0f));
        modelPartData5.addChild("right_eye", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(-1.5f, -3.0f, -6.5f));
        modelPartData5.addChild("left_eye", ModelPartBuilder.create().uv(0, 5).cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(2.5f, -3.0f, -6.5f));
        modelPartData3.addChild("croaking_body", ModelPartBuilder.create().uv(26, 5).cuboid(-3.5f, -0.1f, -2.9f, 7.0f, 2.0f, 3.0f, new Dilation(-0.1f)), ModelTransform.origin(0.0f, -1.0f, -5.0f));
        ModelPartData modelPartData6 = modelPartData3.addChild("tongue", ModelPartBuilder.create().uv(17, 13).cuboid(-2.0f, 0.0f, -7.1f, 4.0f, 0.0f, 7.0f), ModelTransform.origin(0.0f, -1.01f, 1.0f));
        ModelPartData modelPartData7 = modelPartData3.addChild("left_arm", ModelPartBuilder.create().uv(0, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.origin(4.0f, -1.0f, -6.5f));
        modelPartData7.addChild("left_hand", ModelPartBuilder.create().uv(18, 40).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(0.0f, 3.0f, -1.0f));
        ModelPartData modelPartData8 = modelPartData3.addChild("right_arm", ModelPartBuilder.create().uv(0, 38).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 3.0f), ModelTransform.origin(-4.0f, -1.0f, -6.5f));
        modelPartData8.addChild("right_hand", ModelPartBuilder.create().uv(2, 40).cuboid(-4.0f, 0.01f, -5.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(0.0f, 3.0f, 0.0f));
        ModelPartData modelPartData9 = modelPartData2.addChild("left_leg", ModelPartBuilder.create().uv(14, 25).cuboid(-1.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.origin(3.5f, -3.0f, 4.0f));
        modelPartData9.addChild("left_foot", ModelPartBuilder.create().uv(2, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(2.0f, 3.0f, 0.0f));
        ModelPartData modelPartData10 = modelPartData2.addChild("right_leg", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0f, 0.0f, -2.0f, 3.0f, 3.0f, 4.0f), ModelTransform.origin(-3.5f, -3.0f, 4.0f));
        modelPartData10.addChild("right_foot", ModelPartBuilder.create().uv(18, 32).cuboid(-4.0f, 0.01f, -4.0f, 8.0f, 0.0f, 8.0f), ModelTransform.origin(-2.0f, 3.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 48);
    }

    @Override
    public void setAngles(FrogEntityRenderState frogEntityRenderState) {
        super.setAngles(frogEntityRenderState);
        this.longJumpingAnimation.apply(frogEntityRenderState.longJumpingAnimationState, frogEntityRenderState.age);
        this.croakingAnimation.apply(frogEntityRenderState.croakingAnimationState, frogEntityRenderState.age);
        this.usingTongueAnimation.apply(frogEntityRenderState.usingTongueAnimationState, frogEntityRenderState.age);
        if (frogEntityRenderState.insideWaterOrBubbleColumn) {
            this.swimmingAnimation.applyWalking(frogEntityRenderState.limbSwingAnimationProgress, frogEntityRenderState.limbSwingAmplitude, 1.0f, 2.5f);
        } else {
            this.walkingAnimation.applyWalking(frogEntityRenderState.limbSwingAnimationProgress, frogEntityRenderState.limbSwingAmplitude, 1.5f, 2.5f);
        }
        this.idlingInWaterAnimation.apply(frogEntityRenderState.idlingInWaterAnimationState, frogEntityRenderState.age);
        this.croakingBody.visible = frogEntityRenderState.croakingAnimationState.isRunning();
    }
}
