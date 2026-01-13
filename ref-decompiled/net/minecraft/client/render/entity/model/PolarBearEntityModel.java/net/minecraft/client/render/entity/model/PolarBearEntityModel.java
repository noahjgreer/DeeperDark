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
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityModel
extends QuadrupedEntityModel<PolarBearEntityRenderState> {
    private static final float field_53834 = 2.25f;
    private static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0f, 4.0f, 2.25f, 2.0f, 24.0f, Set.of("head"));

    public PolarBearEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(boolean bl) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -3.0f, -3.0f, 7.0f, 7.0f, 7.0f).uv(0, 44).cuboid("mouth", -2.5f, 1.0f, -6.0f, 5.0f, 3.0f, 3.0f).uv(26, 0).cuboid("right_ear", -4.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f).uv(26, 0).mirrored().cuboid("left_ear", 2.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f), ModelTransform.origin(0.0f, 10.0f, -16.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 19).cuboid(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f).uv(39, 0).cuboid(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f), ModelTransform.of(-2.0f, 9.0f, 12.0f, 1.5707964f, 0.0f, 0.0f));
        int i = 10;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(50, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin(-4.5f, 14.0f, 6.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin(4.5f, 14.0f, 6.0f));
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(50, 40).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f);
        modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.origin(-3.5f, 14.0f, -8.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.origin(3.5f, 14.0f, -8.0f));
        return TexturedModelData.of(modelData, 128, 64).transform(bl ? BABY_TRANSFORMER : ModelTransformer.NO_OP).transform(ModelTransformer.scaling(1.2f));
    }

    @Override
    public void setAngles(PolarBearEntityRenderState polarBearEntityRenderState) {
        super.setAngles(polarBearEntityRenderState);
        float f = polarBearEntityRenderState.warningAnimationProgress * polarBearEntityRenderState.warningAnimationProgress;
        float g = polarBearEntityRenderState.ageScale;
        float h = polarBearEntityRenderState.baby ? 0.44444445f : 1.0f;
        this.body.pitch -= f * (float)Math.PI * 0.35f;
        this.body.originY += f * g * 2.0f;
        this.rightFrontLeg.originY -= f * g * 20.0f;
        this.rightFrontLeg.originZ += f * g * 4.0f;
        this.rightFrontLeg.pitch -= f * (float)Math.PI * 0.45f;
        this.leftFrontLeg.originY = this.rightFrontLeg.originY;
        this.leftFrontLeg.originZ = this.rightFrontLeg.originZ;
        this.leftFrontLeg.pitch -= f * (float)Math.PI * 0.45f;
        this.head.originY -= f * h * 24.0f;
        this.head.originZ += f * h * 13.0f;
        this.head.pitch += f * (float)Math.PI * 0.15f;
    }
}
