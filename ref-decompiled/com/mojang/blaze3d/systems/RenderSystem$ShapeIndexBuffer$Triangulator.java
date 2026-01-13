/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.systems;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static interface RenderSystem.ShapeIndexBuffer.Triangulator {
    public void accept(IntConsumer var1, int var2);
}
