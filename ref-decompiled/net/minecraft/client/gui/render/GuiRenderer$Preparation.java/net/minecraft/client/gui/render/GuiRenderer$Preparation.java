/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.texture.TextureSetup;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class GuiRenderer.Preparation
extends Record
implements AutoCloseable {
    final BuiltBuffer mesh;
    final RenderPipeline pipeline;
    final TextureSetup textureSetup;
    final @Nullable ScreenRect scissorArea;

    GuiRenderer.Preparation(BuiltBuffer mesh, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea) {
        this.mesh = mesh;
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.scissorArea = scissorArea;
    }

    @Override
    public void close() {
        this.mesh.close();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GuiRenderer.Preparation.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GuiRenderer.Preparation.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GuiRenderer.Preparation.class, "mesh;pipeline;textureSetup;scissorArea", "mesh", "pipeline", "textureSetup", "scissorArea"}, this, object);
    }

    public BuiltBuffer mesh() {
        return this.mesh;
    }

    public RenderPipeline pipeline() {
        return this.pipeline;
    }

    public TextureSetup textureSetup() {
        return this.textureSetup;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }
}
