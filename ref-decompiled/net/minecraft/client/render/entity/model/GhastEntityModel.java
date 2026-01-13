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
 *  net.minecraft.client.render.entity.model.EntityModelPartNames
 *  net.minecraft.client.render.entity.model.GhastEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.GhastEntityRenderState
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GhastEntityModel
extends EntityModel<GhastEntityRenderState> {
    private final ModelPart[] tentacles = new ModelPart[9];

    public GhastEntityModel(ModelPart modelPart) {
        super(modelPart);
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = modelPart.getChild(EntityModelPartNames.getTentacleName((int)i));
        }
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), ModelTransform.origin((float)0.0f, (float)17.6f, (float)0.0f));
        Random random = Random.create((long)1660L);
        for (int i = 0; i < 9; ++i) {
            float f = (((float)(i % 3) - (float)(i / 3 % 2) * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            float g = ((float)(i / 3) / 2.0f * 2.0f - 1.0f) * 5.0f;
            int j = random.nextInt(7) + 8;
            modelPartData.addChild(EntityModelPartNames.getTentacleName((int)i), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, (float)j, 2.0f), ModelTransform.origin((float)f, (float)24.6f, (float)g));
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32).transform(ModelTransformer.scaling((float)4.5f));
    }

    public void setAngles(GhastEntityRenderState ghastEntityRenderState) {
        super.setAngles((Object)ghastEntityRenderState);
        GhastEntityModel.setTentacleAngles((EntityRenderState)ghastEntityRenderState, (ModelPart[])this.tentacles);
    }

    public static void setTentacleAngles(EntityRenderState state, ModelPart[] tentacles) {
        for (int i = 0; i < tentacles.length; ++i) {
            tentacles[i].pitch = 0.2f * MathHelper.sin((double)(state.age * 0.3f + (float)i)) + 0.4f;
        }
    }
}

