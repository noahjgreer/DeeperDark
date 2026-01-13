/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.NautilusEntityModel
 *  net.minecraft.client.render.entity.model.ZombieNautilusCoralEntityModel
 *  net.minecraft.client.render.entity.state.NautilusEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.NautilusEntityModel;
import net.minecraft.client.render.entity.state.NautilusEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ZombieNautilusCoralEntityModel
extends NautilusEntityModel {
    private final ModelPart corals;

    public ZombieNautilusCoralEntityModel(ModelPart modelPart) {
        super(modelPart);
        ModelPart modelPart2 = this.nautilusRoot.getChild("shell");
        this.corals = modelPart2.getChild("corals");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ZombieNautilusCoralEntityModel.getModelData();
        ModelPartData modelPartData = modelData.getRoot().getChild("root").getChild("shell").addChild("corals", ModelPartBuilder.create(), ModelTransform.origin((float)8.0f, (float)4.5f, (float)-8.0f));
        ModelPartData modelPartData2 = modelPartData.addChild("yellow_coral", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)-11.0f, (float)11.0f));
        modelPartData2.addChild("yellow_coral_second", ModelPartBuilder.create().uv(0, 85).cuboid(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)2.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        modelPartData2.addChild("yellow_coral_first", ModelPartBuilder.create().uv(0, 85).cuboid(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("pink_coral", ModelPartBuilder.create().uv(-8, 94).cuboid(-4.5f, 4.5f, 0.0f, 6.0f, 0.0f, 8.0f), ModelTransform.origin((float)-12.5f, (float)-18.0f, (float)11.0f));
        modelPartData3.addChild("pink_coral_second", ModelPartBuilder.create().uv(-8, 94).cuboid(-3.0f, 0.0f, -4.0f, 6.0f, 0.0f, 8.0f), ModelTransform.of((float)-1.5f, (float)4.5f, (float)4.0f, (float)0.0f, (float)0.0f, (float)1.5708f));
        ModelPartData modelPartData4 = modelPartData.addChild("blue_coral", ModelPartBuilder.create(), ModelTransform.origin((float)-14.0f, (float)0.0f, (float)5.5f));
        modelPartData4.addChild("blue_second", ModelPartBuilder.create().uv(0, 102).cuboid(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)-2.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        modelPartData4.addChild("blue_first", ModelPartBuilder.create().uv(0, 102).cuboid(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.7854f, (float)0.0f));
        ModelPartData modelPartData5 = modelPartData.addChild("red_coral", ModelPartBuilder.create(), ModelTransform.origin((float)0.0f, (float)0.0f, (float)0.0f));
        modelPartData5.addChild("red_coral_second", ModelPartBuilder.create().uv(0, 112).cuboid(-2.5f, -5.5f, 0.0f, 4.0f, 10.0f, 0.0f), ModelTransform.of((float)-0.5f, (float)-1.0f, (float)1.5f, (float)0.0f, (float)-0.829f, (float)0.0f));
        modelPartData5.addChild("red_coral_first", ModelPartBuilder.create().uv(0, 112).cuboid(-4.5f, -5.5f, 0.0f, 6.0f, 10.0f, 0.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.7854f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData, (int)128, (int)128);
    }

    public void setAngles(NautilusEntityRenderState nautilusEntityRenderState) {
        super.setAngles(nautilusEntityRenderState);
        this.corals.visible = nautilusEntityRenderState.armorStack.isEmpty();
    }
}

