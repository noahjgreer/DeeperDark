/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.sound.SoundExecutor
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.thread.ThreadExecutor
 */
package net.minecraft.client.sound;

import java.util.concurrent.locks.LockSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.thread.ThreadExecutor;

@Environment(value=EnvType.CLIENT)
public class SoundExecutor
extends ThreadExecutor<Runnable> {
    private Thread thread = this.createThread();
    private volatile boolean stopped;

    public SoundExecutor() {
        super("Sound executor");
    }

    private Thread createThread() {
        Thread thread2 = new Thread(() -> this.waitForStop());
        thread2.setDaemon(true);
        thread2.setName("Sound engine");
        thread2.setUncaughtExceptionHandler((thread, throwable) -> MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create((Throwable)throwable, (String)("Uncaught exception on thread: " + thread.getName()))));
        thread2.start();
        return thread2;
    }

    public Runnable createTask(Runnable runnable) {
        return runnable;
    }

    public void send(Runnable runnable) {
        if (!this.stopped) {
            super.send(runnable);
        }
    }

    protected boolean canExecute(Runnable task) {
        return !this.stopped;
    }

    protected Thread getThread() {
        return this.thread;
    }

    private void waitForStop() {
        while (!this.stopped) {
            this.runTasks(() -> this.stopped);
        }
    }

    protected void waitForTasks() {
        LockSupport.park("waiting for tasks");
    }

    public void stop() {
        this.stopped = true;
        this.cancelTasks();
        this.thread.interrupt();
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    public void restart() {
        this.stopped = false;
        this.thread = this.createThread();
    }
}

