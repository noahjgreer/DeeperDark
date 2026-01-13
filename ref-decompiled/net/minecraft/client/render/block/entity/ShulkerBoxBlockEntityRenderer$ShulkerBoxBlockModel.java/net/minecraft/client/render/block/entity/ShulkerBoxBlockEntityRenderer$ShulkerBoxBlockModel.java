/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;

@Environment(value=EnvType.CLIENT)
static class ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel
extends Model<Float> {
    private final ModelPart lid;

    public ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
        this.lid = root.getChild("lid");
    }

    @Override
    public void setAngles(Float float_) {
        super.setAngles(float_);
        this.lid.setOrigin(0.0f, 24.0f - float_.floatValue() * 0.5f * 16.0f, 0.0f);
        this.lid.yaw = 270.0f * float_.floatValue() * ((float)Math.PI / 180);
    }
}
