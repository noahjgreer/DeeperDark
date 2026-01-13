/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.Fertilizable$FertilizableType
 *  net.minecraft.block.NyliumBlock
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.StructureWorldAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.chunk.light.ChunkLightProvider
 *  net.minecraft.world.gen.chunk.ChunkGenerator
 *  net.minecraft.world.gen.feature.ConfiguredFeature
 *  net.minecraft.world.gen.feature.NetherConfiguredFeatures
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NetherConfiguredFeatures;

/*
 * Exception performing whole class analysis ignored.
 */
public class NyliumBlock
extends Block
implements Fertilizable {
    public static final MapCodec<NyliumBlock> CODEC = NyliumBlock.createCodec(NyliumBlock::new);

    public MapCodec<NyliumBlock> getCodec() {
        return CODEC;
    }

    public NyliumBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private static boolean stayAlive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        int i = ChunkLightProvider.getRealisticOpacity((BlockState)state, (BlockState)blockState, (Direction)Direction.UP, (int)blockState.getOpacity());
        return i < 15;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!NyliumBlock.stayAlive((BlockState)state, (WorldView)world, (BlockPos)pos)) {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
        }
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos);
        BlockPos blockPos = pos.up();
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.CONFIGURED_FEATURE);
        if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
            this.generate(registry, NetherConfiguredFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, world, chunkGenerator, random, blockPos);
        } else if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
            this.generate(registry, NetherConfiguredFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, world, chunkGenerator, random, blockPos);
            this.generate(registry, NetherConfiguredFeatures.NETHER_SPROUTS_BONEMEAL, world, chunkGenerator, random, blockPos);
            if (random.nextInt(8) == 0) {
                this.generate(registry, NetherConfiguredFeatures.TWISTING_VINES_BONEMEAL, world, chunkGenerator, random, blockPos);
            }
        }
    }

    private void generate(Registry<ConfiguredFeature<?, ?>> registry, RegistryKey<ConfiguredFeature<?, ?>> key, ServerWorld world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
        registry.getOptional(key).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate((StructureWorldAccess)world, chunkGenerator, random, pos));
    }

    public Fertilizable.FertilizableType getFertilizableType() {
        return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
    }
}

