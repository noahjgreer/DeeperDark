/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public static class IglooGenerator.Piece
extends SimpleStructurePiece {
    public IglooGenerator.Piece(StructureTemplateManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
        super(StructurePieceType.IGLOO, 0, manager, identifier, identifier.toString(), IglooGenerator.Piece.createPlacementData(rotation, identifier), IglooGenerator.Piece.getPosOffset(identifier, pos, yOffset));
    }

    public IglooGenerator.Piece(StructureTemplateManager manager, NbtCompound nbt) {
        super(StructurePieceType.IGLOO, nbt, manager, identifier -> IglooGenerator.Piece.createPlacementData(nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow(), identifier));
    }

    private static StructurePlacementData createPlacementData(BlockRotation rotation, Identifier identifier) {
        return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE).setPosition(OFFSETS.get(identifier)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS).setLiquidSettings(StructureLiquidSettings.IGNORE_WATERLOGGING);
    }

    private static BlockPos getPosOffset(Identifier identifier, BlockPos pos, int yOffset) {
        return pos.add(OFFSETS_FROM_TOP.get(identifier)).down(yOffset);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
        if (!"chest".equals(metadata)) {
            return;
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        BlockEntity blockEntity = world.getBlockEntity(pos.down());
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).setLootTable(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos blockPos4;
        BlockState blockState;
        Identifier identifier = Identifier.of(this.templateIdString);
        StructurePlacementData structurePlacementData = IglooGenerator.Piece.createPlacementData(this.placementData.getRotation(), identifier);
        BlockPos blockPos = OFFSETS_FROM_TOP.get(identifier);
        BlockPos blockPos2 = this.pos.add(StructureTemplate.transform(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, -blockPos.getZ())));
        int i = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
        BlockPos blockPos3 = this.pos;
        this.pos = this.pos.add(0, i - 90 - 1, 0);
        super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
        if (identifier.equals(TOP_TEMPLATE) && !(blockState = world.getBlockState((blockPos4 = this.pos.add(StructureTemplate.transform(structurePlacementData, new BlockPos(3, 0, 5)))).down())).isAir() && !blockState.isOf(Blocks.LADDER)) {
            world.setBlockState(blockPos4, Blocks.SNOW_BLOCK.getDefaultState(), 3);
        }
        this.pos = blockPos3;
    }
}
