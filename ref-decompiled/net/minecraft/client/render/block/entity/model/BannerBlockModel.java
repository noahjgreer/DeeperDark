/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.model.BannerBlockModel
 *  net.minecraft.util.Unit
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class BannerBlockModel
extends Model<Unit> {
    public static final int field_55108 = 20;
    public static final int field_55109 = 40;
    public static final String FLAG = "flag";
    private static final String POLE = "pole";
    private static final String BAR = "bar";

    public BannerBlockModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        if (standing) {
            modelPartData.addChild(POLE, ModelPartBuilder.create().uv(44, 0).cuboid(-1.0f, -42.0f, -1.0f, 2.0f, 42.0f, 2.0f), ModelTransform.NONE);
        }
        modelPartData.addChild(BAR, ModelPartBuilder.create().uv(0, 42).cuboid(-10.0f, standing ? -44.0f : -20.5f, standing ? -1.0f : 9.5f, 20.0f, 2.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }
}

