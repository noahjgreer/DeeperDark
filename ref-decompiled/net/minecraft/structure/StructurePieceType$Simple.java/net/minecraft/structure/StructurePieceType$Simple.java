/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;

public static interface StructurePieceType.Simple
extends StructurePieceType {
    public StructurePiece load(NbtCompound var1);

    @Override
    default public StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
        return this.load(nbtCompound);
    }
}
