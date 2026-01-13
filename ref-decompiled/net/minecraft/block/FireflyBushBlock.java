/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.FireflyBushBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.EnvironmentAttributes
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class FireflyBushBlock
extends PlantBlock
implements Fertilizable {
    private static final double FIREFLY_CHANCE = 0.7;
    private static final double FIREFLY_HORIZONTAL_RADIUS = 10.0;
    private static final double FIREFLY_VERTICAL_RADIUS = 5.0;
    private static final int LIGHT_LEVEL_THRESHOLD = 13;
    private static final int IDLE_SOUND_CHANCE = 30;
    public static final MapCodec<FireflyBushBlock> CODEC = FireflyBushBlock.createCodec(FireflyBushBlock::new);

    public FireflyBushBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected MapCodec<? extends FireflyBushBlock> getCodec() {
        return CODEC;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(30) == 0 && ((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.FIREFLY_BUSH_SOUNDS_AUDIO, pos)).booleanValue() && world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos) <= pos.getY()) {
            world.playSoundAtBlockCenterClient(pos, SoundEvents.BLOCK_FIREFLY_BUSH_IDLE, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        if (world.getLightLevel(pos) <= 13 && random.nextDouble() <= 0.7) {
            double d = (double)pos.getX() + random.nextDouble() * 10.0 - 5.0;
            double e = (double)pos.getY() + random.nextDouble() * 5.0;
            double f = (double)pos.getZ() + random.nextDouble() * 10.0 - 5.0;
            world.addParticleClient((ParticleEffect)ParticleTypes.FIREFLY, d, e, f, 0.0, 0.0, 0.0);
        }
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return Fertilizable.canSpread((WorldView)world, (BlockPos)pos, (BlockState)state);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Fertilizable.findPosToSpreadTo((World)world, (BlockPos)pos, (BlockState)state).ifPresent(blockPos -> world.setBlockState(blockPos, this.getDefaultState()));
    }
}

