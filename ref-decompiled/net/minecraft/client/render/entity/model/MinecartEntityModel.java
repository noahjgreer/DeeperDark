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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.MinecartEntityModel
 *  net.minecraft.client.render.entity.state.MinecartEntityRenderState
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
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityModel
extends EntityModel<MinecartEntityRenderState> {
    public MinecartEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 10).cuboid(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f), ModelTransform.of((float)0.0f, (float)4.0f, (float)0.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("front", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of((float)-9.0f, (float)4.0f, (float)0.0f, (float)0.0f, (float)4.712389f, (float)0.0f));
        modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of((float)9.0f, (float)4.0f, (float)0.0f, (float)0.0f, (float)1.5707964f, (float)0.0f));
        modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of((float)0.0f, (float)4.0f, (float)-7.0f, (float)0.0f, (float)((float)Math.PI), (float)0.0f));
        modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.origin((float)0.0f, (float)4.0f, (float)7.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }
}

