/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

static class OceanMonumentGenerator.DoubleYRoomFactory
implements OceanMonumentGenerator.PieceFactory {
    OceanMonumentGenerator.DoubleYRoomFactory() {
    }

    @Override
    public boolean canGenerate(OceanMonumentGenerator.PieceSetting setting) {
        return setting.neighborPresences[Direction.UP.getIndex()] && !setting.neighbors[Direction.UP.getIndex()].used;
    }

    @Override
    public OceanMonumentGenerator.Piece generate(Direction direction, OceanMonumentGenerator.PieceSetting setting, Random random) {
        setting.used = true;
        setting.neighbors[Direction.UP.getIndex()].used = true;
        return new OceanMonumentGenerator.DoubleYRoom(direction, setting);
    }
}
