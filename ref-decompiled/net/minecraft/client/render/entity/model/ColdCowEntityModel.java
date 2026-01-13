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
 *  net.minecraft.client.render.entity.model.ColdCowEntityModel
 *  net.minecraft.client.render.entity.model.CowEntityModel
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
import net.minecraft.client.render.entity.model.CowEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ColdCowEntityModel
extends CowEntityModel {
    public ColdCowEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ColdCowEntityModel.getModelData();
        modelData.getRoot().addChild("body", ModelPartBuilder.create().uv(20, 32).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, new Dilation(0.5f)).uv(18, 4).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).uv(52, 0).cuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), ModelTransform.of((float)0.0f, (float)5.0f, (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData = modelData.getRoot().addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(9, 33).cuboid(-3.0f, 1.0f, -7.0f, 6.0f, 3.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)4.0f, (float)-8.0f));
        modelPartData.addChild("right_horn", ModelPartBuilder.create().uv(0, 40).cuboid(-1.5f, -4.5f, -0.5f, 2.0f, 6.0f, 2.0f), ModelTransform.of((float)-4.5f, (float)-2.5f, (float)-3.5f, (float)1.5708f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("left_horn", ModelPartBuilder.create().uv(0, 32).cuboid(-1.5f, -3.0f, -0.5f, 2.0f, 6.0f, 2.0f), ModelTransform.of((float)5.5f, (float)-2.5f, (float)-5.0f, (float)1.5708f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }
}

