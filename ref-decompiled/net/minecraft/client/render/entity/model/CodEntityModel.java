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
 *  net.minecraft.client.render.entity.model.CodEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CodEntityModel
extends EntityModel<LivingEntityRenderState> {
    private final ModelPart tailFin;

    public CodEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.tailFin = modelPart.getChild("tail_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        modelPartData.addChild("head", ModelPartBuilder.create().uv(11, 0).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        modelPartData.addChild("nose", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)-3.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(22, 1).cuboid(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of((float)-1.0f, (float)23.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.7853982f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(22, 4).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of((float)1.0f, (float)23.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.7853982f));
        modelPartData.addChild("tail_fin", ModelPartBuilder.create().uv(22, 3).cuboid(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)7.0f));
        modelPartData.addChild("top_fin", ModelPartBuilder.create().uv(20, -6).cuboid(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(LivingEntityRenderState livingEntityRenderState) {
        super.setAngles((Object)livingEntityRenderState);
        float f = livingEntityRenderState.touchingWater ? 1.0f : 1.5f;
        this.tailFin.yaw = -f * 0.45f * MathHelper.sin((double)(0.6f * livingEntityRenderState.age));
    }
}

