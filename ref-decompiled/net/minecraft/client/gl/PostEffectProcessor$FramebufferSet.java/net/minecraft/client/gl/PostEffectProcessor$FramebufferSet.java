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
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface PostEffectProcessor.FramebufferSet {
    public static PostEffectProcessor.FramebufferSet singleton(final Identifier id, final Handle<Framebuffer> framebuffer) {
        return new PostEffectProcessor.FramebufferSet(){
            private Handle<Framebuffer> framebuffer;
            {
                this.framebuffer = framebuffer;
            }

            @Override
            public void set(Identifier id2, Handle<Framebuffer> framebuffer2) {
                if (!id2.equals(id)) {
                    throw new IllegalArgumentException("No target with id " + String.valueOf(id2));
                }
                this.framebuffer = framebuffer2;
            }

            @Override
            public @Nullable Handle<Framebuffer> get(Identifier id2) {
                return id2.equals(id) ? this.framebuffer : null;
            }
        };
    }

    public void set(Identifier var1, Handle<Framebuffer> var2);

    public @Nullable Handle<Framebuffer> get(Identifier var1);

    default public Handle<Framebuffer> getOrThrow(Identifier id) {
        Handle<Framebuffer> handle = this.get(id);
        if (handle == null) {
            throw new IllegalArgumentException("Missing target with id " + String.valueOf(id));
        }
        return handle;
    }
}
