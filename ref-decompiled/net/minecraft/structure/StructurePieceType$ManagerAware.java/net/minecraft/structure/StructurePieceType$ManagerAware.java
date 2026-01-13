/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureTemplateManager;

public static interface StructurePieceType.ManagerAware
extends StructurePieceType {
    public StructurePiece load(StructureTemplateManager var1, NbtCompound var2);

    @Override
    default public StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
        return this.load(structureContext.structureTemplateManager(), nbtCompound);
    }
}
