/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import com.mojang.blaze3d.systems.RenderPass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.texture.TextureManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface OrderedRenderCommandQueue.LayeredCustom {
    public  @Nullable BillboardParticleSubmittable.Buffers submit(LayeredCustomCommandRenderer.VerticesCache var1);

    public void render(BillboardParticleSubmittable.Buffers var1, LayeredCustomCommandRenderer.VerticesCache var2, RenderPass var3, TextureManager var4, boolean var5);
}
