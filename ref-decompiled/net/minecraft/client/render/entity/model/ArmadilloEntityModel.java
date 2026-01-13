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
 *  net.minecraft.client.render.entity.animation.ArmadilloAnimations
 *  net.minecraft.client.render.entity.model.ArmadilloEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.ArmadilloEntityRenderState
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.animation.ArmadilloAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityModel
extends EntityModel<ArmadilloEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.6f);
    private static final float field_47860 = 25.0f;
    private static final float field_47861 = 22.5f;
    private static final float field_47862 = 16.5f;
    private static final float field_47863 = 2.5f;
    private static final String HEAD_CUBE = "head_cube";
    private static final String RIGHT_EAR_CUBE = "right_ear_cube";
    private static final String LEFT_EAR_CUBE = "left_ear_cube";
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart cube;
    private final ModelPart head;
    private final ModelPart tail;
    private final Animation walkingAnimation;
    private final Animation unrollingAnimation;
    private final Animation rollingAnimation;
    private final Animation scaredAnimation;

    public ArmadilloEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild("body");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
        this.cube = modelPart.getChild("cube");
        this.walkingAnimation = ArmadilloAnimations.WALKING.createAnimation(modelPart);
        this.unrollingAnimation = ArmadilloAnimations.UNROLLING.createAnimation(modelPart);
        this.rollingAnimation = ArmadilloAnimations.ROLLING.createAnimation(modelPart);
        this.scaredAnimation = ArmadilloAnimations.SCARED.createAnimation(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 20).cuboid(-4.0f, -7.0f, -10.0f, 8.0f, 8.0f, 12.0f, new Dilation(0.3f)).uv(0, 40).cuboid(-4.0f, -7.0f, -10.0f, 8.0f, 8.0f, 12.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)21.0f, (float)4.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(44, 53).cuboid(-0.5f, -0.0865f, 0.0933f, 1.0f, 6.0f, 1.0f, new Dilation(0.0f)), ModelTransform.of((float)0.0f, (float)-3.0f, (float)1.0f, (float)0.5061f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)-2.0f, (float)-11.0f));
        modelPartData3.addChild(HEAD_CUBE, ModelPartBuilder.create().uv(43, 15).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 5.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)-0.3927f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("right_ear", ModelPartBuilder.create(), ModelTransform.origin((float)-1.0f, (float)-1.0f, (float)0.0f));
        modelPartData4.addChild(RIGHT_EAR_CUBE, ModelPartBuilder.create().uv(43, 10).cuboid(-2.0f, -3.0f, 0.0f, 2.0f, 5.0f, 0.0f, new Dilation(0.0f)), ModelTransform.of((float)-0.5f, (float)0.0f, (float)-0.6f, (float)0.1886f, (float)-0.3864f, (float)-0.0718f));
        ModelPartData modelPartData5 = modelPartData3.addChild("left_ear", ModelPartBuilder.create(), ModelTransform.origin((float)1.0f, (float)-2.0f, (float)0.0f));
        modelPartData5.addChild(LEFT_EAR_CUBE, ModelPartBuilder.create().uv(47, 10).cuboid(0.0f, -3.0f, 0.0f, 2.0f, 5.0f, 0.0f, new Dilation(0.0f)), ModelTransform.of((float)0.5f, (float)1.0f, (float)-0.6f, (float)0.1886f, (float)0.3864f, (float)0.0718f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(51, 31).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin((float)-2.0f, (float)21.0f, (float)4.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(42, 31).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin((float)2.0f, (float)21.0f, (float)4.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(51, 43).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin((float)-2.0f, (float)21.0f, (float)-4.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(42, 43).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 3.0f, 2.0f, new Dilation(0.0f)), ModelTransform.origin((float)2.0f, (float)21.0f, (float)-4.0f));
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -10.0f, -6.0f, 10.0f, 10.0f, 10.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)24.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(ArmadilloEntityRenderState armadilloEntityRenderState) {
        super.setAngles((Object)armadilloEntityRenderState);
        if (armadilloEntityRenderState.rolledUp) {
            this.body.hidden = true;
            this.leftHindLeg.visible = false;
            this.rightHindLeg.visible = false;
            this.tail.visible = false;
            this.cube.visible = true;
        } else {
            this.body.hidden = false;
            this.leftHindLeg.visible = true;
            this.rightHindLeg.visible = true;
            this.tail.visible = true;
            this.cube.visible = false;
            this.head.pitch = MathHelper.clamp((float)armadilloEntityRenderState.pitch, (float)-22.5f, (float)25.0f) * ((float)Math.PI / 180);
            this.head.yaw = MathHelper.clamp((float)armadilloEntityRenderState.relativeHeadYaw, (float)-32.5f, (float)32.5f) * ((float)Math.PI / 180);
        }
        this.walkingAnimation.applyWalking(armadilloEntityRenderState.limbSwingAnimationProgress, armadilloEntityRenderState.limbSwingAmplitude, 16.5f, 2.5f);
        this.unrollingAnimation.apply(armadilloEntityRenderState.unrollingAnimationState, armadilloEntityRenderState.age);
        this.rollingAnimation.apply(armadilloEntityRenderState.rollingAnimationState, armadilloEntityRenderState.age);
        this.scaredAnimation.apply(armadilloEntityRenderState.scaredAnimationState, armadilloEntityRenderState.age);
    }
}

