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
 *  net.minecraft.client.render.entity.animation.Animation
 *  net.minecraft.client.render.entity.animation.SnifferAnimations
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.SnifferEntityModel
 *  net.minecraft.client.render.entity.state.SnifferEntityRenderState
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
import net.minecraft.client.render.entity.animation.SnifferAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.SnifferEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class SnifferEntityModel
extends EntityModel<SnifferEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private static final float LIMB_ANGLE_SCALE = 9.0f;
    private static final float LIMB_DISTANCE_SCALE = 100.0f;
    private final ModelPart head;
    private final Animation searchingAnimation;
    private final Animation walkingAnimation;
    private final Animation diggingAnimation;
    private final Animation sniffingAnimation;
    private final Animation risingAnimation;
    private final Animation feelingHappyAnimation;
    private final Animation scentingAnimation;
    private final Animation babyGrowthAnimation;

    public SnifferEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("bone").getChild("body").getChild("head");
        this.searchingAnimation = SnifferAnimations.SEARCHING.createAnimation(modelPart);
        this.walkingAnimation = SnifferAnimations.WALKING.createAnimation(modelPart);
        this.diggingAnimation = SnifferAnimations.DIGGING.createAnimation(modelPart);
        this.sniffingAnimation = SnifferAnimations.SNIFFING.createAnimation(modelPart);
        this.risingAnimation = SnifferAnimations.RISING.createAnimation(modelPart);
        this.feelingHappyAnimation = SnifferAnimations.FEELING_HAPPY.createAnimation(modelPart);
        this.scentingAnimation = SnifferAnimations.SCENTING.createAnimation(modelPart);
        this.babyGrowthAnimation = SnifferAnimations.BABY_GROWTH.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)5.0f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("body", ModelPartBuilder.create().uv(62, 68).cuboid(-12.5f, -14.0f, -20.0f, 25.0f, 29.0f, 40.0f, new Dilation(0.0f)).uv(62, 0).cuboid(-12.5f, -14.0f, -20.0f, 25.0f, 24.0f, 40.0f, new Dilation(0.5f)).uv(87, 68).cuboid(-12.5f, 12.0f, -20.0f, 25.0f, 0.0f, 40.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        modelPartData2.addChild("right_front_leg", ModelPartBuilder.create().uv(32, 87).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)-7.5f, (float)10.0f, (float)-15.0f));
        modelPartData2.addChild("right_mid_leg", ModelPartBuilder.create().uv(32, 105).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)-7.5f, (float)10.0f, (float)0.0f));
        modelPartData2.addChild("right_hind_leg", ModelPartBuilder.create().uv(32, 123).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)-7.5f, (float)10.0f, (float)15.0f));
        modelPartData2.addChild("left_front_leg", ModelPartBuilder.create().uv(0, 87).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)7.5f, (float)10.0f, (float)-15.0f));
        modelPartData2.addChild("left_mid_leg", ModelPartBuilder.create().uv(0, 105).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)7.5f, (float)10.0f, (float)0.0f));
        modelPartData2.addChild("left_hind_leg", ModelPartBuilder.create().uv(0, 123).cuboid(-3.5f, -1.0f, -4.0f, 7.0f, 10.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)7.5f, (float)10.0f, (float)15.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(8, 15).cuboid(-6.5f, -7.5f, -11.5f, 13.0f, 18.0f, 11.0f, new Dilation(0.0f)).uv(8, 4).cuboid(-6.5f, 7.5f, -11.5f, 13.0f, 0.0f, 11.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)6.5f, (float)-19.48f));
        modelPartData4.addChild("left_ear", ModelPartBuilder.create().uv(2, 0).cuboid(0.0f, 0.0f, -3.0f, 1.0f, 19.0f, 7.0f, new Dilation(0.0f)), ModelTransform.origin((float)6.51f, (float)-7.5f, (float)-4.51f));
        modelPartData4.addChild("right_ear", ModelPartBuilder.create().uv(48, 0).cuboid(-1.0f, 0.0f, -3.0f, 1.0f, 19.0f, 7.0f, new Dilation(0.0f)), ModelTransform.origin((float)-6.51f, (float)-7.5f, (float)-4.51f));
        modelPartData4.addChild("nose", ModelPartBuilder.create().uv(10, 45).cuboid(-6.5f, -2.0f, -9.0f, 13.0f, 2.0f, 9.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)-4.5f, (float)-11.5f));
        modelPartData4.addChild("lower_beak", ModelPartBuilder.create().uv(10, 57).cuboid(-6.5f, -7.0f, -8.0f, 13.0f, 12.0f, 9.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)2.5f, (float)-12.5f));
        return TexturedModelData.of((ModelData)modelData, (int)192, (int)192);
    }

    public void setAngles(SnifferEntityRenderState snifferEntityRenderState) {
        super.setAngles((Object)snifferEntityRenderState);
        this.head.pitch = snifferEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = snifferEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        if (snifferEntityRenderState.searching) {
            this.searchingAnimation.applyWalking(snifferEntityRenderState.limbSwingAnimationProgress, snifferEntityRenderState.limbSwingAmplitude, 9.0f, 100.0f);
        } else {
            this.walkingAnimation.applyWalking(snifferEntityRenderState.limbSwingAnimationProgress, snifferEntityRenderState.limbSwingAmplitude, 9.0f, 100.0f);
        }
        this.diggingAnimation.apply(snifferEntityRenderState.diggingAnimationState, snifferEntityRenderState.age);
        this.sniffingAnimation.apply(snifferEntityRenderState.sniffingAnimationState, snifferEntityRenderState.age);
        this.risingAnimation.apply(snifferEntityRenderState.risingAnimationState, snifferEntityRenderState.age);
        this.feelingHappyAnimation.apply(snifferEntityRenderState.feelingHappyAnimationState, snifferEntityRenderState.age);
        this.scentingAnimation.apply(snifferEntityRenderState.scentingAnimationState, snifferEntityRenderState.age);
        if (snifferEntityRenderState.baby) {
            this.babyGrowthAnimation.applyStatic();
        }
    }
}

