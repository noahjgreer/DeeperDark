/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.Collection;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.Batches;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunContext;
import net.minecraft.test.TestStructurePlacer;

public static class TestRunContext.Builder {
    private final ServerWorld world;
    private final TestManager manager = TestManager.INSTANCE;
    private TestRunContext.Batcher batcher = Batches.defaultBatcher();
    private TestRunContext.TestStructureSpawner reuseSpawner = TestRunContext.TestStructureSpawner.REUSE;
    private TestRunContext.TestStructureSpawner initialSpawner = TestRunContext.TestStructureSpawner.NOOP;
    private final Collection<GameTestBatch> batches;
    private boolean stopAfterFailure = false;
    private boolean clearBetweenBatches = false;

    private TestRunContext.Builder(Collection<GameTestBatch> batches, ServerWorld world) {
        this.batches = batches;
        this.world = world;
    }

    public static TestRunContext.Builder of(Collection<GameTestBatch> batches, ServerWorld world) {
        return new TestRunContext.Builder(batches, world);
    }

    public static TestRunContext.Builder ofStates(Collection<GameTestState> states, ServerWorld world) {
        return TestRunContext.Builder.of(Batches.defaultBatcher().batch(states), world);
    }

    public TestRunContext.Builder stopAfterFailure() {
        this.stopAfterFailure = true;
        return this;
    }

    public TestRunContext.Builder clearBetweenBatches() {
        this.clearBetweenBatches = true;
        return this;
    }

    public TestRunContext.Builder initialSpawner(TestRunContext.TestStructureSpawner initialSpawner) {
        this.initialSpawner = initialSpawner;
        return this;
    }

    public TestRunContext.Builder reuseSpawner(TestStructurePlacer reuseSpawner) {
        this.reuseSpawner = reuseSpawner;
        return this;
    }

    public TestRunContext.Builder batcher(TestRunContext.Batcher batcher) {
        this.batcher = batcher;
        return this;
    }

    public TestRunContext build() {
        return new TestRunContext(this.batcher, this.batches, this.world, this.manager, this.reuseSpawner, this.initialSpawner, this.stopAfterFailure, this.clearBetweenBatches);
    }
}
