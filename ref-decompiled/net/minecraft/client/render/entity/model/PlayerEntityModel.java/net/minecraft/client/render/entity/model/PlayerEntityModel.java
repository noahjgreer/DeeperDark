/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel
extends BipedEntityModel<PlayerEntityRenderState> {
    protected static final String LEFT_SLEEVE = "left_sleeve";
    protected static final String RIGHT_SLEEVE = "right_sleeve";
    protected static final String LEFT_PANTS = "left_pants";
    protected static final String RIGHT_PANTS = "right_pants";
    private final List<ModelPart> parts;
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final boolean thinArms;

    public PlayerEntityModel(ModelPart root, boolean thinArms) {
        super(root, RenderLayers::entityTranslucent);
        this.thinArms = thinArms;
        this.leftSleeve = this.leftArm.getChild(LEFT_SLEEVE);
        this.rightSleeve = this.rightArm.getChild(RIGHT_SLEEVE);
        this.leftPants = this.leftLeg.getChild(LEFT_PANTS);
        this.rightPants = this.rightLeg.getChild(RIGHT_PANTS);
        this.jacket = this.body.getChild("jacket");
        this.parts = List.of(this.head, this.body, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }

    public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
        ModelPartData modelPartData3;
        ModelPartData modelPartData2;
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.25f;
        if (slim) {
            modelPartData2 = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(5.0f, 2.0f, 0.0f));
            modelPartData3 = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(-5.0f, 2.0f, 0.0f));
            modelPartData2.addChild(LEFT_SLEEVE, ModelPartBuilder.create().uv(48, 48).cuboid(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
            modelPartData3.addChild(RIGHT_SLEEVE, ModelPartBuilder.create().uv(40, 32).cuboid(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        } else {
            modelPartData2 = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(5.0f, 2.0f, 0.0f));
            modelPartData3 = modelPartData.getChild("right_arm");
            modelPartData2.addChild(LEFT_SLEEVE, ModelPartBuilder.create().uv(48, 48).cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
            modelPartData3.addChild(RIGHT_SLEEVE, ModelPartBuilder.create().uv(40, 32).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        }
        modelPartData2 = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.origin(1.9f, 12.0f, 0.0f));
        modelPartData3 = modelPartData.getChild("right_leg");
        modelPartData2.addChild(LEFT_PANTS, ModelPartBuilder.create().uv(0, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        modelPartData3.addChild(RIGHT_PANTS, ModelPartBuilder.create().uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        ModelPartData modelPartData4 = modelPartData.getChild("body");
        modelPartData4.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.add(0.25f)), ModelTransform.NONE);
        return modelData;
    }

    public static EquipmentModelData<ModelData> createEquipmentModelData(Dilation hatDilation, Dilation armorDilation) {
        return BipedEntityModel.createEquipmentModelData(hatDilation, armorDilation).map(modelData -> {
            ModelPartData modelPartData = modelData.getRoot();
            ModelPartData modelPartData2 = modelPartData.getChild("left_arm");
            ModelPartData modelPartData3 = modelPartData.getChild("right_arm");
            modelPartData2.addChild(LEFT_SLEEVE, ModelPartBuilder.create(), ModelTransform.NONE);
            modelPartData3.addChild(RIGHT_SLEEVE, ModelPartBuilder.create(), ModelTransform.NONE);
            ModelPartData modelPartData4 = modelPartData.getChild("left_leg");
            ModelPartData modelPartData5 = modelPartData.getChild("right_leg");
            modelPartData4.addChild(LEFT_PANTS, ModelPartBuilder.create(), ModelTransform.NONE);
            modelPartData5.addChild(RIGHT_PANTS, ModelPartBuilder.create(), ModelTransform.NONE);
            ModelPartData modelPartData6 = modelPartData.getChild("body");
            modelPartData6.addChild("jacket", ModelPartBuilder.create(), ModelTransform.NONE);
            return modelData;
        });
    }

    @Override
    public void setAngles(PlayerEntityRenderState playerEntityRenderState) {
        boolean bl;
        this.body.visible = bl = !playerEntityRenderState.spectator;
        this.rightArm.visible = bl;
        this.leftArm.visible = bl;
        this.rightLeg.visible = bl;
        this.leftLeg.visible = bl;
        this.hat.visible = playerEntityRenderState.hatVisible;
        this.jacket.visible = playerEntityRenderState.jacketVisible;
        this.leftPants.visible = playerEntityRenderState.leftPantsLegVisible;
        this.rightPants.visible = playerEntityRenderState.rightPantsLegVisible;
        this.leftSleeve.visible = playerEntityRenderState.leftSleeveVisible;
        this.rightSleeve.visible = playerEntityRenderState.rightSleeveVisible;
        super.setAngles(playerEntityRenderState);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }

    @Override
    public void setArmAngle(PlayerEntityRenderState playerEntityRenderState, Arm arm, MatrixStack matrixStack) {
        this.getRootPart().applyTransform(matrixStack);
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float f = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.originX += f;
            modelPart.applyTransform(matrixStack);
            modelPart.originX -= f;
        } else {
            modelPart.applyTransform(matrixStack);
        }
    }

    public ModelPart getRandomPart(Random random) {
        return Util.getRandom(this.parts, random);
    }
}
