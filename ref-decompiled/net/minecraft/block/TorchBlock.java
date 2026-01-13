/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractTorchBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.TorchBlock
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.SimpleParticleType
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class TorchBlock
extends AbstractTorchBlock {
    protected static final MapCodec<SimpleParticleType> PARTICLE_TYPE_CODEC = Registries.PARTICLE_TYPE.getCodec().comapFlatMap(particleType -> {
        DataResult dataResult;
        if (particleType instanceof SimpleParticleType) {
            SimpleParticleType simpleParticleType = (SimpleParticleType)particleType;
            dataResult = DataResult.success((Object)simpleParticleType);
        } else {
            dataResult = DataResult.error(() -> "Not a SimpleParticleType: " + String.valueOf(particleType));
        }
        return dataResult;
    }, particleType -> particleType).fieldOf("particle_options");
    public static final MapCodec<TorchBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)PARTICLE_TYPE_CODEC.forGetter(block -> block.particle), (App)TorchBlock.createSettingsCodec()).apply((Applicative)instance, TorchBlock::new));
    protected final SimpleParticleType particle;

    public MapCodec<? extends TorchBlock> getCodec() {
        return CODEC;
    }

    public TorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings) {
        super(settings);
        this.particle = particle;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.7;
        double f = (double)pos.getZ() + 0.5;
        world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
        world.addParticleClient((ParticleEffect)this.particle, d, e, f, 0.0, 0.0, 0.0);
    }
}

