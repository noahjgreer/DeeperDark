/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.structure.pool;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.util.shape.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

static final class StructurePoolBasedGenerator.ShapedPoolStructurePiece
extends Record {
    final PoolStructurePiece piece;
    final MutableObject<VoxelShape> pieceShape;
    final int depth;

    StructurePoolBasedGenerator.ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
        this.piece = piece;
        this.pieceShape = pieceShape;
        this.depth = currentSize;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StructurePoolBasedGenerator.ShapedPoolStructurePiece.class, "piece;free;depth", "piece", "pieceShape", "depth"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StructurePoolBasedGenerator.ShapedPoolStructurePiece.class, "piece;free;depth", "piece", "pieceShape", "depth"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StructurePoolBasedGenerator.ShapedPoolStructurePiece.class, "piece;free;depth", "piece", "pieceShape", "depth"}, this, object);
    }

    public PoolStructurePiece piece() {
        return this.piece;
    }

    public MutableObject<VoxelShape> pieceShape() {
        return this.pieceShape;
    }

    public int depth() {
        return this.depth;
    }
}
