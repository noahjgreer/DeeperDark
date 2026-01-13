/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static class OceanMonumentGenerator.DoubleYZRoomFactory
implements OceanMonumentGenerator.PieceFactory {
    OceanMonumentGenerator.DoubleYZRoomFactory() {
    }

    @Override
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
        if (setting.neighborPresences[Direction.NORTH.getIndex()] && !setting.neighbors[Direction.NORTH.getIndex()].used && setting.neighborPresences[Direction.UP.getIndex()] && !setting.neighbors[Direction.UP.getIndex()].used) {
            OceanMonumentGenerator.PieceSetting pieceSetting = setting.neighbors[Direction.NORTH.getIndex()];
            return pieceSetting.neighborPresences[Direction.UP.getIndex()] && !pieceSetting.neighbors[Direction.UP.getIndex()].used;
        }
        return false;
    }

    @Override
    public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
        setting.used = true;
        setting.neighbors[Direction.NORTH.getIndex()].used = true;
        setting.neighbors[Direction.UP.getIndex()].used = true;
        setting.neighbors[Direction.NORTH.getIndex()].neighbors[Direction.UP.getIndex()].used = true;
        return new OceanMonumentGenerator.DoubleYZRoom(direction, setting);
    }
}
