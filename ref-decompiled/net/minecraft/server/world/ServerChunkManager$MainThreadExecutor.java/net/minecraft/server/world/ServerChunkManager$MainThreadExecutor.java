/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;

final class ServerChunkManager.MainThreadExecutor
extends ThreadExecutor<Runnable> {
    ServerChunkManager.MainThreadExecutor(World world) {
        super("Chunk source main thread executor for " + String.valueOf(world.getRegistryKey().getValue()));
    }

    @Override
    public void runTasks(BooleanSupplier stopCondition) {
        super.runTasks(() -> MinecraftServer.checkWorldGenException() && stopCondition.getAsBoolean());
    }

    @Override
    public Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return true;
    }

    @Override
    protected boolean shouldExecuteAsync() {
        return true;
    }

    @Override
    protected Thread getThread() {
        return ServerChunkManager.this.serverThread;
    }

    @Override
    protected void executeTask(Runnable task) {
        Profilers.get().visit("runTask");
        super.executeTask(task);
    }

    @Override
    protected boolean runTask() {
        if (ServerChunkManager.this.updateChunks()) {
            return true;
        }
        ServerChunkManager.this.lightingProvider.tick();
        return super.runTask();
    }
}
