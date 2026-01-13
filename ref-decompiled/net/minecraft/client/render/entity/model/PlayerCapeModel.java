/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.PlayerCapeModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  org.joml.Quaternionf
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
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class PlayerCapeModel
extends PlayerEntityModel {
    private static final String CAPE = "cape";
    private final ModelPart cape;

    public PlayerCapeModel(ModelPart modelPart) {
        super(modelPart, false);
        this.cape = this.body.getChild(CAPE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = PlayerEntityModel.getTexturedModelData((Dilation)Dilation.NONE, (boolean)false);
        ModelPartData modelPartData = modelData.getRoot().resetChildrenParts();
        ModelPartData modelPartData2 = modelPartData.getChild("body");
        modelPartData2.addChild(CAPE, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, Dilation.NONE, 1.0f, 0.5f), ModelTransform.of((float)0.0f, (float)0.0f, (float)2.0f, (float)0.0f, (float)((float)Math.PI), (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
        super.setAngles(playerEntityRenderState);
        this.cape.rotate(new Quaternionf().rotateY((float)(-Math.PI)).rotateX((6.0f + playerEntityRenderState.field_53537 / 2.0f + playerEntityRenderState.field_53536) * ((float)Math.PI / 180)).rotateZ(playerEntityRenderState.field_53538 / 2.0f * ((float)Math.PI / 180)).rotateY((180.0f - playerEntityRenderState.field_53538 / 2.0f) * ((float)Math.PI / 180)));
    }
}

