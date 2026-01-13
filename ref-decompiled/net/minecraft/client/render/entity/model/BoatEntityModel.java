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
 *  net.minecraft.client.render.entity.model.BoatEntityModel
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
public class BoatEntityModel
extends AbstractBoatEntityModel {
    private static final int field_52877 = 28;
    private static final int field_52878 = 32;
    private static final int field_52879 = 6;
    private static final int field_52880 = 20;
    private static final int field_52881 = 4;
    private static final String WATER_PATCH = "water_patch";
    private static final String BACK = "back";
    private static final String FRONT = "front";
    private static final String RIGHT = "right";
    private static final String LEFT = "left";

    public BoatEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    private static void addParts(ModelPartData modelPartData) {
        int i = 16;
        int j = 14;
        int k = 10;
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of((float)0.0f, (float)3.0f, (float)1.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 19).cuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), ModelTransform.of((float)-15.0f, (float)4.0f, (float)4.0f, (float)0.0f, (float)4.712389f, (float)0.0f));
        modelPartData.addChild("front", ModelPartBuilder.create().uv(0, 27).cuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), ModelTransform.of((float)15.0f, (float)4.0f, (float)0.0f, (float)0.0f, (float)1.5707964f, (float)0.0f));
        modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 35).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.of((float)0.0f, (float)4.0f, (float)-9.0f, (float)0.0f, (float)((float)Math.PI), (float)0.0f));
        modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.origin((float)0.0f, (float)4.0f, (float)9.0f));
        int l = 20;
        int m = 7;
        int n = 6;
        float f = -5.0f;
        modelPartData.addChild("left_paddle", ModelPartBuilder.create().uv(62, 0).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of((float)3.0f, (float)-5.0f, (float)9.0f, (float)0.0f, (float)0.0f, (float)0.19634955f));
        modelPartData.addChild("right_paddle", ModelPartBuilder.create().uv(62, 20).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of((float)3.0f, (float)-5.0f, (float)-9.0f, (float)0.0f, (float)((float)Math.PI), (float)0.19634955f));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        BoatEntityModel.addParts((ModelPartData)modelPartData);
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)64);
    }

    public static TexturedModelData getChestTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        BoatEntityModel.addParts((ModelPartData)modelPartData);
        modelPartData.addChild("chest_bottom", ModelPartBuilder.create().uv(0, 76).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 8.0f, 12.0f), ModelTransform.of((float)-2.0f, (float)-5.0f, (float)-6.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        modelPartData.addChild("chest_lid", ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 4.0f, 12.0f), ModelTransform.of((float)-2.0f, (float)-9.0f, (float)-6.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        modelPartData.addChild("chest_lock", ModelPartBuilder.create().uv(0, 59).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 4.0f, 1.0f), ModelTransform.of((float)-1.0f, (float)-6.0f, (float)-1.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)128);
    }

    public static TexturedModelData getBaseTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("water_patch", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of((float)0.0f, (float)-3.0f, (float)1.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)0, (int)0);
    }
}

