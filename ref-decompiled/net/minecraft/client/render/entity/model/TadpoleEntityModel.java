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
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.TadpoleEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
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
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityModel
extends EntityModel<LivingEntityRenderState> {
    private final ModelPart tail;

    public TadpoleEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityCutoutNoCull);
        this.tail = modelPart.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.0f;
        float g = 22.0f;
        float h = -3.0f;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -1.0f, 0.0f, 3.0f, 2.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)-3.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -1.0f, 0.0f, 0.0f, 2.0f, 7.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)16, (int)16);
    }

    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles((Object)livingEntityRenderState);
        float f = livingEntityRenderState.touchingWater ? 1.0f : 1.5f;
        this.tail.yaw = -f * 0.25f * MathHelper.sin((double)(0.3f * livingEntityRenderState.age));
    }
}

