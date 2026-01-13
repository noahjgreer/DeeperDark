/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AscendingParticle
 *  net.minecraft.client.particle.DustPlumeParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class DustPlumeParticle
extends AscendingParticle {
    private static final int COLOR = 12235202;

    protected DustPlumeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.7f, 0.6f, 0.7f, velocityX, velocityY + (double)0.15f, velocityZ, scaleMultiplier, spriteProvider, 0.5f, 7, 0.5f, false);
        float f = this.random.nextFloat() * 0.2f;
        this.red = (float)ColorHelper.getRed((int)12235202) / 255.0f - f;
        this.green = (float)ColorHelper.getGreen((int)12235202) / 255.0f - f;
        this.blue = (float)ColorHelper.getBlue((int)12235202) / 255.0f - f;
    }

    public void tick() {
        this.gravityStrength = 0.88f * this.gravityStrength;
        this.velocityMultiplier = 0.92f * this.velocityMultiplier;
        super.tick();
    }
}

