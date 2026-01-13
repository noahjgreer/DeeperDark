/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.world.World;

public static interface TrialSpawnerLogic.TrialSpawner {
    public void setSpawnerState(World var1, TrialSpawnerState var2);

    public TrialSpawnerState getSpawnerState();

    public void updateListeners();
}
