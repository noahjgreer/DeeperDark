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
 *  net.minecraft.client.render.entity.model.SlimeEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
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
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel
extends EntityModel<EntityRenderState> {
    public SlimeEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getOuterTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public static TexturedModelData getInnerTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("cube", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.NONE);
        modelPartData.addChild("right_eye", ModelPartBuilder.create().uv(32, 0).cuboid(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("left_eye", ModelPartBuilder.create().uv(32, 4).cuboid(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("mouth", ModelPartBuilder.create().uv(32, 8).cuboid(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }
}

