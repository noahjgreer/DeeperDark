/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class CrackParticle.ItemFactory
extends CrackParticle.Factory<ItemStackParticleEffect> {
    @Override
    public Particle createParticle(ItemStackParticleEffect itemStackParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        return new CrackParticle(clientWorld, d, e, f, g, h, i, this.getSprite(itemStackParticleEffect.getItemStack(), clientWorld, random));
    }
}
