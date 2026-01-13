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
 *  net.minecraft.client.render.entity.model.AbstractBoatEntityModel
 *  net.minecraft.client.render.entity.model.RaftEntityModel
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
import net.minecraft.client.render.entity.model.AbstractBoatEntityModel;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RaftEntityModel
extends AbstractBoatEntityModel {
    public RaftEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    private static void addParts(ModelPartData modelPartData) {
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -11.0f, -4.0f, 28.0f, 20.0f, 4.0f).uv(0, 0).cuboid(-14.0f, -9.0f, -8.0f, 28.0f, 16.0f, 4.0f), ModelTransform.of((float)0.0f, (float)-2.1f, (float)1.0f, (float)1.5708f, (float)0.0f, (float)0.0f));
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelPartData.addChild("left_paddle", ModelPartBuilder.create().uv(0, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of((float)3.0f, (float)-4.0f, (float)9.0f, (float)0.0f, (float)0.0f, (float)0.19634955f));
        modelPartData.addChild("right_paddle", ModelPartBuilder.create().uv(40, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of((float)3.0f, (float)-4.0f, (float)-9.0f, (float)0.0f, (float)((float)Math.PI), (float)0.19634955f));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        RaftEntityModel.addParts((ModelPartData)modelPartData);
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)64);
    }

    public static TexturedModelData getChestTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        RaftEntityModel.addParts((ModelPartData)modelPartData);
        modelPartData.addChild("chest_bottom", ModelPartBuilder.create().uv(0, 76).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), ModelTransform.of((float)-2.0f, (float)-10.1f, (float)-6.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        modelPartData.addChild("chest_lid", ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), ModelTransform.of((float)-2.0f, (float)-14.1f, (float)-6.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        modelPartData.addChild("chest_lock", ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), ModelTransform.of((float)-1.0f, (float)-11.1f, (float)-1.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)128);
    }
}

