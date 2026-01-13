/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.Fertilizable$FertilizableType
 *  net.minecraft.block.MossBlock
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.StructureWorldAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.gen.feature.ConfiguredFeature
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/*
 * Exception performing whole class analysis ignored.
 */
public class MossBlock
extends Block
implements Fertilizable {
    public static final MapCodec<MossBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec((RegistryKey)RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.feature), (App)MossBlock.createSettingsCodec()).apply((Applicative)instance, MossBlock::new));
    private final RegistryKey<ConfiguredFeature<?, ?>> feature;

    public MapCodec<MossBlock> getCodec() {
        return CODEC;
    }

    public MossBlock(RegistryKey<ConfiguredFeature<?, ?>> feature, AbstractBlock.Settings settings) {
        super(settings);
        this.feature = feature;
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap(registry -> registry.getOptional(this.feature)).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate((StructureWorldAccess)world, world.getChunkManager().getChunkGenerator(), random, pos.up()));
    }

    public Fertilizable.FertilizableType getFertilizableType() {
        return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
    }
}

