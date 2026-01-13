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
 *  net.minecraft.client.render.entity.model.TridentRiptideEntityModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
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
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TridentRiptideEntityModel
extends EntityModel<PlayerEntityRenderState> {
    private static final int field_54016 = 2;
    private final ModelPart[] parts = new ModelPart[2];

    public TridentRiptideEntityModel(ModelPart modelPart) {
        super(modelPart);
        for (int i = 0; i < 2; ++i) {
            this.parts[i] = modelPart.getChild(TridentRiptideEntityModel.getPartName((int)i));
        }
    }

    private static String getPartName(int index) {
        return "box" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        for (int i = 0; i < 2; ++i) {
            float f = -3.2f + 9.6f * (float)(i + 1);
            float g = 0.75f * (float)(i + 1);
            modelPartData.addChild(TridentRiptideEntityModel.getPartName((int)i), ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f + f, -8.0f, 16.0f, 32.0f, 16.0f), ModelTransform.NONE.withScale(g));
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
        super.setAngles((Object)playerEntityRenderState);
        for (int i = 0; i < this.parts.length; ++i) {
            float f = playerEntityRenderState.age * (float)(-(45 + (i + 1) * 5));
            this.parts[i].yaw = MathHelper.wrapDegrees((float)f) * ((float)Math.PI / 180);
        }
    }
}

