/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.WorldProperties;

@Environment(value=EnvType.CLIENT)
public static class ClientWorld.Properties
implements MutableWorldProperties {
    private final boolean hardcore;
    private final boolean flatWorld;
    private WorldProperties.SpawnPoint position;
    private long time;
    private long timeOfDay;
    private boolean raining;
    private Difficulty difficulty;
    private boolean difficultyLocked;

    public ClientWorld.Properties(Difficulty difficulty, boolean hardcore, boolean flatWorld) {
        this.difficulty = difficulty;
        this.hardcore = hardcore;
        this.flatWorld = flatWorld;
    }

    @Override
    public WorldProperties.SpawnPoint getSpawnPoint() {
        return this.position;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getTimeOfDay() {
        return this.timeOfDay;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTimeOfDay(long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public void setSpawnPoint(WorldProperties.SpawnPoint spawnPoint) {
        this.position = spawnPoint;
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    @Override
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
        MutableWorldProperties.super.populateCrashReport(reportSection, world);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficultyLocked(boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }

    public double getSkyDarknessHeight(HeightLimitView world) {
        if (this.flatWorld) {
            return world.getBottomY();
        }
        return 63.0;
    }

    public float getVoidDarknessRange() {
        if (this.flatWorld) {
            return 1.0f;
        }
        return 32.0f;
    }
}
