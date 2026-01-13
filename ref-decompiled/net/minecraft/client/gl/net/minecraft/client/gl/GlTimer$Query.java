/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static class GlTimer.Query {
    private static final long MISSING = 0L;
    private static final long CLOSED = -1L;
    private final GpuQuery query;
    private long result = 0L;

    GlTimer.Query(GpuQuery query) {
        this.query = query;
    }

    public void close() {
        RenderSystem.assertOnRenderThread();
        if (this.result != 0L) {
            return;
        }
        this.result = -1L;
        this.query.close();
    }

    public boolean isResultAvailable() {
        RenderSystem.assertOnRenderThread();
        if (this.result != 0L) {
            return true;
        }
        OptionalLong optionalLong = this.query.getValue();
        if (optionalLong.isPresent()) {
            this.result = optionalLong.getAsLong();
            this.query.close();
            return true;
        }
        return false;
    }

    public long queryResult() {
        OptionalLong optionalLong;
        RenderSystem.assertOnRenderThread();
        if (this.result == 0L && (optionalLong = this.query.getValue()).isPresent()) {
            this.result = optionalLong.getAsLong();
            this.query.close();
        }
        return this.result;
    }
}
