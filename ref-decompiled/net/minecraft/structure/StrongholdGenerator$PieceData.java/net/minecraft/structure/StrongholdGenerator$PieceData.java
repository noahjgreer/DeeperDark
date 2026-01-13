/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.StrongholdGenerator;

static class StrongholdGenerator.PieceData {
    public final Class<? extends StrongholdGenerator.Piece> pieceType;
    public final int weight;
    public int generatedCount;
    public final int limit;

    public StrongholdGenerator.PieceData(Class<? extends StrongholdGenerator.Piece> pieceType, int weight, int limit) {
        this.pieceType = pieceType;
        this.weight = weight;
        this.limit = limit;
    }

    public boolean canGenerate(int chainLength) {
        return this.limit == 0 || this.generatedCount < this.limit;
    }

    public boolean canGenerate() {
        return this.limit == 0 || this.generatedCount < this.limit;
    }
}
