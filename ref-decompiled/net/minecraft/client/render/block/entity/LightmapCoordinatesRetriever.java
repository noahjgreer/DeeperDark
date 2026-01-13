/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
public class LightmapCoordinatesRetriever<S extends BlockEntity>
implements DoubleBlockProperties.PropertyRetriever<S, Int2IntFunction> {
    public Int2IntFunction getFromBoth(S blockEntity, S blockEntity2) {
        return i -> {
            int j = WorldRenderer.getLightmapCoordinates((BlockRenderView)blockEntity.getWorld(), (BlockPos)blockEntity.getPos());
            int k = WorldRenderer.getLightmapCoordinates((BlockRenderView)blockEntity2.getWorld(), (BlockPos)blockEntity2.getPos());
            int l = LightmapTextureManager.getBlockLightCoordinates((int)j);
            int m = LightmapTextureManager.getBlockLightCoordinates((int)k);
            int n = LightmapTextureManager.getSkyLightCoordinates((int)j);
            int o = LightmapTextureManager.getSkyLightCoordinates((int)k);
            return LightmapTextureManager.pack((int)Math.max(l, m), (int)Math.max(n, o));
        };
    }

    public Int2IntFunction getFrom(S blockEntity) {
        return i -> i;
    }

    public Int2IntFunction getFallback() {
        return i -> i;
    }

    public /* synthetic */ Object getFallback() {
        return this.getFallback();
    }
}

