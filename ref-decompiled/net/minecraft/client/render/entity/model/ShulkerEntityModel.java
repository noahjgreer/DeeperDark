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
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ShulkerEntityModel
 *  net.minecraft.client.render.entity.state.ShulkerEntityRenderState
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
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel
extends EntityModel<ShulkerEntityRenderState> {
    public static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityCutoutNoCullZOffset);
        this.lid = modelPart.getChild("lid");
        this.head = modelPart.getChild("head");
    }

    private static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), ModelTransform.origin((float)0.0f, (float)24.0f, (float)0.0f));
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 28).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), ModelTransform.origin((float)0.0f, (float)24.0f, (float)0.0f));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ShulkerEntityModel.getModelData();
        modelData.getRoot().addChild("head", ModelPartBuilder.create().uv(0, 52).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.origin((float)0.0f, (float)12.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public static TexturedModelData getShulkerBoxTexturedModelData() {
        ModelData modelData = ShulkerEntityModel.getModelData();
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(ShulkerEntityRenderState shulkerEntityRenderState) {
        super.setAngles((Object)shulkerEntityRenderState);
        float f = (0.5f + shulkerEntityRenderState.openProgress) * (float)Math.PI;
        float g = -1.0f + MathHelper.sin((double)f);
        float h = 0.0f;
        if (f > (float)Math.PI) {
            h = MathHelper.sin((double)(shulkerEntityRenderState.age * 0.1f)) * 0.7f;
        }
        this.lid.setOrigin(0.0f, 16.0f + MathHelper.sin((double)f) * 8.0f + h, 0.0f);
        this.lid.yaw = shulkerEntityRenderState.openProgress > 0.3f ? g * g * g * g * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = shulkerEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = (shulkerEntityRenderState.headYaw - 180.0f - shulkerEntityRenderState.shellYaw) * ((float)Math.PI / 180);
    }
}

