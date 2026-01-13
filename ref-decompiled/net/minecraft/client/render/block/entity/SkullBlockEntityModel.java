/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;

@Environment(value=EnvType.CLIENT)
public abstract class SkullBlockEntityModel
extends Model<SkullModelState> {
    public SkullBlockEntityModel(ModelPart root) {
        super(root, RenderLayers::entityTranslucent);
    }
}

