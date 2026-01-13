/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.util.BufferAllocator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.BufferAllocator;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Tessellator {
    private static final int MAX_BUFFER_SIZE = 786432;
    private final BufferAllocator allocator;
    private static @Nullable Tessellator INSTANCE;

    public static void initialize() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Tesselator has already been initialized");
        }
        INSTANCE = new Tessellator();
    }

    public static Tessellator getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Tesselator has not been initialized");
        }
        return INSTANCE;
    }

    public Tessellator(int bufferCapacity) {
        this.allocator = new BufferAllocator(bufferCapacity);
    }

    public Tessellator() {
        this(786432);
    }

    public BufferBuilder begin(VertexFormat.DrawMode drawMode, VertexFormat format) {
        return new BufferBuilder(this.allocator, drawMode, format);
    }

    public void clear() {
        this.allocator.clear();
    }
}

