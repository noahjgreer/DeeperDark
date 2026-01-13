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
 *  net.minecraft.client.render.entity.model.SmallPufferfishEntityModel
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
public class SmallPufferfishEntityModel
extends EntityModel<EntityRenderState> {
    private final ModelPart leftFin;
    private final ModelPart rightFin;

    public SmallPufferfishEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftFin = modelPart.getChild("left_fin");
        this.rightFin = modelPart.getChild("right_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 23;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 27).cuboid(-1.5f, -2.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)23.0f, (float)0.0f));
        modelPartData.addChild("right_eye", ModelPartBuilder.create().uv(24, 6).cuboid(-1.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)0.0f));
        modelPartData.addChild("left_eye", ModelPartBuilder.create().uv(28, 6).cuboid(0.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)0.0f));
        modelPartData.addChild("back_fin", ModelPartBuilder.create().uv(-3, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 0.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)22.0f, (float)1.5f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(25, 0).cuboid(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.origin((float)-1.5f, (float)22.0f, (float)-1.5f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(25, 0).cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.origin((float)1.5f, (float)22.0f, (float)-1.5f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles((Object)entityRenderState);
        this.rightFin.roll = -0.2f + 0.4f * MathHelper.sin((double)(entityRenderState.age * 0.2f));
        this.leftFin.roll = 0.2f - 0.4f * MathHelper.sin((double)(entityRenderState.age * 0.2f));
    }
}

