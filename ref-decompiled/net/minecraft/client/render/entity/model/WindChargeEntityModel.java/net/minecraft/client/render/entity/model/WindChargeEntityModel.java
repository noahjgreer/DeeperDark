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
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class WindChargeEntityModel
extends EntityModel<EntityRenderState> {
    private static final int field_48704 = 16;
    private final ModelPart bone;
    private final ModelPart windCharge;
    private final ModelPart wind;

    public WindChargeEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityTranslucent);
        this.bone = modelPart.getChild("bone");
        this.wind = this.bone.getChild("wind");
        this.windCharge = this.bone.getChild("wind_charge");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("wind", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, new Dilation(0.0f)).uv(0, 9).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 4.0f, 6.0f, new Dilation(0.0f)), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f));
        modelPartData2.addChild("wind_charge", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles(entityRenderState);
        this.windCharge.yaw = -entityRenderState.age * 16.0f * ((float)Math.PI / 180);
        this.wind.yaw = entityRenderState.age * 16.0f * ((float)Math.PI / 180);
    }
}
