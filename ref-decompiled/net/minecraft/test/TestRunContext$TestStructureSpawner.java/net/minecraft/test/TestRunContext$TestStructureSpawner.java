/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestState;

public static interface TestRunContext.TestStructureSpawner {
    public static final TestRunContext.TestStructureSpawner REUSE = oldState -> Optional.ofNullable(oldState.init()).map(gameTestState -> gameTestState.startCountdown(1));
    public static final TestRunContext.TestStructureSpawner NOOP = oldState -> Optional.empty();

    public Optional<GameTestState> spawnStructure(GameTestState var1);

    default public void onBatch(ServerWorld world) {
    }
}
