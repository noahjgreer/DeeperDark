/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static class OceanMonumentGenerator.DoubleXRoomFactory
implements OceanMonumentGenerator.PieceFactory {
    OceanMonumentGenerator.DoubleXRoomFactory() {
    }

    @Override
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
        return setting.neighborPresences[Direction.EAST.getIndex()] && !setting.neighbors[Direction.EAST.getIndex()].used;
    }

    @Override
    public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
        setting.used = true;
        setting.neighbors[Direction.EAST.getIndex()].used = true;
        return new OceanMonumentGenerator.DoubleXRoom(direction, setting);
    }
}
