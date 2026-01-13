/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.SpellParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SpellParticle
extends BillboardParticle {
    private static final Random RANDOM = Random.create();
    private final SpriteProvider spriteProvider;
    private float defaultAlpha = 1.0f;

    SpellParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.5 - RANDOM.nextDouble(), velocityY, 0.5 - RANDOM.nextDouble(), spriteProvider.getFirst());
        this.velocityMultiplier = 0.96f;
        this.gravityStrength = -0.1f;
        this.ascending = true;
        this.spriteProvider = spriteProvider;
        this.velocityY *= (double)0.2f;
        if (velocityX == 0.0 && velocityZ == 0.0) {
            this.velocityX *= (double)0.1f;
            this.velocityZ *= (double)0.1f;
        }
        this.scale *= 0.75f;
        this.maxAge = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.updateSprite(spriteProvider);
        if (this.isInvisible()) {
            this.setAlpha(0.0f);
        }
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
        this.alpha = this.isInvisible() ? 0.0f : MathHelper.lerp((float)0.05f, (float)this.alpha, (float)this.defaultAlpha);
    }

    protected void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.defaultAlpha = alpha;
    }

    private boolean isInvisible() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        return clientPlayerEntity != null && clientPlayerEntity.getEyePos().squaredDistanceTo(this.x, this.y, this.z) <= 9.0 && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass();
    }
}

