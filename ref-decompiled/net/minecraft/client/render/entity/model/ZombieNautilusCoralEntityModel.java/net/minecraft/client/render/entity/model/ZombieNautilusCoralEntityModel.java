/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
        ModelPartData modelPartData = modelData.getRoot().getChild("root").getChild("shell").addChild("corals", ModelPartBuilder.create(), ModelTransform.origin(8.0f, 4.5f, -8.0f));
        ModelPartData modelPartData2 = modelPartData.addChild("yellow_coral", ModelPartBuilder.create(), ModelTransform.origin(0.0f, -11.0f, 11.0f));
        modelPartData2.addChild("yellow_coral_second", ModelPartBuilder.create().uv(0, 85).cuboid(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.of(0.0f, 0.0f, 2.0f, 0.0f, -0.7854f, 0.0f));
        modelPartData2.addChild("yellow_coral_first", ModelPartBuilder.create().uv(0, 85).cuboid(-4.5f, -3.5f, 0.0f, 6.0f, 8.0f, 0.0f), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("pink_coral", ModelPartBuilder.create().uv(-8, 94).cuboid(-4.5f, 4.5f, 0.0f, 6.0f, 0.0f, 8.0f), ModelTransform.origin(-12.5f, -18.0f, 11.0f));
        modelPartData3.addChild("pink_coral_second", ModelPartBuilder.create().uv(-8, 94).cuboid(-3.0f, 0.0f, -4.0f, 6.0f, 0.0f, 8.0f), ModelTransform.of(-1.5f, 4.5f, 4.0f, 0.0f, 0.0f, 1.5708f));
        ModelPartData modelPartData4 = modelPartData.addChild("blue_coral", ModelPartBuilder.create(), ModelTransform.origin(-14.0f, 0.0f, 5.5f));
        modelPartData4.addChild("blue_second", ModelPartBuilder.create().uv(0, 102).cuboid(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), ModelTransform.of(0.0f, 0.0f, -2.0f, 0.0f, 0.7854f, 0.0f));
        modelPartData4.addChild("blue_first", ModelPartBuilder.create().uv(0, 102).cuboid(-3.5f, -5.5f, 0.0f, 5.0f, 10.0f, 0.0f), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f));
        ModelPartData modelPartData5 = modelPartData.addChild("red_coral", ModelPartBuilder.create(), ModelTransform.origin(0.0f, 0.0f, 0.0f));
        modelPartData5.addChild("red_coral_second", ModelPartBuilder.create().uv(0, 112).cuboid(-2.5f, -5.5f, 0.0f, 4.0f, 10.0f, 0.0f), ModelTransform.of(-0.5f, -1.0f, 1.5f, 0.0f, -0.829f, 0.0f));
        modelPartData5.addChild("red_coral_first", ModelPartBuilder.create().uv(0, 112).cuboid(-4.5f, -5.5f, 0.0f, 6.0f, 10.0f, 0.0f), ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(NautilusEntityRenderState nautilusEntityRenderState) {
        super.setAngles(nautilusEntityRenderState);
        this.corals.visible = nautilusEntityRenderState.armorStack.isEmpty();
    }
}
