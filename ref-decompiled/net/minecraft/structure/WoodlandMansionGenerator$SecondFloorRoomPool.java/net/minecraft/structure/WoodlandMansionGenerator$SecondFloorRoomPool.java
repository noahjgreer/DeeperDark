/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.math.random.Random;

static class WoodlandMansionGenerator.SecondFloorRoomPool
extends WoodlandMansionGenerator.RoomPool {
    WoodlandMansionGenerator.SecondFloorRoomPool() {
    }

    @Override
    public String getSmallRoom(Random random) {
        return "1x1_b" + (random.nextInt(5) + 1);
    }

    @Override
    public String getSmallSecretRoom(Random random) {
        return "1x1_as" + (random.nextInt(4) + 1);
    }

    @Override
    public String getMediumFunctionalRoom(Random random, boolean staircase) {
        if (staircase) {
            return "1x2_c_stairs";
        }
        return "1x2_c" + (random.nextInt(4) + 1);
    }

    @Override
    public String getMediumGenericRoom(Random random, boolean staircase) {
        if (staircase) {
            return "1x2_d_stairs";
        }
        return "1x2_d" + (random.nextInt(5) + 1);
    }

    @Override
    public String getMediumSecretRoom(Random random) {
        return "1x2_se" + (random.nextInt(1) + 1);
    }

    @Override
    public String getBigRoom(Random random) {
        return "2x2_b" + (random.nextInt(5) + 1);
    }

    @Override
    public String getBigSecretRoom(Random random) {
        return "2x2_s1";
    }
}
