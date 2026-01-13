/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.math.random.Random;

static class WoodlandMansionGenerator.FirstFloorRoomPool
extends WoodlandMansionGenerator.RoomPool {
    WoodlandMansionGenerator.FirstFloorRoomPool() {
    }

    @Override
    public String getSmallRoom(Random random) {
        return "1x1_a" + (random.nextInt(5) + 1);
    }

    @Override
    public String getSmallSecretRoom(Random random) {
        return "1x1_as" + (random.nextInt(4) + 1);
    }

    @Override
    public String getMediumFunctionalRoom(Random random, boolean staircase) {
        return "1x2_a" + (random.nextInt(9) + 1);
    }

    @Override
    public String getMediumGenericRoom(Random random, boolean staircase) {
        return "1x2_b" + (random.nextInt(5) + 1);
    }

    @Override
    public String getMediumSecretRoom(Random random) {
        return "1x2_s" + (random.nextInt(2) + 1);
    }

    @Override
    public String getBigRoom(Random random) {
        return "2x2_a" + (random.nextInt(4) + 1);
    }

    @Override
    public String getBigSecretRoom(Random random) {
        return "2x2_s1";
    }
}
