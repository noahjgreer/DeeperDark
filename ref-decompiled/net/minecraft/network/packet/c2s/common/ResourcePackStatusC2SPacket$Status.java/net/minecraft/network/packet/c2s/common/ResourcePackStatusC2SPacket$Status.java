/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.common;

public static final class ResourcePackStatusC2SPacket.Status
extends Enum<ResourcePackStatusC2SPacket.Status> {
    public static final /* enum */ ResourcePackStatusC2SPacket.Status SUCCESSFULLY_LOADED = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status DECLINED = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status FAILED_DOWNLOAD = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status ACCEPTED = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status DOWNLOADED = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status INVALID_URL = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status FAILED_RELOAD = new ResourcePackStatusC2SPacket.Status();
    public static final /* enum */ ResourcePackStatusC2SPacket.Status DISCARDED = new ResourcePackStatusC2SPacket.Status();
    private static final /* synthetic */ ResourcePackStatusC2SPacket.Status[] field_13019;

    public static ResourcePackStatusC2SPacket.Status[] values() {
        return (ResourcePackStatusC2SPacket.Status[])field_13019.clone();
    }

    public static ResourcePackStatusC2SPacket.Status valueOf(String string) {
        return Enum.valueOf(ResourcePackStatusC2SPacket.Status.class, string);
    }

    public boolean hasFinished() {
        return this != ACCEPTED && this != DOWNLOADED;
    }

    private static /* synthetic */ ResourcePackStatusC2SPacket.Status[] method_36961() {
        return new ResourcePackStatusC2SPacket.Status[]{SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, DOWNLOADED, INVALID_URL, FAILED_RELOAD, DISCARDED};
    }

    static {
        field_13019 = ResourcePackStatusC2SPacket.Status.method_36961();
    }
}
