/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AbstractDustParticle
 *  net.minecraft.client.particle.RedDustParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.AbstractDustParticleEffect
 *  net.minecraft.particle.DustParticleEffect
 *  org.joml.Vector3f
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class RedDustParticle
extends AbstractDustParticle<DustParticleEffect> {
    protected RedDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, DustParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, (AbstractDustParticleEffect)parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        Vector3f vector3f = parameters.getColor();
        this.red = this.darken(vector3f.x(), f);
        this.green = this.darken(vector3f.y(), f);
        this.blue = this.darken(vector3f.z(), f);
    }
}

