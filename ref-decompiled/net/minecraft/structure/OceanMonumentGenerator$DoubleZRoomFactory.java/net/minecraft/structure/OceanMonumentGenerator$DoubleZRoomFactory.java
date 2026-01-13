/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static class OceanMonumentGenerator.DoubleZRoomFactory
implements OceanMonumentGenerator.PieceFactory {
    OceanMonumentGenerator.DoubleZRoomFactory() {
    }

    @Override
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
        return setting.neighborPresences[Direction.NORTH.getIndex()] && !setting.neighbors[Direction.NORTH.getIndex()].used;
    }

    @Override
    public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
        OceanMonumentGenerator.PieceSetting pieceSetting = setting;
        if (!setting.neighborPresences[Direction.NORTH.getIndex()] || setting.neighbors[Direction.NORTH.getIndex()].used) {
            pieceSetting = setting.neighbors[Direction.SOUTH.getIndex()];
        }
        pieceSetting.used = true;
        pieceSetting.neighbors[Direction.NORTH.getIndex()].used = true;
        return new OceanMonumentGenerator.DoubleZRoom(direction, pieceSetting);
    }
}
