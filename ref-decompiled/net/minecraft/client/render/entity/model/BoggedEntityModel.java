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
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.BoggedEntityModel
 *  net.minecraft.client.render.entity.model.SkeletonEntityModel
 *  net.minecraft.client.render.entity.state.BoggedEntityRenderState
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BoggedEntityModel
extends SkeletonEntityModel<BoggedEntityRenderState> {
    private final ModelPart mushrooms;

    public BoggedEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.mushrooms = modelPart.getChild("head").getChild("mushrooms");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)Dilation.NONE, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        SkeletonEntityModel.addLimbs((ModelPartData)modelPartData);
        ModelPartData modelPartData2 = modelPartData.getChild("head").addChild("mushrooms", ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData2.addChild("red_mushroom_1", ModelPartBuilder.create().uv(50, 16).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)3.0f, (float)-8.0f, (float)3.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        modelPartData2.addChild("red_mushroom_2", ModelPartBuilder.create().uv(50, 16).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)3.0f, (float)-8.0f, (float)3.0f, (float)0.0f, (float)2.3561945f, (float)0.0f));
        modelPartData2.addChild("brown_mushroom_1", ModelPartBuilder.create().uv(50, 22).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)-3.0f, (float)-8.0f, (float)-3.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        modelPartData2.addChild("brown_mushroom_2", ModelPartBuilder.create().uv(50, 22).cuboid(-3.0f, -3.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)-3.0f, (float)-8.0f, (float)-3.0f, (float)0.0f, (float)2.3561945f, (float)0.0f));
        modelPartData2.addChild("brown_mushroom_3", ModelPartBuilder.create().uv(50, 28).cuboid(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)-2.0f, (float)-1.0f, (float)4.0f, (float)-1.5707964f, (float)0.0f, (float)0.7853982f));
        modelPartData2.addChild("brown_mushroom_4", ModelPartBuilder.create().uv(50, 28).cuboid(-3.0f, -4.0f, 0.0f, 6.0f, 4.0f, 0.0f), ModelTransform.of((float)-2.0f, (float)-1.0f, (float)4.0f, (float)-1.5707964f, (float)0.0f, (float)2.3561945f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(BoggedEntityRenderState boggedEntityRenderState) {
        super.setAngles((SkeletonEntityRenderState)boggedEntityRenderState);
        this.mushrooms.visible = !boggedEntityRenderState.sheared;
    }
}

