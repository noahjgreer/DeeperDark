/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ElderGuardianParticleModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.util.Unit
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianParticleModel
extends Model<Unit> {
    public ElderGuardianParticleModel(ModelPart part) {
        super(part, RenderLayers::entityCutoutNoCull);
    }
}

