/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 *  net.minecraft.client.render.entity.model.PiglinEntityModel
 *  net.minecraft.client.render.entity.model.PiglinHeadEntityModel
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;

@Environment(value=EnvType.CLIENT)
public class PiglinHeadEntityModel
extends SkullBlockEntityModel {
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public PiglinHeadEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.leftEar = this.head.getChild("left_ear");
        this.rightEar = this.head.getChild("right_ear");
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        PiglinEntityModel.getModelPartData((Dilation)Dilation.NONE, (ModelData)modelData);
        return modelData;
    }

    public void setAngles(SkullBlockEntityModel.SkullModelState skullModelState) {
        super.setAngles((Object)skullModelState);
        this.head.yaw = skullModelState.yaw * ((float)Math.PI / 180);
        this.head.pitch = skullModelState.pitch * ((float)Math.PI / 180);
        float f = 1.2f;
        this.leftEar.roll = (float)(-(Math.cos(skullModelState.poweredTicks * (float)Math.PI * 0.2f * 1.2f) + 2.5)) * 0.2f;
        this.rightEar.roll = (float)(Math.cos(skullModelState.poweredTicks * (float)Math.PI * 0.2f) + 2.5) * 0.2f;
    }
}

