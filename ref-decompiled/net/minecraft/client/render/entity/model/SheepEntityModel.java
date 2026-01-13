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
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.QuadrupedEntityModel
 *  net.minecraft.client.render.entity.model.SheepEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SheepEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
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
import net.minecraft.client.render.entity.state.SheepEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class SheepEntityModel
extends QuadrupedEntityModel<SheepEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 8.0f, 4.0f, 2.0f, 2.0f, 24.0f, Set.of("head"));

    public SheepEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = QuadrupedEntityModel.getModelData((int)12, (boolean)false, (boolean)true, (Dilation)Dilation.NONE);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -4.0f, -6.0f, 6.0f, 6.0f, 8.0f), ModelTransform.origin((float)0.0f, (float)6.0f, (float)-8.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(28, 8).cuboid(-4.0f, -10.0f, -7.0f, 8.0f, 16.0f, 6.0f), ModelTransform.of((float)0.0f, (float)5.0f, (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(SheepEntityRenderState sheepEntityRenderState) {
        super.setAngles((LivingEntityRenderState)sheepEntityRenderState);
        this.head.originY += sheepEntityRenderState.neckAngle * 9.0f * sheepEntityRenderState.ageScale;
        this.head.pitch = sheepEntityRenderState.headAngle;
    }
}

