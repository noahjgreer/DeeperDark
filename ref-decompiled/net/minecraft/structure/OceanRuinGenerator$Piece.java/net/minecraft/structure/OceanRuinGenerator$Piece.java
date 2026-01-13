/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.OceanRuinStructure;

public static class OceanRuinGenerator.Piece
extends SimpleStructurePiece {
    private final OceanRuinStructure.BiomeTemperature biomeType;
    private final float integrity;
    private final boolean large;

    public OceanRuinGenerator.Piece(StructureTemplateManager structureTemplateManager, Identifier template, BlockPos pos, BlockRotation rotation, float integrity, OceanRuinStructure.BiomeTemperature biomeType, boolean large) {
        super(StructurePieceType.OCEAN_TEMPLE, 0, structureTemplateManager, template, template.toString(), OceanRuinGenerator.Piece.createPlacementData(rotation, integrity, biomeType), pos);
        this.integrity = integrity;
        this.biomeType = biomeType;
        this.large = large;
    }

    private OceanRuinGenerator.Piece(StructureTemplateManager holder, NbtCompound nbt, BlockRotation rotation, float integrity, OceanRuinStructure.BiomeTemperature biomeType, boolean large) {
        super(StructurePieceType.OCEAN_TEMPLE, nbt, holder, identifier -> OceanRuinGenerator.Piece.createPlacementData(rotation, integrity, biomeType));
        this.integrity = integrity;
        this.biomeType = biomeType;
        this.large = large;
    }

    private static StructurePlacementData createPlacementData(BlockRotation rotation, float integrity, OceanRuinStructure.BiomeTemperature temperature) {
        StructureProcessor structureProcessor = temperature == OceanRuinStructure.BiomeTemperature.COLD ? SUSPICIOUS_GRAVEL_PROCESSOR : SUSPICIOUS_SAND_PROCESSOR;
        return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE).addProcessor(new BlockRotStructureProcessor(integrity)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS).addProcessor(structureProcessor);
    }

    public static OceanRuinGenerator.Piece fromNbt(StructureTemplateManager structureTemplateManager, NbtCompound nbt) {
        BlockRotation blockRotation = nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow();
        float f = nbt.getFloat("Integrity", 0.0f);
        OceanRuinStructure.BiomeTemperature biomeTemperature = nbt.get("BiomeType", OceanRuinStructure.BiomeTemperature.ENUM_NAME_CODEC).orElseThrow();
        boolean bl = nbt.getBoolean("IsLarge", false);
        return new OceanRuinGenerator.Piece(structureTemplateManager, nbt, blockRotation, f, biomeTemperature, bl);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
        nbt.putFloat("Integrity", this.integrity);
        nbt.put("BiomeType", OceanRuinStructure.BiomeTemperature.ENUM_NAME_CODEC, this.biomeType);
        nbt.putBoolean("IsLarge", this.large);
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
        DrownedEntity drownedEntity;
        if ("chest".equals(metadata)) {
            world.setBlockState(pos, (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, world.getFluidState(pos).isIn(FluidTags.WATER)), 2);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity) {
                ((ChestBlockEntity)blockEntity).setLootTable(this.large ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
            }
        } else if ("drowned".equals(metadata) && (drownedEntity = EntityType.DROWNED.create(world.toServerWorld(), SpawnReason.STRUCTURE)) != null) {
            drownedEntity.setPersistent();
            drownedEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
            drownedEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null);
            world.spawnEntityAndPassengers(drownedEntity);
            if (pos.getY() > world.getSeaLevel()) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            } else {
                world.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
            }
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
        this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
        BlockPos blockPos = StructureTemplate.transformAround(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), BlockMirror.NONE, this.placementData.getRotation(), BlockPos.ORIGIN).add(this.pos);
        this.pos = new BlockPos(this.pos.getX(), this.getGenerationY(this.pos, world, blockPos), this.pos.getZ());
        super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
    }

    private int getGenerationY(BlockPos start, BlockView world, BlockPos end) {
        int i = start.getY();
        int j = 512;
        int k = i - 1;
        int l = 0;
        for (BlockPos blockPos : BlockPos.iterate(start, end)) {
            int m = blockPos.getX();
            int n = blockPos.getZ();
            int o = start.getY() - 1;
            BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
            BlockState blockState = world.getBlockState(mutable);
            FluidState fluidState = world.getFluidState(mutable);
            while ((blockState.isAir() || fluidState.isIn(FluidTags.WATER) || blockState.isIn(BlockTags.ICE)) && o > world.getBottomY() + 1) {
                mutable.set(m, --o, n);
                blockState = world.getBlockState(mutable);
                fluidState = world.getFluidState(mutable);
            }
            j = Math.min(j, o);
            if (o >= k - 2) continue;
            ++l;
        }
        int p = Math.abs(start.getX() - end.getX());
        if (k - j > 2 && l > p - 2) {
            i = j + 1;
        }
        return i;
    }
}
