/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlSampler;
import net.minecraft.client.texture.GlTextureView;

@Environment(value=EnvType.CLIENT)
protected record RenderPassImpl.SamplerUniform(GlTextureView view, GlSampler sampler) {
}
