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
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.ArrowEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.ProjectileEntityRenderState
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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArrowEntityModel
extends EntityModel<ProjectileEntityRenderState> {
    public ArrowEntityModel(ModelPart modelPart) {
        super(modelPart, RenderLayers::entityCutout);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, -2.5f, -2.5f, 0.0f, 5.0f, 5.0f), ModelTransform.of((float)-11.0f, (float)0.0f, (float)0.0f, (float)0.7853982f, (float)0.0f, (float)0.0f).withScale(0.8f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-12.0f, -2.0f, 0.0f, 16.0f, 4.0f, 0.0f, Dilation.NONE, 1.0f, 0.8f);
        modelPartData.addChild("cross_1", modelPartBuilder, ModelTransform.rotation((float)0.7853982f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("cross_2", modelPartBuilder, ModelTransform.rotation((float)2.3561945f, (float)0.0f, (float)0.0f));
        return TexturedModelData.of((ModelData)modelData.transform(modelTransform -> modelTransform.scaled(0.9f)), (int)32, (int)32);
    }

    public void setAngles(ProjectileEntityRenderState projectileEntityRenderState) {
        super.setAngles((Object)projectileEntityRenderState);
        if (projectileEntityRenderState.shake > 0.0f) {
            float f = -MathHelper.sin((double)(projectileEntityRenderState.shake * 3.0f)) * projectileEntityRenderState.shake;
            this.root.roll += f * ((float)Math.PI / 180);
        }
    }
}

