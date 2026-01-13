/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.LeavesBlock
 *  net.minecraft.block.TintedParticleLeavesBlock
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleType
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.ParticleUtil
 *  net.minecraft.particle.TintedParticleEffect
 *  net.minecraft.util.dynamic.Codecs
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class TintedParticleLeavesBlock
extends LeavesBlock {
    public static final MapCodec<TintedParticleLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.rangedInclusiveFloat((float)0.0f, (float)1.0f).fieldOf("leaf_particle_chance").forGetter(tintedParticleLeavesBlock -> Float.valueOf(tintedParticleLeavesBlock.leafParticleChance)), (App)TintedParticleLeavesBlock.createSettingsCodec()).apply((Applicative)instance, TintedParticleLeavesBlock::new));

    public TintedParticleLeavesBlock(float f, AbstractBlock.Settings settings) {
        super(f, settings);
    }

    protected void spawnLeafParticle(World world, BlockPos pos, Random random) {
        TintedParticleEffect tintedParticleEffect = TintedParticleEffect.create((ParticleType)ParticleTypes.TINTED_LEAVES, (int)world.getBlockColor(pos));
        ParticleUtil.spawnParticle((World)world, (BlockPos)pos, (Random)random, (ParticleEffect)tintedParticleEffect);
    }

    public MapCodec<? extends TintedParticleLeavesBlock> getCodec() {
        return CODEC;
    }
}

