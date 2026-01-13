/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleGroup;

@Environment(value=EnvType.CLIENT)
class WaterSuspendParticle.SporeBlossomAirFactory.1
extends WaterSuspendParticle {
    WaterSuspendParticle.SporeBlossomAirFactory.1(WaterSuspendParticle.SporeBlossomAirFactory sporeBlossomAirFactory, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
    }

    @Override
    public Optional<ParticleGroup> getGroup() {
        return Optional.of(ParticleGroup.SPORE_BLOSSOM_AIR);
    }
}
