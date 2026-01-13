/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.animation.Animation
 *  net.minecraft.client.render.entity.animation.CamelAnimations
 *  net.minecraft.client.render.entity.model.CamelEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.CamelEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.CamelAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CamelEntityModel
extends EntityModel<CamelEntityRenderState> {
    private static final float LIMB_ANGLE_SCALE = 2.0f;
    private static final float LIMB_DISTANCE_SCALE = 2.5f;
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.45f);
    protected final ModelPart head;
    private final Animation walkingAnimation;
    private final Animation sittingTransitionAnimation;
    private final Animation sittingAnimation;
    private final Animation standingTransitionAnimation;
    private final Animation idlingAnimation;
    private final Animation dashingAnimation;

    public CamelEntityModel(ModelPart modelPart) {
        super(modelPart);
        ModelPart modelPart2 = modelPart.getChild("body");
        this.head = modelPart2.getChild("head");
        this.walkingAnimation = CamelAnimations.WALKING.createAnimation(modelPart);
        this.sittingTransitionAnimation = CamelAnimations.SITTING_TRANSITION.createAnimation(modelPart);
        this.sittingAnimation = CamelAnimations.SITTING.createAnimation(modelPart);
        this.standingTransitionAnimation = CamelAnimations.STANDING_TRANSITION.createAnimation(modelPart);
        this.idlingAnimation = CamelAnimations.IDLING.createAnimation(modelPart);
        this.dashingAnimation = CamelAnimations.DASHING.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of((ModelData)CamelEntityModel.getModelData(), (int)128, (int)128);
    }

    protected static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 25).cuboid(-7.5f, -12.0f, -23.5f, 15.0f, 12.0f, 27.0f), ModelTransform.origin((float)0.0f, (float)4.0f, (float)9.5f));
        modelPartData2.addChild("hump", ModelPartBuilder.create().uv(74, 0).cuboid(-4.5f, -5.0f, -5.5f, 9.0f, 5.0f, 11.0f), ModelTransform.origin((float)0.0f, (float)-12.0f, (float)-10.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(122, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 0.0f), ModelTransform.origin((float)0.0f, (float)-9.0f, (float)3.5f));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(60, 24).cuboid(-3.5f, -7.0f, -15.0f, 7.0f, 8.0f, 19.0f).uv(21, 0).cuboid(-3.5f, -21.0f, -15.0f, 7.0f, 14.0f, 7.0f).uv(50, 0).cuboid(-2.5f, -21.0f, -21.0f, 5.0f, 5.0f, 6.0f), ModelTransform.origin((float)0.0f, (float)-3.0f, (float)-19.5f));
        modelPartData3.addChild("left_ear", ModelPartBuilder.create().uv(45, 0).cuboid(-0.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.origin((float)2.5f, (float)-21.0f, (float)-9.5f));
        modelPartData3.addChild("right_ear", ModelPartBuilder.create().uv(67, 0).cuboid(-2.5f, 0.5f, -1.0f, 3.0f, 1.0f, 2.0f), ModelTransform.origin((float)-2.5f, (float)-21.0f, (float)-9.5f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(58, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.origin((float)4.9f, (float)1.0f, (float)9.5f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(94, 16).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.origin((float)-4.9f, (float)1.0f, (float)9.5f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.origin((float)4.9f, (float)1.0f, (float)-10.5f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(0, 26).cuboid(-2.5f, 2.0f, -2.5f, 5.0f, 21.0f, 5.0f), ModelTransform.origin((float)-4.9f, (float)1.0f, (float)-10.5f));
        return modelData;
    }

    public void setAngles(CamelEntityRenderState camelEntityRenderState) {
        super.setAngles((Object)camelEntityRenderState);
        this.setHeadAngles(camelEntityRenderState, camelEntityRenderState.relativeHeadYaw, camelEntityRenderState.pitch);
        this.walkingAnimation.applyWalking(camelEntityRenderState.limbSwingAnimationProgress, camelEntityRenderState.limbSwingAmplitude, 2.0f, 2.5f);
        this.sittingTransitionAnimation.apply(camelEntityRenderState.sittingTransitionAnimationState, camelEntityRenderState.age);
        this.sittingAnimation.apply(camelEntityRenderState.sittingAnimationState, camelEntityRenderState.age);
        this.standingTransitionAnimation.apply(camelEntityRenderState.standingTransitionAnimationState, camelEntityRenderState.age);
        this.idlingAnimation.apply(camelEntityRenderState.idlingAnimationState, camelEntityRenderState.age);
        this.dashingAnimation.apply(camelEntityRenderState.dashingAnimationState, camelEntityRenderState.age);
    }

    private void setHeadAngles(CamelEntityRenderState state, float headYaw, float headPitch) {
        headYaw = MathHelper.clamp((float)headYaw, (float)-30.0f, (float)30.0f);
        headPitch = MathHelper.clamp((float)headPitch, (float)-25.0f, (float)45.0f);
        if (state.jumpCooldown > 0.0f) {
            float f = 45.0f * state.jumpCooldown / 55.0f;
            headPitch = MathHelper.clamp((float)(headPitch + f), (float)-25.0f, (float)70.0f);
        }
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
    }
}

