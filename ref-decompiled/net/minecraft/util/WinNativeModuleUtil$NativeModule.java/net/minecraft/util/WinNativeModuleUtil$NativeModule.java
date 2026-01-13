/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.Optional;
import net.minecraft.util.WinNativeModuleUtil;

public static class WinNativeModuleUtil.NativeModule {
    public final String path;
    public final Optional<WinNativeModuleUtil.NativeModuleInfo> info;

    public WinNativeModuleUtil.NativeModule(String path, Optional<WinNativeModuleUtil.NativeModuleInfo> info) {
        this.path = path;
        this.info = info;
    }

    public String toString() {
        return this.info.map(info -> this.path + ":" + String.valueOf(info)).orElse(this.path);
    }
}
