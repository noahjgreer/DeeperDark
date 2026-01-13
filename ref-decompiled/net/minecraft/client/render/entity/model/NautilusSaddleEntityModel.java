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
 *  net.minecraft.client.render.entity.model.NautilusEntityModel
 *  net.minecraft.client.render.entity.model.NautilusSaddleEntityModel
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
import net.minecraft.client.render.entity.model.NautilusEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class NautilusSaddleEntityModel
extends NautilusEntityModel {
    private final ModelPart saddleRoot;
    private final ModelPart shell;

    public NautilusSaddleEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.saddleRoot = modelPart.getChild("root");
        this.shell = this.saddleRoot.getChild("shell");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = NautilusSaddleEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)29.0f, (float)-6.0f));
        modelPartData2.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0f, -10.0f, -7.0f, 14.0f, 10.0f, 16.0f, new Dilation(0.2f)), ModelTransform.origin((float)0.0f, (float)-13.0f, (float)5.0f));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)128);
    }
}

