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
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.SquidEntityModel
 *  net.minecraft.client.render.entity.state.SquidEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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
import net.minecraft.client.render.entity.state.SquidEntityRenderState;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SquidEntityModel
extends EntityModel<SquidEntityRenderState> {
    public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling((float)0.5f);
    private final ModelPart[] tentacles = new ModelPart[8];

    public SquidEntityModel(ModelPart modelPart) {
        super(modelPart);
        Arrays.setAll(this.tentacles, i -> modelPart.getChild(SquidEntityModel.getTentacleName((int)i)));
    }

    private static String getTentacleName(int index) {
        return "tentacle" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.02f);
        int i = -16;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -8.0f, -6.0f, 12.0f, 16.0f, 12.0f, dilation), ModelTransform.origin((float)0.0f, (float)8.0f, (float)0.0f));
        int j = 8;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(48, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 18.0f, 2.0f);
        for (int k = 0; k < 8; ++k) {
            double d = (double)k * Math.PI * 2.0 / 8.0;
            float f = (float)Math.cos(d) * 5.0f;
            float g = 15.0f;
            float h = (float)Math.sin(d) * 5.0f;
            d = (double)k * Math.PI * -2.0 / 8.0 + 1.5707963267948966;
            float l = (float)d;
            modelPartData.addChild(SquidEntityModel.getTentacleName((int)k), modelPartBuilder, ModelTransform.of((float)f, (float)15.0f, (float)h, (float)0.0f, (float)l, (float)0.0f));
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(SquidEntityRenderState squidEntityRenderState) {
        super.setAngles((Object)squidEntityRenderState);
        for (ModelPart modelPart : this.tentacles) {
            modelPart.pitch = squidEntityRenderState.tentacleAngle;
        }
    }
}

