/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public static class ShipwreckGenerator.Piece
extends SimpleStructurePiece {
    private final boolean grounded;

    public ShipwreckGenerator.Piece(StructureTemplateManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, boolean grounded) {
        super(StructurePieceType.SHIPWRECK, 0, manager, identifier, identifier.toString(), ShipwreckGenerator.Piece.createPlacementData(rotation), pos);
        this.grounded = grounded;
    }

    public ShipwreckGenerator.Piece(StructureTemplateManager manager, NbtCompound nbt) {
        super(StructurePieceType.SHIPWRECK, nbt, manager, id -> ShipwreckGenerator.Piece.createPlacementData(nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow()));
        this.grounded = nbt.getBoolean("isBeached", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("isBeached", this.grounded);
        nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
    }

    private static StructurePlacementData createPlacementData(BlockRotation rotation) {
        return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE).setPosition(DEFAULT_POSITION).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
        RegistryKey<LootTable> registryKey = LOOT_TABLES.get(metadata);
        if (registryKey != null) {
            LootableInventory.setLootTable(world, random, pos.down(), registryKey);
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.isTooLargeForNormalGeneration()) {
            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
            return;
        }
        int i = world.getTopYInclusive() + 1;
        int j = 0;
        Vec3i vec3i = this.template.getSize();
        Heightmap.Type type = this.grounded ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
        int k = vec3i.getX() * vec3i.getZ();
        if (k == 0) {
            j = world.getTopY(type, this.pos.getX(), this.pos.getZ());
        } else {
            BlockPos blockPos = this.pos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);
            for (BlockPos blockPos2 : BlockPos.iterate(this.pos, blockPos)) {
                int l = world.getTopY(type, blockPos2.getX(), blockPos2.getZ());
                j += l;
                i = Math.min(i, l);
            }
            j /= k;
        }
        this.setY(this.grounded ? this.findGroundedY(i, random) : j);
        super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
    }

    public boolean isTooLargeForNormalGeneration() {
        Vec3i vec3i = this.template.getSize();
        return vec3i.getX() > 32 || vec3i.getY() > 32;
    }

    public int findGroundedY(int y, Random random) {
        return y - this.template.getSize().getY() / 2 - random.nextInt(3);
    }

    public void setY(int y) {
        this.pos = new BlockPos(this.pos.getX(), y, this.pos.getZ());
    }
}
