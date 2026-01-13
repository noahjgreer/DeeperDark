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
 *  net.minecraft.client.render.block.entity.model.BannerFlagBlockModel
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BannerFlagBlockModel
extends Model<Float> {
    private final ModelPart flag;

    public BannerFlagBlockModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
        this.flag = root.getChild("flag");
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("flag", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)(standing ? -44.0f : -20.5f), (float)(standing ? 0.0f : 10.5f)));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(Float float_) {
        super.setAngles((Object)float_);
        this.flag.pitch = (-0.0125f + 0.01f * MathHelper.cos((double)((float)Math.PI * 2 * float_.floatValue()))) * (float)Math.PI;
    }
}

