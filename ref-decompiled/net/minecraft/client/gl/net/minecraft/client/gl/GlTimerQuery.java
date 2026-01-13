/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.ARBTimerQuery
 *  org.lwjgl.opengl.GL32C
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.GpuQuery;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.GL32C;

@Environment(value=EnvType.CLIENT)
public class GlTimerQuery
implements GpuQuery {
    private final int id;
    private boolean closed;
    private OptionalLong value = OptionalLong.empty();

    GlTimerQuery(int id) {
        this.id = id;
    }

    @Override
    public OptionalLong getValue() {
        RenderSystem.assertOnRenderThread();
        if (this.closed) {
            throw new IllegalStateException("GlTimerQuery is closed");
        }
        if (this.value.isPresent()) {
            return this.value;
        }
        if (GL32C.glGetQueryObjecti((int)this.id, (int)34919) == 1) {
            this.value = OptionalLong.of(ARBTimerQuery.glGetQueryObjecti64((int)this.id, (int)34918));
            return this.value;
        }
        return OptionalLong.empty();
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThread();
        if (this.closed) {
            return;
        }
        this.closed = true;
        GL32C.glDeleteQueries((int)this.id);
    }
}
