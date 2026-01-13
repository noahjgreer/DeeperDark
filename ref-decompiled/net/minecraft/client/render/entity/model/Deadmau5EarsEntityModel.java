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
 *  net.minecraft.client.render.entity.model.Deadmau5EarsEntityModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
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
import net.minecraft.client.render.entity.model.PlayerEntityModel;

@Environment(value=EnvType.CLIENT)
public class Deadmau5EarsEntityModel
extends PlayerEntityModel {
    public Deadmau5EarsEntityModel(ModelPart modelPart) {
        super(modelPart, false);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = PlayerEntityModel.getTexturedModelData((Dilation)Dilation.NONE, (boolean)false);
        ModelPartData modelPartData = modelData.getRoot().resetChildrenParts();
        ModelPartData modelPartData2 = modelPartData.getChild("head");
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 0).cuboid(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, new Dilation(1.0f));
        modelPartData2.addChild("left_ear", modelPartBuilder, ModelTransform.origin((float)-6.0f, (float)-6.0f, (float)0.0f));
        modelPartData2.addChild("right_ear", modelPartBuilder, ModelTransform.origin((float)6.0f, (float)-6.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }
}

