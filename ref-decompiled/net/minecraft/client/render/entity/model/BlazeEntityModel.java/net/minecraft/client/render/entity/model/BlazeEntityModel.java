/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlazeEntityModel
extends EntityModel<LivingEntityRenderState> {
    private final ModelPart[] rods;
    private final ModelPart head;

    public BlazeEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rods = new ModelPart[12];
        Arrays.setAll(this.rods, i -> modelPart.getChild(BlazeEntityModel.getRodName(i)));
    }

    private static String getRodName(int index) {
        return "part" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        float j;
        float h;
        float g;
        int i;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        float f = 0.0f;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        for (i = 0; i < 4; ++i) {
            g = MathHelper.cos(f) * 9.0f;
            h = -2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 9.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.origin(g, h, j));
            f += 1.5707964f;
        }
        f = 0.7853982f;
        for (i = 4; i < 8; ++i) {
            g = MathHelper.cos(f) * 7.0f;
            h = 2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 7.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.origin(g, h, j));
            f += 1.5707964f;
        }
        f = 0.47123894f;
        for (i = 8; i < 12; ++i) {
            g = MathHelper.cos(f) * 5.0f;
            h = 11.0f + MathHelper.cos((float)i * 1.5f * 0.5f);
            j = MathHelper.sin(f) * 5.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.origin(g, h, j));
            f += 1.5707964f;
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        int i;
        super.setAngles(livingEntityRenderState);
        float f = livingEntityRenderState.age * (float)Math.PI * -0.1f;
        for (i = 0; i < 4; ++i) {
            this.rods[i].originY = -2.0f + MathHelper.cos(((float)(i * 2) + livingEntityRenderState.age) * 0.25f);
            this.rods[i].originX = MathHelper.cos(f) * 9.0f;
            this.rods[i].originZ = MathHelper.sin(f) * 9.0f;
            f += 1.5707964f;
        }
        f = 0.7853982f + livingEntityRenderState.age * (float)Math.PI * 0.03f;
        for (i = 4; i < 8; ++i) {
            this.rods[i].originY = 2.0f + MathHelper.cos(((float)(i * 2) + livingEntityRenderState.age) * 0.25f);
            this.rods[i].originX = MathHelper.cos(f) * 7.0f;
            this.rods[i].originZ = MathHelper.sin(f) * 7.0f;
            f += 1.5707964f;
        }
        f = 0.47123894f + livingEntityRenderState.age * (float)Math.PI * -0.05f;
        for (i = 8; i < 12; ++i) {
            this.rods[i].originY = 11.0f + MathHelper.cos(((float)i * 1.5f + livingEntityRenderState.age) * 0.5f);
            this.rods[i].originX = MathHelper.cos(f) * 5.0f;
            this.rods[i].originZ = MathHelper.sin(f) * 5.0f;
            f += 1.5707964f;
        }
        this.head.yaw = livingEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = livingEntityRenderState.pitch * ((float)Math.PI / 180);
    }
}
