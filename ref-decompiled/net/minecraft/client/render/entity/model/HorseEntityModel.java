/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.AbstractHorseEntityModel
 *  net.minecraft.client.render.entity.model.HorseEntityModel
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class HorseEntityModel
extends AbstractHorseEntityModel<LivingHorseEntityRenderState> {
    public HorseEntityModel(ModelPart modelPart) {
        super(modelPart);
    }
}

