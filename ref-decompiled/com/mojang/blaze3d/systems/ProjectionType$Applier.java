/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package com.mojang.blaze3d.systems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface ProjectionType.Applier {
    public void apply(Matrix4f var1, float var2);
}
