/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.UploadProgress;
import net.minecraft.client.realms.util.UploadProgressTracker;

@Environment(value=EnvType.CLIENT)
static class UploadProgressTracker.1
implements UploadProgressTracker {
    private final UploadProgress progress = new UploadProgress();

    UploadProgressTracker.1() {
    }

    @Override
    public UploadProgress getUploadProgress() {
        return this.progress;
    }

    @Override
    public void updateProgressDisplay() {
    }
}
