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
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.RavagerEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends EntityModel<RavagerEntityRenderState> {
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    public RavagerEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.neck = modelPart.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("mouth");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 16;
        ModelPartData modelPartData2 = modelPartData.addChild("neck", ModelPartBuilder.create().uv(68, 73).cuboid(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f), ModelTransform.origin(0.0f, -7.0f, 5.5f));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).uv(0, 0).cuboid(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), ModelTransform.origin(0.0f, 16.0f, -17.0f));
        modelPartData3.addChild("right_horn", ModelPartBuilder.create().uv(74, 55).cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        modelPartData3.addChild("left_horn", ModelPartBuilder.create().uv(74, 55).mirrored().cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        modelPartData3.addChild("mouth", ModelPartBuilder.create().uv(0, 36).cuboid(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), ModelTransform.origin(0.0f, -2.0f, 2.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 55).cuboid(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f).uv(0, 91).cuboid(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f), ModelTransform.of(0.0f, 1.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(96, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.origin(-8.0f, -13.0f, 18.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(96, 0).mirrored().cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.origin(8.0f, -13.0f, 18.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(64, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.origin(-8.0f, -13.0f, -5.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(64, 0).mirrored().cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.origin(8.0f, -13.0f, -5.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(RavagerEntityRenderState ravagerEntityRenderState) {
        float j;
        float h;
        super.setAngles(ravagerEntityRenderState);
        float f = ravagerEntityRenderState.stunTick;
        float g = ravagerEntityRenderState.attackTick;
        int i = 10;
        if (g > 0.0f) {
            h = MathHelper.wrap(g, 10.0f);
            j = (1.0f + h) * 0.5f;
            float k = j * j * j * 12.0f;
            float l = k * MathHelper.sin(this.neck.pitch);
            this.neck.originZ = -6.5f + k;
            this.neck.originY = -7.0f - l;
            this.jaw.pitch = g > 5.0f ? MathHelper.sin((-4.0f + g) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * g / 10.0f);
        } else {
            h = -1.0f;
            j = -1.0f * MathHelper.sin(this.neck.pitch);
            this.neck.originX = 0.0f;
            this.neck.originY = -7.0f - j;
            this.neck.originZ = 5.5f;
            boolean bl = f > 0.0f;
            this.neck.pitch = bl ? 0.21991149f : 0.0f;
            this.jaw.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)f / 40.0;
                this.neck.originX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if ((double)ravagerEntityRenderState.roarTick > 0.0) {
                float l = MathHelper.sin(ravagerEntityRenderState.roarTick * (float)Math.PI * 0.25f);
                this.jaw.pitch = 1.5707964f * l;
            }
        }
        this.head.pitch = ravagerEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = ravagerEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        h = ravagerEntityRenderState.limbSwingAnimationProgress;
        j = 0.4f * ravagerEntityRenderState.limbSwingAmplitude;
        this.rightHindLeg.pitch = MathHelper.cos(h * 0.6662f) * j;
        this.leftHindLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * j;
        this.rightFrontLeg.pitch = MathHelper.cos(h * 0.6662f + (float)Math.PI) * j;
        this.leftFrontLeg.pitch = MathHelper.cos(h * 0.6662f) * j;
    }
}
