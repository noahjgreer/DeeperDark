/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.NoRenderParticle
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleTextureSheet
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class NoRenderParticle
extends Particle {
    protected NoRenderParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    protected NoRenderParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
    }

    public ParticleTextureSheet textureSheet() {
        return ParticleTextureSheet.NO_RENDER;
    }
}

