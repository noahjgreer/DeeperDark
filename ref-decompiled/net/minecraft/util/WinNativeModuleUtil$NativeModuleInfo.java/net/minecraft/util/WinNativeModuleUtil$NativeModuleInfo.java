/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public static class WinNativeModuleUtil.NativeModuleInfo {
    public final String fileDescription;
    public final String fileVersion;
    public final String companyName;

    public WinNativeModuleUtil.NativeModuleInfo(String fileDescription, String fileVersion, String companyName) {
        this.fileDescription = fileDescription;
        this.fileVersion = fileVersion;
        this.companyName = companyName;
    }

    public String toString() {
        return this.fileDescription + ":" + this.fileVersion + ":" + this.companyName;
    }
}
