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
 *  net.minecraft.client.render.entity.model.LlamaEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.LlamaEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import java.util.Map;
import java.util.function.UnaryOperator;
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
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel
extends EntityModel<LlamaEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = LlamaEntityModel::transformBaby;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightChest;
    private final ModelPart leftChest;

    public LlamaEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightChest = modelPart.getChild("right_chest");
        this.leftChest = modelPart.getChild("left_chest");
        this.rightHindLeg = modelPart.getChild("right_hind_leg");
        this.leftHindLeg = modelPart.getChild("left_hind_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, dilation).uv(0, 14).cuboid("neck", -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, dilation).uv(17, 0).cuboid("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation).uv(17, 0).cuboid("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation), ModelTransform.origin((float)0.0f, (float)7.0f, (float)-6.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(29, 0).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, dilation), ModelTransform.of((float)0.0f, (float)5.0f, (float)2.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_chest", ModelPartBuilder.create().uv(45, 28).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of((float)-8.5f, (float)3.0f, (float)3.0f, (float)0.0f, (float)1.5707964f, (float)0.0f));
        modelPartData.addChild("left_chest", ModelPartBuilder.create().uv(45, 41).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of((float)5.5f, (float)3.0f, (float)3.0f, (float)0.0f, (float)1.5707964f, (float)0.0f));
        int i = 4;
        int j = 14;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin((float)-3.5f, (float)10.0f, (float)6.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin((float)3.5f, (float)10.0f, (float)6.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin((float)-3.5f, (float)10.0f, (float)-5.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin((float)3.5f, (float)10.0f, (float)-5.0f));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)64);
    }

    private static ModelData transformBaby(ModelData modelData) {
        float f = 2.0f;
        float g = 0.7f;
        float h = 1.1f;
        UnaryOperator unaryOperator = modelTransform -> modelTransform.moveOrigin(0.0f, 21.0f, 3.52f).scaled(0.71428573f, 0.64935064f, 0.7936508f);
        UnaryOperator unaryOperator2 = modelTransform -> modelTransform.moveOrigin(0.0f, 33.0f, 0.0f).scaled(0.625f, 0.45454544f, 0.45454544f);
        UnaryOperator unaryOperator3 = modelTransform -> modelTransform.moveOrigin(0.0f, 33.0f, 0.0f).scaled(0.45454544f, 0.41322312f, 0.45454544f);
        ModelData modelData2 = new ModelData();
        for (Map.Entry entry : modelData.getRoot().getChildren()) {
            String string = (String)entry.getKey();
            ModelPartData modelPartData = (ModelPartData)entry.getValue();
            UnaryOperator unaryOperator4 = switch (string) {
                case "head" -> unaryOperator;
                case "body" -> unaryOperator2;
                default -> unaryOperator3;
            };
            modelData2.getRoot().addChild(string, modelPartData.applyTransformer(unaryOperator4));
        }
        return modelData2;
    }

    public void setAngles(LlamaEntityRenderState llamaEntityRenderState) {
        super.setAngles((Object)llamaEntityRenderState);
        this.head.pitch = llamaEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = llamaEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float f = llamaEntityRenderState.limbSwingAmplitude;
        float g = llamaEntityRenderState.limbSwingAnimationProgress;
        this.rightHindLeg.pitch = MathHelper.cos((double)(g * 0.6662f)) * 1.4f * f;
        this.leftHindLeg.pitch = MathHelper.cos((double)(g * 0.6662f + (float)Math.PI)) * 1.4f * f;
        this.rightFrontLeg.pitch = MathHelper.cos((double)(g * 0.6662f + (float)Math.PI)) * 1.4f * f;
        this.leftFrontLeg.pitch = MathHelper.cos((double)(g * 0.6662f)) * 1.4f * f;
        this.rightChest.visible = llamaEntityRenderState.hasChest;
        this.leftChest.visible = llamaEntityRenderState.hasChest;
    }
}

