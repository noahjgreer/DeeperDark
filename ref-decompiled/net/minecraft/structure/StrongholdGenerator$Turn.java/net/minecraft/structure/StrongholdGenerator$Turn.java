/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;

public static abstract class StrongholdGenerator.Turn
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 5;
    protected static final int SIZE_Y = 5;
    protected static final int SIZE_Z = 5;

    protected StrongholdGenerator.Turn(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
        super(structurePieceType, i, blockBox);
    }

    public StrongholdGenerator.Turn(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
    }
}
