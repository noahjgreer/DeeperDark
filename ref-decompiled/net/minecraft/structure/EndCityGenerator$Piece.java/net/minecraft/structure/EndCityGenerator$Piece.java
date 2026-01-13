/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public static class EndCityGenerator.Piece
extends SimpleStructurePiece {
    public EndCityGenerator.Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation, boolean includeAir) {
        super(StructurePieceType.END_CITY, 0, manager, EndCityGenerator.Piece.getId(template), template, EndCityGenerator.Piece.createPlacementData(includeAir, rotation), pos);
    }

    public EndCityGenerator.Piece(StructureTemplateManager manager, NbtCompound nbt) {
        super(StructurePieceType.END_CITY, nbt, manager, id -> EndCityGenerator.Piece.createPlacementData(nbt.getBoolean("OW", false), nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow()));
    }

    private static StructurePlacementData createPlacementData(boolean includeAir, BlockRotation rotation) {
        BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = includeAir ? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS : BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
        return new StructurePlacementData().setIgnoreEntities(true).addProcessor(blockIgnoreStructureProcessor).setRotation(rotation);
    }

    @Override
    protected Identifier getId() {
        return EndCityGenerator.Piece.getId(this.templateIdString);
    }

    private static Identifier getId(String template) {
        return Identifier.ofVanilla("end_city/" + template);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
        nbt.putBoolean("OW", this.placementData.getProcessors().get(0) == BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
        if (metadata.startsWith("Chest")) {
            BlockPos blockPos = pos.down();
            if (boundingBox.contains(blockPos)) {
                LootableInventory.setLootTable(world, random, blockPos, LootTables.END_CITY_TREASURE_CHEST);
            }
        } else if (boundingBox.contains(pos) && World.isValid(pos)) {
            if (metadata.startsWith("Sentry")) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(world.toServerWorld(), SpawnReason.STRUCTURE);
                if (shulkerEntity != null) {
                    shulkerEntity.setPosition((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
                    world.spawnEntity(shulkerEntity);
                }
            } else if (metadata.startsWith("Elytra")) {
                ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world.toServerWorld(), pos, this.placementData.getRotation().rotate(Direction.SOUTH));
                itemFrameEntity.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
                world.spawnEntity(itemFrameEntity);
            }
        }
    }
}
