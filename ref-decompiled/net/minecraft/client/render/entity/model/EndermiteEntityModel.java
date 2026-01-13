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
 *  net.minecraft.client.render.entity.model.EndermiteEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.util.math.MathHelper
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

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EndermiteEntityModel
extends EntityModel<EntityRenderState> {
    private static final int BODY_SEGMENTS_COUNT = 4;
    private static final int[][] SEGMENT_DIMENSIONS = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] SEGMENT_UVS = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private final ModelPart[] bodySegments = new ModelPart[4];

    public EndermiteEntityModel(ModelPart modelPart) {
        super(modelPart);
        for (int i = 0; i < 4; ++i) {
            this.bodySegments[i] = modelPart.getChild(EndermiteEntityModel.getSegmentName((int)i));
        }
    }

    private static String getSegmentName(int index) {
        return "segment" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = -3.5f;
        for (int i = 0; i < 4; ++i) {
            modelPartData.addChild(EndermiteEntityModel.getSegmentName((int)i), ModelPartBuilder.create().uv(SEGMENT_UVS[i][0], SEGMENT_UVS[i][1]).cuboid((float)SEGMENT_DIMENSIONS[i][0] * -0.5f, 0.0f, (float)SEGMENT_DIMENSIONS[i][2] * -0.5f, (float)SEGMENT_DIMENSIONS[i][0], (float)SEGMENT_DIMENSIONS[i][1], (float)SEGMENT_DIMENSIONS[i][2]), ModelTransform.origin((float)0.0f, (float)(24 - SEGMENT_DIMENSIONS[i][1]), (float)f));
            if (i >= 3) continue;
            f += (float)(SEGMENT_DIMENSIONS[i][2] + SEGMENT_DIMENSIONS[i + 1][2]) * 0.5f;
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(EntityRenderState entityRenderState) {
        super.setAngles((Object)entityRenderState);
        for (int i = 0; i < this.bodySegments.length; ++i) {
            this.bodySegments[i].yaw = MathHelper.cos((double)(entityRenderState.age * 0.9f + (float)i * 0.15f * (float)Math.PI)) * (float)Math.PI * 0.01f * (float)(1 + Math.abs(i - 2));
            this.bodySegments[i].originX = MathHelper.sin((double)(entityRenderState.age * 0.9f + (float)i * 0.15f * (float)Math.PI)) * (float)Math.PI * 0.1f * (float)Math.abs(i - 2);
        }
    }
}

