/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AscendingParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.particle.WhiteAshParticle
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
public class WhiteAshParticle
extends AscendingParticle {
    private static final int COLOR = 12235202;

    protected WhiteAshParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1f, -0.1f, 0.1f, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0f, 20, 0.0125f, false);
        this.red = (float)ColorHelper.getRed((int)12235202) / 255.0f;
        this.green = (float)ColorHelper.getGreen((int)12235202) / 255.0f;
        this.blue = (float)ColorHelper.getBlue((int)12235202) / 255.0f;
    }
}

