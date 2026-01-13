/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.model.ModelWithHat
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public interface ModelWithHat<T extends EntityRenderState> {
    public void rotateArms(T var1, MatrixStack var2);
}

