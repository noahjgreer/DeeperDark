/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static class OceanMonumentGenerator.SimpleRoomTopFactory
implements OceanMonumentGenerator.PieceFactory {
    OceanMonumentGenerator.SimpleRoomTopFactory() {
    }

    @Override
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
        return !setting.neighborPresences[Direction.WEST.getIndex()] && !setting.neighborPresences[Direction.EAST.getIndex()] && !setting.neighborPresences[Direction.NORTH.getIndex()] && !setting.neighborPresences[Direction.SOUTH.getIndex()] && !setting.neighborPresences[Direction.UP.getIndex()];
    }

    @Override
    public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
        setting.used = true;
        return new OceanMonumentGenerator.SimpleRoomTop(direction, setting);
    }
}
