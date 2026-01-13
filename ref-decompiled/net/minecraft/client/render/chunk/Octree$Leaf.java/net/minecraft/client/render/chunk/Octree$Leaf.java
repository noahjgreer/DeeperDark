/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
final class Octree.Leaf
implements Octree.Node {
    private final ChunkBuilder.BuiltChunk chunk;

    Octree.Leaf(ChunkBuilder.BuiltChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void visit(Octree.Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
        Box box = this.chunk.getBoundingBox();
        if (skipVisibilityCheck || frustum.isVisible(this.getBuiltChunk().getBoundingBox())) {
            nearCenter = nearCenter && Octree.this.isCenterWithin(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, margin);
            visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
        }
    }

    @Override
    public ChunkBuilder.BuiltChunk getBuiltChunk() {
        return this.chunk;
    }

    @Override
    public Box getBoundingBox() {
        return this.chunk.getBoundingBox();
    }
}
