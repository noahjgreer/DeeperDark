/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.ChickenEntityModel
 *  net.minecraft.client.render.entity.model.ColdChickenEntityModel
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ChickenEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ColdChickenEntityModel
extends ChickenEntityModel {
    public ColdChickenEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ColdChickenEntityModel.getModelData();
        modelData.getRoot().addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f).uv(38, 9).cuboid(0.0f, 3.0f, -1.0f, 0.0f, 3.0f, 5.0f), ModelTransform.of((float)0.0f, (float)16.0f, (float)0.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        modelData.getRoot().addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f).uv(44, 0).cuboid(-3.0f, -7.0f, -2.015f, 6.0f, 3.0f, 4.0f), ModelTransform.origin((float)0.0f, (float)15.0f, (float)-4.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }
}

