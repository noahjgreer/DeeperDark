/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 */
package net.minecraft.block;

import com.google.common.cache.CacheLoader;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

class Block.1
extends CacheLoader<VoxelShape, Boolean> {
    Block.1() {
    }

    public Boolean load(VoxelShape voxelShape) {
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), voxelShape, BooleanBiFunction.NOT_SAME);
    }

    public /* synthetic */ Object load(Object shape) throws Exception {
        return this.load((VoxelShape)shape);
    }
}
