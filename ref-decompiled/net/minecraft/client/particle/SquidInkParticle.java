/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AnimatedParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.particle.SquidInkParticle
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class SquidInkParticle
extends AnimatedParticle {
    SquidInkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.velocityMultiplier = 0.92f;
        this.scale = 0.5f;
        this.setAlpha(1.0f);
        this.setColor(ColorHelper.getRedFloat((int)color), ColorHelper.getGreenFloat((int)color), ColorHelper.getBlueFloat((int)color));
        this.maxAge = (int)(this.scale * 12.0f / (this.random.nextFloat() * 0.8f + 0.2f));
        this.updateSprite(spriteProvider);
        this.collidesWithWorld = false;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    public void tick() {
        super.tick();
        if (!this.dead) {
            this.updateSprite(this.spriteProvider);
            if (this.age > this.maxAge / 2) {
                this.setAlpha(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            }
            if (this.world.getBlockState(BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).isAir()) {
                this.velocityY -= (double)0.0074f;
            }
        }
    }
}

