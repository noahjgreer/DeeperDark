/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.LayeringTransform
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LayeringTransform {
    private final String name;
    private final @Nullable Consumer<Matrix4fStack> transform;
    public static final LayeringTransform NO_LAYERING = new LayeringTransform("no_layering", null);
    public static final LayeringTransform VIEW_OFFSET_Z_LAYERING = new LayeringTransform("view_offset_z_layering", matrices -> RenderSystem.getProjectionType().apply((Matrix4f)matrices, 1.0f));
    public static final LayeringTransform VIEW_OFFSET_Z_LAYERING_FORWARD = new LayeringTransform("view_offset_z_layering_forward", matrices -> RenderSystem.getProjectionType().apply((Matrix4f)matrices, -1.0f));

    public LayeringTransform(String name, @Nullable Consumer<Matrix4fStack> transform) {
        this.name = name;
        this.transform = transform;
    }

    public String toString() {
        return "LayeringTransform[" + this.name + "]";
    }

    public @Nullable Consumer<Matrix4fStack> getTransform() {
        return this.transform;
    }
}

