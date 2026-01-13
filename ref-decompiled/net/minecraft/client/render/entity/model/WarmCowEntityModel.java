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
 *  net.minecraft.client.render.entity.model.CowEntityModel
 *  net.minecraft.client.render.entity.model.WarmCowEntityModel
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CowEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class WarmCowEntityModel
extends CowEntityModel {
    public WarmCowEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = WarmCowEntityModel.getModelData();
        modelData.getRoot().addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(1, 33).cuboid(-3.0f, 1.0f, -7.0f, 6.0f, 3.0f, 1.0f).uv(27, 0).cuboid(-8.0f, -3.0f, -5.0f, 4.0f, 2.0f, 2.0f).uv(39, 0).cuboid(-8.0f, -5.0f, -5.0f, 2.0f, 2.0f, 2.0f).uv(27, 0).mirrored().cuboid(4.0f, -3.0f, -5.0f, 4.0f, 2.0f, 2.0f).mirrored(false).uv(39, 0).mirrored().cuboid(6.0f, -5.0f, -5.0f, 2.0f, 2.0f, 2.0f).mirrored(false), ModelTransform.origin((float)0.0f, (float)4.0f, (float)-8.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }
}

