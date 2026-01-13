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
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.animation.Animation
 *  net.minecraft.client.render.entity.animation.BreezeAnimations
 *  net.minecraft.client.render.entity.model.BreezeEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.BreezeEntityRenderState
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
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.BreezeAnimations;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BreezeEntityModel
extends EntityModel<BreezeEntityRenderState> {
    private static final float field_47431 = 0.6f;
    private static final float field_47432 = 0.8f;
    private static final float field_47433 = 1.0f;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart windBody;
    private final ModelPart windTop;
    private final ModelPart windMid;
    private final ModelPart windBottom;
    private final ModelPart rods;
    private final Animation idlingAnimation;
    private final Animation shootingAnimation;
    private final Animation slidingAnimation;
    private final Animation slidingBackAnimation;
    private final Animation inhalingAnimation;
    private final Animation longJumpingAnimation;

    public BreezeEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityTranslucent);
        this.windBody = modelPart.getChild("wind_body");
        this.windBottom = this.windBody.getChild("wind_bottom");
        this.windMid = this.windBottom.getChild("wind_mid");
        this.windTop = this.windMid.getChild("wind_top");
        this.head = modelPart.getChild("body").getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.rods = modelPart.getChild("body").getChild("rods");
        this.idlingAnimation = BreezeAnimations.IDLING.createAnimation(modelPart);
        this.shootingAnimation = BreezeAnimations.SHOOTING.createAnimation(modelPart);
        this.slidingAnimation = BreezeAnimations.SLIDING.createAnimation(modelPart);
        this.slidingBackAnimation = BreezeAnimations.SLIDING_BACK.createAnimation(modelPart);
        this.inhalingAnimation = BreezeAnimations.INHALING.createAnimation(modelPart);
        this.longJumpingAnimation = BreezeAnimations.LONG_JUMPING.createAnimation(modelPart);
    }

    private static ModelData createModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("rods", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)8.0f, (float)0.0f));
        modelPartData3.addChild("rod_1", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of((float)2.5981f, (float)-3.0f, (float)1.5f, (float)-2.7489f, (float)-1.0472f, (float)3.1416f));
        modelPartData3.addChild("rod_2", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of((float)-2.5981f, (float)-3.0f, (float)1.5f, (float)-2.7489f, (float)1.0472f, (float)3.1416f));
        modelPartData3.addChild("rod_3", ModelPartBuilder.create().uv(0, 17).cuboid(-1.0f, 0.0f, -3.0f, 2.0f, 8.0f, 2.0f, new Dilation(0.0f)), ModelTransform.of((float)0.0f, (float)-3.0f, (float)-3.0f, (float)0.3927f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)4.0f, (float)0.0f));
        modelPartData4.addChild("eyes", ModelPartBuilder.create().uv(4, 24).cuboid(-5.0f, -5.0f, -4.2f, 10.0f, 3.0f, 4.0f, new Dilation(0.0f)).uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData5 = modelPartData.addChild("wind_body", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData6 = modelPartData5.addChild("wind_bottom", ModelPartBuilder.create().uv(1, 83).cuboid(-2.5f, -7.0f, -2.5f, 5.0f, 7.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)24.0f, (float)0.0f));
        ModelPartData modelPartData7 = modelPartData6.addChild("wind_mid", ModelPartBuilder.create().uv(74, 28).cuboid(-6.0f, -6.0f, -6.0f, 12.0f, 6.0f, 12.0f, new Dilation(0.0f)).uv(78, 32).cuboid(-4.0f, -6.0f, -4.0f, 8.0f, 6.0f, 8.0f, new Dilation(0.0f)).uv(49, 71).cuboid(-2.5f, -6.0f, -2.5f, 5.0f, 6.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)-7.0f, (float)0.0f));
        modelPartData7.addChild("wind_top", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -8.0f, -9.0f, 18.0f, 8.0f, 18.0f, new Dilation(0.0f)).uv(6, 6).cuboid(-6.0f, -8.0f, -6.0f, 12.0f, 8.0f, 12.0f, new Dilation(0.0f)).uv(105, 57).cuboid(-2.5f, -8.0f, -2.5f, 5.0f, 8.0f, 5.0f, new Dilation(0.0f)), ModelTransform.origin((float)0.0f, (float)-6.0f, (float)0.0f));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BreezeEntityModel.createModelData();
        modelData.getRoot().resetChildrenExcept(Set.of("head", "rods"));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public static TexturedModelData getWindTexturedModelData() {
        ModelData modelData = BreezeEntityModel.createModelData();
        modelData.getRoot().resetChildrenExcept(Set.of("wind_body"));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)128);
    }

    public static TexturedModelData getEyesTexturedModelData() {
        ModelData modelData = BreezeEntityModel.createModelData();
        modelData.getRoot().resetChildrenExcept(Set.of("eyes"));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(BreezeEntityRenderState breezeEntityRenderState) {
        super.setAngles((Object)breezeEntityRenderState);
        this.idlingAnimation.apply(breezeEntityRenderState.idleAnimationState, breezeEntityRenderState.age);
        this.shootingAnimation.apply(breezeEntityRenderState.shootingAnimationState, breezeEntityRenderState.age);
        this.slidingAnimation.apply(breezeEntityRenderState.slidingAnimationState, breezeEntityRenderState.age);
        this.slidingBackAnimation.apply(breezeEntityRenderState.slidingBackAnimationState, breezeEntityRenderState.age);
        this.inhalingAnimation.apply(breezeEntityRenderState.inhalingAnimationState, breezeEntityRenderState.age);
        this.longJumpingAnimation.apply(breezeEntityRenderState.longJumpingAnimationState, breezeEntityRenderState.age);
    }

    public ModelPart getHead() {
        return this.head;
    }

    public ModelPart getEyes() {
        return this.eyes;
    }

    public ModelPart getRods() {
        return this.rods;
    }

    public ModelPart getWindBody() {
        return this.windBody;
    }
}

