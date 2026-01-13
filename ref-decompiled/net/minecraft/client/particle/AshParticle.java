/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AscendingParticle
 *  net.minecraft.client.particle.AshParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class AshParticle
extends AscendingParticle {
    protected AshParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1f, -0.1f, 0.1f, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.5f, 20, 0.1f, false);
    }
}

