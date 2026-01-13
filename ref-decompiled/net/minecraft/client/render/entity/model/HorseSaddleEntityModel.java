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
 *  net.minecraft.client.render.entity.model.AbstractHorseEntityModel
 *  net.minecraft.client.render.entity.model.HorseSaddleEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
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
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class HorseSaddleEntityModel
extends AbstractHorseEntityModel<LivingHorseEntityRenderState> {
    private static final String SADDLE = "saddle";
    private static final String LEFT_SADDLE_MOUTH = "left_saddle_mouth";
    private static final String LEFT_SADDLE_LINE = "left_saddle_line";
    private static final String RIGHT_SADDLE_MOUTH = "right_saddle_mouth";
    private static final String RIGHT_SADDLE_LINE = "right_saddle_line";
    private static final String HEAD_SADDLE = "head_saddle";
    private static final String MOUTH_SADDLE_WRAP = "mouth_saddle_wrap";
    private final ModelPart[] saddleLines;

    public HorseSaddleEntityModel(ModelPart modelPart) {
        super(modelPart);
        ModelPart modelPart2 = this.head.getChild("left_saddle_line");
        ModelPart modelPart3 = this.head.getChild("right_saddle_line");
        this.saddleLines = new ModelPart[]{modelPart2, modelPart3};
    }

    public static TexturedModelData getTexturedModelData(boolean baby) {
        return HorseSaddleEntityModel.getUntransformedTexturedModelData((boolean)baby).transform(baby ? BABY_TRANSFORMER : ModelTransformer.NO_OP);
    }

    public static TexturedModelData getUntransformedTexturedModelData(boolean baby) {
        ModelData modelData = baby ? HorseSaddleEntityModel.getBabyModelData((Dilation)Dilation.NONE) : HorseSaddleEntityModel.getModelData((Dilation)Dilation.NONE);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.getChild("body");
        ModelPartData modelPartData3 = modelPartData.getChild("head_parts");
        modelPartData2.addChild("saddle", ModelPartBuilder.create().uv(26, 0).cuboid(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData3.addChild("left_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild("right_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild("left_saddle_line", ModelPartBuilder.create().uv(32, 2).cuboid(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), ModelTransform.rotation((float)-0.5235988f, (float)0.0f, (float)0.0f));
        modelPartData3.addChild("right_saddle_line", ModelPartBuilder.create().uv(32, 2).cuboid(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f), ModelTransform.rotation((float)-0.5235988f, (float)0.0f, (float)0.0f));
        modelPartData3.addChild("head_saddle", ModelPartBuilder.create().uv(1, 1).cuboid(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new Dilation(0.22f)), ModelTransform.NONE);
        modelPartData3.addChild("mouth_saddle_wrap", ModelPartBuilder.create().uv(19, 0).cuboid(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new Dilation(0.2f)), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public void setAngles(LivingHorseEntityRenderState livingHorseEntityRenderState) {
        super.setAngles(livingHorseEntityRenderState);
        for (ModelPart modelPart : this.saddleLines) {
            modelPart.visible = livingHorseEntityRenderState.hasPassengers;
        }
    }
}

