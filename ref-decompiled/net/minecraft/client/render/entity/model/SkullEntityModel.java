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
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 *  net.minecraft.client.render.entity.model.SkullEntityModel
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
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends SkullBlockEntityModel {
    protected final ModelPart head;

    public SkullEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return modelData;
    }

    public static TexturedModelData getHeadTexturedModelData() {
        ModelData modelData = SkullEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.getChild("head").addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(0.25f)), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public static TexturedModelData getSkullTexturedModelData() {
        ModelData modelData = SkullEntityModel.getModelData();
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(SkullBlockEntityModel.SkullModelState skullModelState) {
        super.setAngles((Object)skullModelState);
        this.head.yaw = skullModelState.yaw * ((float)Math.PI / 180);
        this.head.pitch = skullModelState.pitch * ((float)Math.PI / 180);
    }
}

