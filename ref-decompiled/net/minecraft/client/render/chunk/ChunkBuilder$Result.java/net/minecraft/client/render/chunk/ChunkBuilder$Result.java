/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class ChunkBuilder.Result
extends Enum<ChunkBuilder.Result> {
    public static final /* enum */ ChunkBuilder.Result SUCCESSFUL = new ChunkBuilder.Result();
    public static final /* enum */ ChunkBuilder.Result CANCELLED = new ChunkBuilder.Result();
    private static final /* synthetic */ ChunkBuilder.Result[] field_21440;

    public static ChunkBuilder.Result[] values() {
        return (ChunkBuilder.Result[])field_21440.clone();
    }

    public static ChunkBuilder.Result valueOf(String string) {
        return Enum.valueOf(ChunkBuilder.Result.class, string);
    }

    private static /* synthetic */ ChunkBuilder.Result[] method_36923() {
        return new ChunkBuilder.Result[]{SUCCESSFUL, CANCELLED};
    }

    static {
        field_21440 = ChunkBuilder.Result.method_36923();
    }
}
