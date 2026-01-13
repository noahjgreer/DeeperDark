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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public interface ModelWithHead {
    public ModelPart getHead();

    default public void applyTransform(MatrixStack matrices) {
        this.getHead().applyTransform(matrices);
    }
}
