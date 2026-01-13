/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static class SystemToast.Type {
    public static final SystemToast.Type NARRATOR_TOGGLE = new SystemToast.Type();
    public static final SystemToast.Type WORLD_BACKUP = new SystemToast.Type();
    public static final SystemToast.Type PACK_LOAD_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type WORLD_ACCESS_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type PACK_COPY_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type FILE_DROP_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type PERIODIC_NOTIFICATION = new SystemToast.Type();
    public static final SystemToast.Type LOW_DISK_SPACE = new SystemToast.Type(10000L);
    public static final SystemToast.Type CHUNK_LOAD_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type CHUNK_SAVE_FAILURE = new SystemToast.Type();
    public static final SystemToast.Type UNSECURE_SERVER_WARNING = new SystemToast.Type(10000L);
    final long displayDuration;

    public SystemToast.Type(long displayDuration) {
        this.displayDuration = displayDuration;
    }

    public SystemToast.Type() {
        this(5000L);
    }
}
