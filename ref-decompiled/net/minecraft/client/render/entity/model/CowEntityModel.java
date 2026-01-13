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
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.CowEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.QuadrupedEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CowEntityModel
extends QuadrupedEntityModel<LivingEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 8.0f, 6.0f, Set.of("head"));
    private static final int field_56493 = 12;

    public CowEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = CowEntityModel.getModelData();
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(1, 33).cuboid(-3.0f, 1.0f, -7.0f, 6.0f, 3.0f, 1.0f).uv(22, 0).cuboid("right_horn", -5.0f, -5.0f, -5.0f, 1.0f, 3.0f, 1.0f).uv(22, 0).cuboid("left_horn", 4.0f, -5.0f, -5.0f, 1.0f, 3.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)4.0f, (float)-8.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 4).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).uv(52, 0).cuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), ModelTransform.of((float)0.0f, (float)5.0f, (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().mirrored().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        modelPartData.addChild("right_hind_leg", modelPartBuilder2, ModelTransform.origin((float)-4.0f, (float)12.0f, (float)7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin((float)4.0f, (float)12.0f, (float)7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.origin((float)-4.0f, (float)12.0f, (float)-5.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin((float)4.0f, (float)12.0f, (float)-5.0f));
        return modelData;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

