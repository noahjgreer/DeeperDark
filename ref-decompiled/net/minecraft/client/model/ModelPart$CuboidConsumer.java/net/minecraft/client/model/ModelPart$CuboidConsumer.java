/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface ModelPart.CuboidConsumer {
    public void accept(MatrixStack.Entry var1, String var2, int var3, ModelPart.Cuboid var4);
}
