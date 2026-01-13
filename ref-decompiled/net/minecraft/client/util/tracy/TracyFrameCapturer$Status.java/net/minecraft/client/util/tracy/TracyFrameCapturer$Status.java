/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.tracy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class TracyFrameCapturer.Status
extends Enum<TracyFrameCapturer.Status> {
    public static final /* enum */ TracyFrameCapturer.Status WAITING_FOR_CAPTURE = new TracyFrameCapturer.Status();
    public static final /* enum */ TracyFrameCapturer.Status WAITING_FOR_COPY = new TracyFrameCapturer.Status();
    public static final /* enum */ TracyFrameCapturer.Status WAITING_FOR_UPLOAD = new TracyFrameCapturer.Status();
    private static final /* synthetic */ TracyFrameCapturer.Status[] field_57837;

    public static TracyFrameCapturer.Status[] values() {
        return (TracyFrameCapturer.Status[])field_57837.clone();
    }

    public static TracyFrameCapturer.Status valueOf(String string) {
        return Enum.valueOf(TracyFrameCapturer.Status.class, string);
    }

    private static /* synthetic */ TracyFrameCapturer.Status[] method_68340() {
        return new TracyFrameCapturer.Status[]{WAITING_FOR_CAPTURE, WAITING_FOR_COPY, WAITING_FOR_UPLOAD};
    }

    static {
        field_57837 = TracyFrameCapturer.Status.method_68340();
    }
}
