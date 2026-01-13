/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.Box;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface Octree.Node {
    public void visit(Octree.Visitor var1, boolean var2, Frustum var3, int var4, int var5, boolean var6);

    public @Nullable ChunkBuilder.BuiltChunk getBuiltChunk();

    public Box getBoundingBox();
}
