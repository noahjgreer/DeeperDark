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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel
 *  net.minecraft.client.render.entity.state.TropicalFishEntityRenderState
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SmallTropicalFishEntityModel
extends EntityModel<TropicalFishEntityRenderState> {
    private final ModelPart tail;

    public SmallTropicalFishEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.tail = modelPart.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -1.5f, -3.0f, 2.0f, 3.0f, 6.0f, dilation), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(22, -6).cuboid(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 6.0f, dilation), ModelTransform.origin((float)0.0f, (float)22.0f, (float)3.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(2, 16).cuboid(-2.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of((float)-1.0f, (float)22.5f, (float)0.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(2, 12).cuboid(0.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of((float)1.0f, (float)22.5f, (float)0.0f, (float)0.0f, (float)-0.7853982f, (float)0.0f));
        modelPartData.addChild("top_fin", ModelPartBuilder.create().uv(10, -5).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f, dilation), ModelTransform.origin((float)0.0f, (float)20.5f, (float)-3.0f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        super.setAngles((Object)tropicalFishEntityRenderState);
        float f = tropicalFishEntityRenderState.touchingWater ? 1.0f : 1.5f;
        this.tail.yaw = -f * 0.45f * MathHelper.sin((double)(0.6f * tropicalFishEntityRenderState.age));
    }
}

