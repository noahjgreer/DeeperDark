/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.StrongholdGenerator;

class StrongholdGenerator.1
extends StrongholdGenerator.PieceData {
    StrongholdGenerator.1(Class class_, int i, int j) {
        super(class_, i, j);
    }

    @Override
    public boolean canGenerate(int chainLength) {
        return super.canGenerate(chainLength) && chainLength > 4;
    }
}
