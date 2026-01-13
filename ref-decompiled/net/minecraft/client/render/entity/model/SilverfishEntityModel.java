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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.SilverfishEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SilverfishEntityModel
extends EntityModel<EntityRenderState> {
    private static final int BODY_PARTS_COUNT = 7;
    private final ModelPart[] body = new ModelPart[7];
    private final ModelPart[] scales = new ModelPart[3];
    private static final int[][] SEGMENT_LOCATIONS = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] SEGMENT_SIZES = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishEntityModel(ModelPart modelPart) {
        super(modelPart);
        Arrays.setAll(this.body, i -> modelPart.getChild(SilverfishEntityModel.getSegmentName((int)i)));
        Arrays.setAll(this.scales, i -> modelPart.getChild(SilverfishEntityModel.getLayerName((int)i)));
    }

    private static String getLayerName(int index) {
        return "layer" + index;
    }

    private static String getSegmentName(int index) {
        return "segment" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float[] fs = new float[7];
        float f = -3.5f;
        for (int i = 0; i < 7; ++i) {
            modelPartData.addChild(SilverfishEntityModel.getSegmentName((int)i), ModelPartBuilder.create().uv(SEGMENT_SIZES[i][0], SEGMENT_SIZES[i][1]).cuboid((float)SEGMENT_LOCATIONS[i][0] * -0.5f, 0.0f, (float)SEGMENT_LOCATIONS[i][2] * -0.5f, (float)SEGMENT_LOCATIONS[i][0], (float)SEGMENT_LOCATIONS[i][1], (float)SEGMENT_LOCATIONS[i][2]), ModelTransform.origin((float)0.0f, (float)(24 - SEGMENT_LOCATIONS[i][1]), (float)f));
            fs[i] = f;
            if (i >= 6) continue;
            f += (float)(SEGMENT_LOCATIONS[i][2] + SEGMENT_LOCATIONS[i + 1][2]) * 0.5f;
        }
        modelPartData.addChild(SilverfishEntityModel.getLayerName((int)0), ModelPartBuilder.create().uv(20, 0).cuboid(-5.0f, 0.0f, (float)SEGMENT_LOCATIONS[2][2] * -0.5f, 10.0f, 8.0f, (float)SEGMENT_LOCATIONS[2][2]), ModelTransform.origin((float)0.0f, (float)16.0f, (float)fs[2]));
        modelPartData.addChild(SilverfishEntityModel.getLayerName((int)1), ModelPartBuilder.create().uv(20, 11).cuboid(-3.0f, 0.0f, (float)SEGMENT_LOCATIONS[4][2] * -0.5f, 6.0f, 4.0f, (float)SEGMENT_LOCATIONS[4][2]), ModelTransform.origin((float)0.0f, (float)20.0f, (float)fs[4]));
        modelPartData.addChild(SilverfishEntityModel.getLayerName((int)2), ModelPartBuilder.create().uv(20, 18).cuboid(-3.0f, 0.0f, (float)SEGMENT_LOCATIONS[4][2] * -0.5f, 6.0f, 5.0f, (float)SEGMENT_LOCATIONS[1][2]), ModelTransform.origin((float)0.0f, (float)19.0f, (float)fs[1]));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles((Object)entityRenderState);
        for (int i = 0; i < this.body.length; ++i) {
            this.body[i].yaw = MathHelper.cos((double)(entityRenderState.age * 0.9f + (float)i * 0.15f * (float)Math.PI)) * (float)Math.PI * 0.05f * (float)(1 + Math.abs(i - 2));
            this.body[i].originX = MathHelper.sin((double)(entityRenderState.age * 0.9f + (float)i * 0.15f * (float)Math.PI)) * (float)Math.PI * 0.2f * (float)Math.abs(i - 2);
        }
        this.scales[0].yaw = this.body[2].yaw;
        this.scales[1].yaw = this.body[4].yaw;
        this.scales[1].originX = this.body[4].originX;
        this.scales[2].yaw = this.body[1].yaw;
        this.scales[2].originX = this.body[1].originX;
    }
}

