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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelPartNames
 *  net.minecraft.client.render.entity.model.GhastEntityModel
 *  net.minecraft.client.render.entity.model.HappyGhastEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.HappyGhastEntityRenderState
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class HappyGhastEntityModel
extends EntityModel<HappyGhastEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.2375f);
    private static final float HARNESSED_SCALE = 0.9375f;
    private final ModelPart[] tentacles = new ModelPart[9];
    private final ModelPart body;

    public HappyGhastEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.body = modelPart.getChild("body");
        for (int i = 0; i < this.tentacles.length; ++i) {
            this.tentacles[i] = this.body.getChild(EntityModelPartNames.getTentacleName((int)i));
        }
    }

    public static TexturedModelData getTexturedModelData(boolean baby, Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f, dilation), ModelTransform.origin((float)0.0f, (float)16.0f, (float)0.0f));
        if (baby) {
            modelPartData2.addChild("inner_body", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 16.0f, 16.0f, dilation.add(-0.5f)), ModelTransform.origin((float)0.0f, (float)8.0f, (float)0.0f));
        }
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)0), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin((float)-3.75f, (float)7.0f, (float)-5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)1), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, dilation), ModelTransform.origin((float)1.25f, (float)7.0f, (float)-5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)2), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 4.0f, 2.0f, dilation), ModelTransform.origin((float)6.25f, (float)7.0f, (float)-5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)3), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin((float)-6.25f, (float)7.0f, (float)0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)4), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin((float)-1.25f, (float)7.0f, (float)0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)5), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f, dilation), ModelTransform.origin((float)3.75f, (float)7.0f, (float)0.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)6), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.origin((float)-3.75f, (float)7.0f, (float)5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)7), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f, dilation), ModelTransform.origin((float)1.25f, (float)7.0f, (float)5.0f));
        modelPartData2.addChild(EntityModelPartNames.getTentacleName((int)8), ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 5.0f, 2.0f, dilation), ModelTransform.origin((float)6.25f, (float)7.0f, (float)5.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64).transform(ModelTransformer.scaling((float)4.0f));
    }

    public void setAngles(HappyGhastEntityRenderState happyGhastEntityRenderState) {
        super.setAngles((Object)happyGhastEntityRenderState);
        if (!happyGhastEntityRenderState.harnessStack.isEmpty()) {
            this.body.xScale = 0.9375f;
            this.body.yScale = 0.9375f;
            this.body.zScale = 0.9375f;
        }
        GhastEntityModel.setTentacleAngles((EntityRenderState)happyGhastEntityRenderState, (ModelPart[])this.tentacles);
    }
}

