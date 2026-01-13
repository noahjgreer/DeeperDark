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
 *  net.minecraft.client.render.entity.model.DonkeyEntityModel
 *  net.minecraft.client.render.entity.model.HorseSaddleEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.DonkeyEntityRenderState
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
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DonkeyEntityModel
extends AbstractHorseEntityModel<DonkeyEntityRenderState> {
    public static final float field_55113 = 0.87f;
    public static final float field_55114 = 0.92f;
    private static final ModelTransformer DONKEY_PARTS_ADDER = data -> {
        DonkeyEntityModel.addDonkeyParts((ModelPartData)data.getRoot());
        return data;
    };
    private final ModelPart leftChest;
    private final ModelPart rightChest;

    public DonkeyEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftChest = this.body.getChild("left_chest");
        this.rightChest = this.body.getChild("right_chest");
    }

    public static TexturedModelData getTexturedModelData(float scale) {
        return TexturedModelData.of((ModelData)AbstractHorseEntityModel.getModelData((Dilation)Dilation.NONE), (int)64, (int)64).transform(DONKEY_PARTS_ADDER).transform(ModelTransformer.scaling((float)scale));
    }

    public static TexturedModelData getBabyTexturedModelData(float scale) {
        return TexturedModelData.of((ModelData)AbstractHorseEntityModel.getBabyModelData((Dilation)Dilation.NONE), (int)64, (int)64).transform(DONKEY_PARTS_ADDER).transform(BABY_TRANSFORMER).transform(ModelTransformer.scaling((float)scale));
    }

    public static TexturedModelData getSaddleTexturedModelData(float scale, boolean baby) {
        return HorseSaddleEntityModel.getUntransformedTexturedModelData((boolean)baby).transform(DONKEY_PARTS_ADDER).transform(baby ? AbstractHorseEntityModel.BABY_TRANSFORMER : ModelTransformer.NO_OP).transform(ModelTransformer.scaling((float)scale));
    }

    private static void addDonkeyParts(ModelPartData root) {
        ModelPartData modelPartData = root.getChild("body");
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 21).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 8.0f, 3.0f);
        modelPartData.addChild("left_chest", modelPartBuilder, ModelTransform.of((float)6.0f, (float)-8.0f, (float)0.0f, (float)0.0f, (float)-1.5707964f, (float)0.0f));
        modelPartData.addChild("right_chest", modelPartBuilder, ModelTransform.of((float)-6.0f, (float)-8.0f, (float)0.0f, (float)0.0f, (float)1.5707964f, (float)0.0f));
        ModelPartData modelPartData2 = root.getChild("head_parts").getChild("head");
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 12).cuboid(-1.0f, -7.0f, 0.0f, 2.0f, 7.0f, 1.0f);
        modelPartData2.addChild("left_ear", modelPartBuilder2, ModelTransform.of((float)1.25f, (float)-10.0f, (float)4.0f, (float)0.2617994f, (float)0.0f, (float)0.2617994f));
        modelPartData2.addChild("right_ear", modelPartBuilder2, ModelTransform.of((float)-1.25f, (float)-10.0f, (float)4.0f, (float)0.2617994f, (float)0.0f, (float)-0.2617994f));
    }

    public void setAngles(DonkeyEntityRenderState donkeyEntityRenderState) {
        super.setAngles((LivingHorseEntityRenderState)donkeyEntityRenderState);
        this.leftChest.visible = donkeyEntityRenderState.hasChest;
        this.rightChest.visible = donkeyEntityRenderState.hasChest;
    }
}

