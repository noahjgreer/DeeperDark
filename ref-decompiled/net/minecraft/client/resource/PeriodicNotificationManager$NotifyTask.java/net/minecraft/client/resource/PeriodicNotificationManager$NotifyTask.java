/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.PeriodicNotificationManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class PeriodicNotificationManager.NotifyTask
extends TimerTask {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<PeriodicNotificationManager.Entry> entries;
    private final long periodMs;
    private final AtomicLong delayMs;

    public PeriodicNotificationManager.NotifyTask(List<PeriodicNotificationManager.Entry> entries, long minDelayMs, long periodMs) {
        this.entries = entries;
        this.periodMs = periodMs;
        this.delayMs = new AtomicLong(minDelayMs);
    }

    public PeriodicNotificationManager.NotifyTask reload(List<PeriodicNotificationManager.Entry> entries, long period) {
        this.cancel();
        return new PeriodicNotificationManager.NotifyTask(entries, this.delayMs.get(), period);
    }

    @Override
    public void run() {
        long l = this.delayMs.getAndAdd(this.periodMs);
        long m = this.delayMs.get();
        for (PeriodicNotificationManager.Entry entry : this.entries) {
            long o;
            long n;
            if (l < entry.delay || (n = l / entry.period) == (o = m / entry.period)) continue;
            this.client.execute(() -> SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable(entry.title, n), Text.translatable(entry.message, n)));
            return;
        }
    }
}
