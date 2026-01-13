/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static interface OceanMonumentGenerator.PieceFactory {
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting var1);

    public OceanMonumentGenerator.Piece generate(Direction var1, OceanMonumentGenerator.PieceSetting var2, Random var3);
}
