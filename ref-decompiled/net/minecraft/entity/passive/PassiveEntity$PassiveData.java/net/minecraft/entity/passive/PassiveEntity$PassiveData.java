/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityData;

public static class PassiveEntity.PassiveData
implements EntityData {
    private int spawnCount;
    private final boolean babyAllowed;
    private final float babyChance;

    public PassiveEntity.PassiveData(boolean babyAllowed, float babyChance) {
        this.babyAllowed = babyAllowed;
        this.babyChance = babyChance;
    }

    public PassiveEntity.PassiveData(boolean babyAllowed) {
        this(babyAllowed, 0.05f);
    }

    public PassiveEntity.PassiveData(float babyChance) {
        this(true, babyChance);
    }

    public int getSpawnedCount() {
        return this.spawnCount;
    }

    public void countSpawned() {
        ++this.spawnCount;
    }

    public boolean canSpawnBaby() {
        return this.babyAllowed;
    }

    public float getBabyChance() {
        return this.babyChance;
    }
}
