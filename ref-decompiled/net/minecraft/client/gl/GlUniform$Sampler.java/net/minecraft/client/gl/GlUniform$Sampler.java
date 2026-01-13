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
import net.minecraft.client.gl.GlUniform;

@Environment(value=EnvType.CLIENT)
public record GlUniform.Sampler(int location, int samplerIndex) implements GlUniform
{
}
