/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlTimer {
    private @Nullable CommandEncoder encoder;
    private @Nullable GpuQuery query;

    public static GlTimer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public boolean isRunning() {
        return this.query != null;
    }

    public void beginProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.query != null) {
            throw new IllegalStateException("Current profile not ended");
        }
        this.encoder = RenderSystem.getDevice().createCommandEncoder();
        this.query = this.encoder.timerQueryBegin();
    }

    public Query endProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.query == null || this.encoder == null) {
            throw new IllegalStateException("endProfile called before beginProfile");
        }
        this.encoder.timerQueryEnd(this.query);
        Query query = new Query(this.query);
        this.query = null;
        this.encoder = null;
        return query;
    }

    @Environment(value=EnvType.CLIENT)
    static class InstanceHolder {
        static final GlTimer INSTANCE = InstanceHolder.create();

        private InstanceHolder() {
        }

        private static GlTimer create() {
            return new GlTimer();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Query {
        private static final long MISSING = 0L;
        private static final long CLOSED = -1L;
        private final GpuQuery query;
        private long result = 0L;

        Query(GpuQuery query) {
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
}
