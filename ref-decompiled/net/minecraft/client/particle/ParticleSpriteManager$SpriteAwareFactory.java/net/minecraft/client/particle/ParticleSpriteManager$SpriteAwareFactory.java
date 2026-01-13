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
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface ParticleSpriteManager.SpriteAwareFactory<T extends ParticleEffect> {
    public ParticleFactory<T> create(SpriteProvider var1);
}
