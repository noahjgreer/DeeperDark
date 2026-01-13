/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.model.BellBlockModel
 *  net.minecraft.client.render.block.entity.model.BellBlockModel$1
 *  net.minecraft.client.render.block.entity.model.BellBlockModel$BellModelState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.model.BellBlockModel;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockModel
extends Model<BellModelState> {
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    public BellBlockModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
        this.bellBody = root.getChild(BELL_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BELL_BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), ModelTransform.origin((float)8.0f, (float)12.0f, (float)8.0f));
        modelPartData2.addChild("bell_base", ModelPartBuilder.create().uv(0, 13).cuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.origin((float)-8.0f, (float)-12.0f, (float)-8.0f));
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)32);
    }

    public void setAngles(BellModelState bellModelState) {
        super.setAngles((Object)bellModelState);
        float f = 0.0f;
        float g = 0.0f;
        if (bellModelState.shakeDirection != null) {
            float h = MathHelper.sin((double)(bellModelState.ticks / (float)Math.PI)) / (4.0f + bellModelState.ticks / 3.0f);
            switch (1.field_61661[bellModelState.shakeDirection.ordinal()]) {
                case 1: {
                    f = -h;
                    break;
                }
                case 2: {
                    f = h;
                    break;
                }
                case 3: {
                    g = -h;
                    break;
                }
                case 4: {
                    g = h;
                }
            }
        }
        this.bellBody.pitch = f;
        this.bellBody.roll = g;
    }
}

