/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultFramebufferSet
implements PostEffectProcessor.FramebufferSet {
    public static final Identifier MAIN = PostEffectProcessor.MAIN;
    public static final Identifier TRANSLUCENT = Identifier.ofVanilla("translucent");
    public static final Identifier ITEM_ENTITY = Identifier.ofVanilla("item_entity");
    public static final Identifier PARTICLES = Identifier.ofVanilla("particles");
    public static final Identifier WEATHER = Identifier.ofVanilla("weather");
    public static final Identifier CLOUDS = Identifier.ofVanilla("clouds");
    public static final Identifier ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
    public static final Set<Identifier> MAIN_ONLY = Set.of(MAIN);
    public static final Set<Identifier> MAIN_AND_ENTITY_OUTLINE = Set.of(MAIN, ENTITY_OUTLINE);
    public static final Set<Identifier> STAGES = Set.of(MAIN, TRANSLUCENT, ITEM_ENTITY, PARTICLES, WEATHER, CLOUDS);
    public Handle<Framebuffer> mainFramebuffer = Handle.empty();
    public @Nullable Handle<Framebuffer> translucentFramebuffer;
    public @Nullable Handle<Framebuffer> itemEntityFramebuffer;
    public @Nullable Handle<Framebuffer> particlesFramebuffer;
    public @Nullable Handle<Framebuffer> weatherFramebuffer;
    public @Nullable Handle<Framebuffer> cloudsFramebuffer;
    public @Nullable Handle<Framebuffer> entityOutlineFramebuffer;

    @Override
    public void set(Identifier id, Handle<Framebuffer> framebuffer) {
        if (id.equals(MAIN)) {
            this.mainFramebuffer = framebuffer;
        } else if (id.equals(TRANSLUCENT)) {
            this.translucentFramebuffer = framebuffer;
        } else if (id.equals(ITEM_ENTITY)) {
            this.itemEntityFramebuffer = framebuffer;
        } else if (id.equals(PARTICLES)) {
            this.particlesFramebuffer = framebuffer;
        } else if (id.equals(WEATHER)) {
            this.weatherFramebuffer = framebuffer;
        } else if (id.equals(CLOUDS)) {
            this.cloudsFramebuffer = framebuffer;
        } else if (id.equals(ENTITY_OUTLINE)) {
            this.entityOutlineFramebuffer = framebuffer;
        } else {
            throw new IllegalArgumentException("No target with id " + String.valueOf(id));
        }
    }

    @Override
    public @Nullable Handle<Framebuffer> get(Identifier id) {
        if (id.equals(MAIN)) {
            return this.mainFramebuffer;
        }
        if (id.equals(TRANSLUCENT)) {
            return this.translucentFramebuffer;
        }
        if (id.equals(ITEM_ENTITY)) {
            return this.itemEntityFramebuffer;
        }
        if (id.equals(PARTICLES)) {
            return this.particlesFramebuffer;
        }
        if (id.equals(WEATHER)) {
            return this.weatherFramebuffer;
        }
        if (id.equals(CLOUDS)) {
            return this.cloudsFramebuffer;
        }
        if (id.equals(ENTITY_OUTLINE)) {
            return this.entityOutlineFramebuffer;
        }
        return null;
    }

    public void clear() {
        this.mainFramebuffer = Handle.empty();
        this.translucentFramebuffer = null;
        this.itemEntityFramebuffer = null;
        this.particlesFramebuffer = null;
        this.weatherFramebuffer = null;
        this.cloudsFramebuffer = null;
        this.entityOutlineFramebuffer = null;
    }
}
