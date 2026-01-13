/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.CrackParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class CrackParticle
extends BillboardParticle {
    private final float sampleU;
    private final float sampleV;
    private final BillboardParticle.RenderType renderType;

    CrackParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        this(clientWorld, d, e, f, sprite);
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g;
        this.velocityY += h;
        this.velocityZ += i;
    }

    protected CrackParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0, sprite);
        this.gravityStrength = 1.0f;
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
        this.renderType = sprite.getAtlasId().equals((Object)SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) ? BillboardParticle.RenderType.BLOCK_ATLAS_TRANSLUCENT : BillboardParticle.RenderType.ITEM_ATLAS_TRANSLUCENT;
    }

    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f);
    }

    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f);
    }

    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f);
    }

    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f);
    }

    public BillboardParticle.RenderType getRenderType() {
        return this.renderType;
    }
}

