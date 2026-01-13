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
 *  net.minecraft.client.render.entity.model.LargePufferfishEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LargePufferfishEntityModel
extends EntityModel<EntityRenderState> {
    private final ModelPart leftBlueFin;
    private final ModelPart rightBlueFin;

    public LargePufferfishEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftBlueFin = modelPart.getChild("left_blue_fin");
        this.rightBlueFin = modelPart.getChild("right_blue_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        modelPartData.addChild("right_blue_fin", ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), ModelTransform.origin((float)-4.0f, (float)15.0f, (float)-2.0f));
        modelPartData.addChild("left_blue_fin", ModelPartBuilder.create().uv(24, 3).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), ModelTransform.origin((float)4.0f, (float)15.0f, (float)-2.0f));
        modelPartData.addChild("top_front_fin", ModelPartBuilder.create().uv(15, 17).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of((float)0.0f, (float)14.0f, (float)-4.0f, (float)0.7853982f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("top_middle_fin", ModelPartBuilder.create().uv(14, 16).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)14.0f, (float)0.0f));
        modelPartData.addChild("top_back_fin", ModelPartBuilder.create().uv(23, 18).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of((float)0.0f, (float)14.0f, (float)4.0f, (float)-0.7853982f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_front_fin", ModelPartBuilder.create().uv(5, 17).cuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of((float)-4.0f, (float)22.0f, (float)-4.0f, (float)0.0f, (float)-0.7853982f, (float)0.0f));
        modelPartData.addChild("left_front_fin", ModelPartBuilder.create().uv(1, 17).cuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of((float)4.0f, (float)22.0f, (float)-4.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        modelPartData.addChild("bottom_front_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of((float)0.0f, (float)22.0f, (float)-4.0f, (float)-0.7853982f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("bottom_middle_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)0.0f));
        modelPartData.addChild("bottom_back_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of((float)0.0f, (float)22.0f, (float)4.0f, (float)0.7853982f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_back_fin", ModelPartBuilder.create().uv(9, 17).cuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of((float)-4.0f, (float)22.0f, (float)4.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        modelPartData.addChild("left_back_fin", ModelPartBuilder.create().uv(9, 17).cuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of((float)4.0f, (float)22.0f, (float)4.0f, (float)0.0f, (float)-0.7853982f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles((Object)entityRenderState);
        this.rightBlueFin.roll = -0.2f + 0.4f * MathHelper.sin((double)(entityRenderState.age * 0.2f));
        this.leftBlueFin.roll = 0.2f - 0.4f * MathHelper.sin((double)(entityRenderState.age * 0.2f));
    }
}

