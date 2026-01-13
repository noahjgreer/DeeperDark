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
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.SalmonEntityModel
 *  net.minecraft.client.render.entity.state.SalmonEntityRenderState
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
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityModel
extends EntityModel<SalmonEntityRenderState> {
    public static final ModelTransformer SMALL_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    public static final ModelTransformer LARGE_TRANSFORMER = ModelTransformer.scaling((float)1.5f);
    private static final String BODY_FRONT = "body_front";
    private static final String BODY_BACK = "body_back";
    private static final float field_54015 = -7.2f;
    private final ModelPart tail;

    public SalmonEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.tail = modelPart.getChild(BODY_BACK);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 20;
        ModelPartData modelPartData2 = modelPartData.addChild(BODY_FRONT, ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)-7.2f));
        ModelPartData modelPartData3 = modelPartData.addChild(BODY_BACK, ModelPartBuilder.create().uv(0, 13).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)0.8000002f));
        modelPartData.addChild("head", ModelPartBuilder.create().uv(22, 0).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)20.0f, (float)-7.2f));
        modelPartData3.addChild("back_fin", ModelPartBuilder.create().uv(20, 10).cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)8.0f));
        modelPartData2.addChild("top_front_fin", ModelPartBuilder.create().uv(2, 1).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)-4.5f, (float)5.0f));
        modelPartData3.addChild("top_back_fin", ModelPartBuilder.create().uv(0, 2).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f), ModelTransform.origin((float)0.0f, (float)-4.5f, (float)-1.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(-4, 0).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of((float)-1.5f, (float)21.5f, (float)-7.2f, (float)0.0f, (float)0.0f, (float)-0.7853982f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of((float)1.5f, (float)21.5f, (float)-7.2f, (float)0.0f, (float)0.0f, (float)0.7853982f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(SalmonEntityRenderState salmonEntityRenderState) {
        super.setAngles((Object)salmonEntityRenderState);
        float f = 1.0f;
        float g = 1.0f;
        if (!salmonEntityRenderState.touchingWater) {
            f = 1.3f;
            g = 1.7f;
        }
        this.tail.yaw = -f * 0.25f * MathHelper.sin((double)(g * 0.6f * salmonEntityRenderState.age));
    }
}

