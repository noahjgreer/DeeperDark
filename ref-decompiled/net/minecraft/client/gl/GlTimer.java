/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuQuery
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GlTimer
 *  net.minecraft.client.gl.GlTimer$InstanceHolder
 *  net.minecraft.client.gl.GlTimer$Query
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlTimer;
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
}

