/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.NetherFortressGenerator;

static class NetherFortressGenerator.PieceData {
    public final Class<? extends NetherFortressGenerator.Piece> pieceType;
    public final int weight;
    public int generatedCount;
    public final int limit;
    public final boolean repeatable;

    public NetherFortressGenerator.PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit, boolean repeatable) {
        this.pieceType = pieceType;
        this.weight = weight;
        this.limit = limit;
        this.repeatable = repeatable;
    }

    public NetherFortressGenerator.PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit) {
        this(pieceType, weight, limit, false);
    }

    public boolean canGenerate(int chainLength) {
        return this.limit == 0 || this.generatedCount < this.limit;
    }

    public boolean canGenerate() {
        return this.limit == 0 || this.generatedCount < this.limit;
    }
}
