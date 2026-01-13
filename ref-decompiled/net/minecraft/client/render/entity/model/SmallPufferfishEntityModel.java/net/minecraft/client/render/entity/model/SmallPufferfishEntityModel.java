/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 27).cuboid(-1.5f, -2.0f, -1.5f, 3.0f, 2.0f, 3.0f), ModelTransform.origin(0.0f, 23.0f, 0.0f));
        modelPartData.addChild("right_eye", ModelPartBuilder.create().uv(24, 6).cuboid(-1.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.origin(0.0f, 20.0f, 0.0f));
        modelPartData.addChild("left_eye", ModelPartBuilder.create().uv(28, 6).cuboid(0.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f), ModelTransform.origin(0.0f, 20.0f, 0.0f));
        modelPartData.addChild("back_fin", ModelPartBuilder.create().uv(-3, 0).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 0.0f, 3.0f), ModelTransform.origin(0.0f, 22.0f, 1.5f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(25, 0).cuboid(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.origin(-1.5f, 22.0f, -1.5f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(25, 0).cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f), ModelTransform.origin(1.5f, 22.0f, -1.5f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles(entityRenderState);
        this.rightFin.roll = -0.2f + 0.4f * MathHelper.sin(entityRenderState.age * 0.2f);
        this.leftFin.roll = 0.2f - 0.4f * MathHelper.sin(entityRenderState.age * 0.2f);
    }
}
