/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import java.util.ArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.jspecify.annotations.Nullable;

public static class WoodlandMansionGenerator.Piece
extends SimpleStructurePiece {
    public WoodlandMansionGenerator.Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation) {
        this(manager, template, pos, rotation, BlockMirror.NONE);
    }

    public WoodlandMansionGenerator.Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
        super(StructurePieceType.WOODLAND_MANSION, 0, manager, WoodlandMansionGenerator.Piece.getId(template), template, WoodlandMansionGenerator.Piece.createPlacementData(mirror, rotation), pos);
    }

    public WoodlandMansionGenerator.Piece(StructureTemplateManager manager, NbtCompound nbt) {
        super(StructurePieceType.WOODLAND_MANSION, nbt, manager, (Identifier id) -> WoodlandMansionGenerator.Piece.createPlacementData(nbt.get("Mi", BlockMirror.ENUM_NAME_CODEC).orElseThrow(), nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow()));
    }

    @Override
    protected Identifier getId() {
        return WoodlandMansionGenerator.Piece.getId(this.templateIdString);
    }

    private static Identifier getId(String identifier) {
        return Identifier.ofVanilla("woodland_mansion/" + identifier);
    }

    private static StructurePlacementData createPlacementData(BlockMirror mirror, BlockRotation rotation) {
        return new StructurePlacementData().setIgnoreEntities(true).setRotation(rotation).setMirror(mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
        nbt.put("Mi", BlockMirror.ENUM_NAME_CODEC, this.placementData.getMirror());
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
        if (metadata.startsWith("Chest")) {
            BlockRotation blockRotation = this.placementData.getRotation();
            BlockState blockState = Blocks.CHEST.getDefaultState();
            if ("ChestWest".equals(metadata)) {
                blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
            } else if ("ChestEast".equals(metadata)) {
                blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(metadata)) {
                blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(metadata)) {
                blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
            }
            this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
        } else {
            ArrayList<@Nullable MobEntity> list = new ArrayList<MobEntity>();
            switch (metadata) {
                case "Mage": {
                    list.add(EntityType.EVOKER.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                    break;
                }
                case "Warrior": {
                    list.add(EntityType.VINDICATOR.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                    break;
                }
                case "Group of Allays": {
                    int i = world.getRandom().nextInt(3) + 1;
                    for (int j = 0; j < i; ++j) {
                        list.add(EntityType.ALLAY.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            for (MobEntity mobEntity : list) {
                if (mobEntity == null) continue;
                mobEntity.setPersistent();
                mobEntity.refreshPositionAndAngles(pos, 0.0f, 0.0f);
                mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, null);
                world.spawnEntityAndPassengers(mobEntity);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }
}
