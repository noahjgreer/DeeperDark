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
 *  net.minecraft.client.render.entity.model.ArmPosing
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.ModelWithHat
 *  net.minecraft.client.render.entity.model.ZombieVillagerEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieVillagerRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
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
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityModel<S extends ZombieVillagerRenderState>
extends BipedEntityModel<S>
implements ModelWithHat<S> {
    public ZombieVillagerEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)Dilation.NONE, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", new ModelPartBuilder().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f).uv(24, 0).cuboid(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData3.addChild("hat_rim", ModelPartBuilder.create().uv(30, 47).cuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), ModelTransform.rotation((float)-1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 20.0f, 6.0f, new Dilation(0.05f)), ModelTransform.NONE);
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(44, 22).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)-5.0f, (float)2.0f, (float)0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)5.0f, (float)2.0f, (float)0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)-2.0f, (float)12.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)2.0f, (float)12.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public static TexturedModelData getTexturedModelDataWithoutHead() {
        return ZombieVillagerEntityModel.getTexturedModelData().transform(data -> {
            data.getRoot().resetChildrenParts("head").resetChildrenParts();
            return data;
        });
    }

    public static EquipmentModelData<TexturedModelData> getEquipmentModelData(Dilation hatDilation, Dilation armorDilation) {
        return ZombieVillagerEntityModel.createEquipmentModelData(ZombieVillagerEntityModel::getArmorTexturedModelData, (Dilation)hatDilation, (Dilation)armorDilation).map(modelData -> TexturedModelData.of((ModelData)modelData, (int)64, (int)32));
    }

    private static ModelData getArmorTexturedModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData((Dilation)dilation, (float)0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, dilation.add(0.1f)), ModelTransform.NONE);
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.1f)), ModelTransform.origin((float)-2.0f, (float)12.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation.add(0.1f)), ModelTransform.origin((float)2.0f, (float)12.0f, (float)0.0f));
        modelPartData2.getChild("hat").addChild("hat_rim", ModelPartBuilder.create(), ModelTransform.NONE);
        return modelData;
    }

    public void setAngles(S zombieVillagerRenderState) {
        super.setAngles(zombieVillagerRenderState);
        ArmPosing.zombieArms((ModelPart)this.leftArm, (ModelPart)this.rightArm, (boolean)((ZombieVillagerRenderState)zombieVillagerRenderState).attacking, zombieVillagerRenderState);
    }

    public void rotateArms(ZombieVillagerRenderState zombieVillagerRenderState, MatrixStack matrixStack) {
        this.setArmAngle((BipedEntityRenderState)zombieVillagerRenderState, Arm.RIGHT, matrixStack);
    }
}

