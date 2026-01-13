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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.ModelWithHat
 *  net.minecraft.client.render.entity.model.ModelWithHead
 *  net.minecraft.client.render.entity.model.VillagerResemblingModel
 *  net.minecraft.client.render.entity.state.VillagerEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel
extends EntityModel<VillagerEntityRenderState>
implements ModelWithHead,
ModelWithHat<VillagerEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart arms;

    public VillagerResemblingModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
        this.arms = modelPart.getChild("arms");
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.5f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new Dilation(0.51f)), ModelTransform.NONE);
        modelPartData3.addChild("hat_rim", ModelPartBuilder.create().uv(30, 47).cuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), ModelTransform.rotation((float)-1.5707964f, (float)0.0f, (float)0.0f));
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.origin((float)0.0f, (float)-2.0f, (float)0.0f));
        ModelPartData modelPartData4 = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f), ModelTransform.NONE);
        modelPartData4.addChild("jacket", ModelPartBuilder.create().uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 20.0f, 6.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData.addChild("arms", ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(44, 22).cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, true).uv(40, 38).cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), ModelTransform.of((float)0.0f, (float)3.0f, (float)-1.0f, (float)-0.75f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)-2.0f, (float)12.0f, (float)0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.origin((float)2.0f, (float)12.0f, (float)0.0f));
        return modelData;
    }

    public static ModelData getNoHatModelData() {
        ModelData modelData = VillagerResemblingModel.getModelData();
        modelData.getRoot().resetChildrenParts("head").resetChildrenParts();
        return modelData;
    }

    public void setAngles(VillagerEntityRenderState villagerEntityRenderState) {
        super.setAngles((Object)villagerEntityRenderState);
        this.head.yaw = villagerEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = villagerEntityRenderState.pitch * ((float)Math.PI / 180);
        if (villagerEntityRenderState.headRolling) {
            this.head.roll = 0.3f * MathHelper.sin((double)(0.45f * villagerEntityRenderState.age));
            this.head.pitch = 0.4f;
        } else {
            this.head.roll = 0.0f;
        }
        this.rightLeg.pitch = MathHelper.cos((double)(villagerEntityRenderState.limbSwingAnimationProgress * 0.6662f)) * 1.4f * villagerEntityRenderState.limbSwingAmplitude * 0.5f;
        this.leftLeg.pitch = MathHelper.cos((double)(villagerEntityRenderState.limbSwingAnimationProgress * 0.6662f + (float)Math.PI)) * 1.4f * villagerEntityRenderState.limbSwingAmplitude * 0.5f;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void rotateArms(VillagerEntityRenderState villagerEntityRenderState, MatrixStack matrixStack) {
        this.root.applyTransform(matrixStack);
        this.arms.applyTransform(matrixStack);
    }
}

