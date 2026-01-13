/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.CatEntityModel
 *  net.minecraft.client.render.entity.model.FelineEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.CatEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.FelineEntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.CatEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class CatEntityModel
extends FelineEntityModel<CatEntityRenderState> {
    public static final ModelTransformer CAT_TRANSFORMER = ModelTransformer.scaling((float)0.8f);

    public CatEntityModel(ModelPart modelPart) {
        super(modelPart);
    }
}

