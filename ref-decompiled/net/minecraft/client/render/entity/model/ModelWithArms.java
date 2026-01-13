/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.model.ModelWithArms
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Arm
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public interface ModelWithArms<T extends EntityRenderState> {
    public void setArmAngle(T var1, Arm var2, MatrixStack var3);
}

