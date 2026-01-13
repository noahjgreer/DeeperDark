/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.RenderCommandQueue
 */
package net.minecraft.client.render.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.RenderCommandQueue;

@Environment(value=EnvType.CLIENT)
public interface OrderedRenderCommandQueue
extends RenderCommandQueue {
    public RenderCommandQueue getBatchingQueue(int var1);
}

