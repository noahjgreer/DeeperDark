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
import net.minecraft.client.render.entity.model.NautilusEntityModel;

@Environment(value=EnvType.CLIENT)
public class NautilusArmorEntityModel
extends NautilusEntityModel {
    private final ModelPart armorRoot;
    private final ModelPart shell;

    public NautilusArmorEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.armorRoot = modelPart.getChild("root");
        this.shell = this.armorRoot.getChild("shell");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = NautilusArmorEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 29.0f, -6.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0f, -10.0f, -7.0f, 14.0f, 10.0f, 16.0f, new Dilation(0.01f)).uv(0, 26).cuboid(-7.0f, 0.0f, -7.0f, 14.0f, 8.0f, 20.0f, new Dilation(0.01f)).uv(48, 26).cuboid(-7.0f, 0.0f, 6.0f, 14.0f, 8.0f, 0.0f, new Dilation(0.0f)), ModelTransform.origin(0.0f, -13.0f, 5.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }
}
