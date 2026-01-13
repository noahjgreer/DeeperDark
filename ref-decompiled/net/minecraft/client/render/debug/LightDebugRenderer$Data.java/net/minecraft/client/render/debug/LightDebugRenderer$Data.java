/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.light.LightingProvider;

@Environment(value=EnvType.CLIENT)
static final class LightDebugRenderer.Data {
    final VoxelSet readyShape;
    final VoxelSet shape;
    final ChunkSectionPos minSectionPos;

    LightDebugRenderer.Data(LightingProvider lightingProvider, ChunkSectionPos sectionPos, int radius, LightType lightType) {
        int i = radius * 2 + 1;
        this.readyShape = new BitSetVoxelSet(i, i, i);
        this.shape = new BitSetVoxelSet(i, i, i);
        for (int j = 0; j < i; ++j) {
            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < i; ++l) {
                    ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(sectionPos.getSectionX() + l - radius, sectionPos.getSectionY() + k - radius, sectionPos.getSectionZ() + j - radius);
                    LightStorage.Status status = lightingProvider.getStatus(lightType, chunkSectionPos);
                    if (status == LightStorage.Status.LIGHT_AND_DATA) {
                        this.readyShape.set(l, k, j);
                        this.shape.set(l, k, j);
                        continue;
                    }
                    if (status != LightStorage.Status.LIGHT_ONLY) continue;
                    this.shape.set(l, k, j);
                }
            }
        }
        this.minSectionPos = ChunkSectionPos.from(sectionPos.getSectionX() - radius, sectionPos.getSectionY() - radius, sectionPos.getSectionZ() - radius);
    }
}
