/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.GustParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class GustParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected GustParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.updateSprite(spriteProvider);
        this.maxAge = 12 + this.random.nextInt(4);
        this.scale = 1.0f;
        this.setBoundingBoxSpacing(1.0f, 1.0f);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.updateSprite(this.spriteProvider);
    }
}

