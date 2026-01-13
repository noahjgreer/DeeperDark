/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class PostEffectProcessor.FramebufferSet.1
implements PostEffectProcessor.FramebufferSet {
    private Handle<Framebuffer> framebuffer;
    final /* synthetic */ Handle field_53108;
    final /* synthetic */ Identifier field_53109;

    PostEffectProcessor.FramebufferSet.1() {
        this.field_53108 = handle;
        this.field_53109 = identifier;
        this.framebuffer = this.field_53108;
    }

    @Override
    public void set(Identifier id, Handle<Framebuffer> framebuffer) {
        if (!id.equals(this.field_53109)) {
            throw new IllegalArgumentException("No target with id " + String.valueOf(id));
        }
        this.framebuffer = framebuffer;
    }

    @Override
    public @Nullable Handle<Framebuffer> get(Identifier id) {
        return id.equals(this.field_53109) ? this.framebuffer : null;
    }
}
