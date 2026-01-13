/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.render.chunk.RenderedChunk
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.HeightLimitView
 *  net.minecraft.world.chunk.ChunkSection
 *  net.minecraft.world.chunk.EmptyChunk
 *  net.minecraft.world.chunk.PalettedContainer
 *  net.minecraft.world.chunk.WorldChunk
 *  net.minecraft.world.gen.chunk.DebugChunkGenerator
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RenderedChunk {
    private final Map<BlockPos, BlockEntity> blockEntities;
    private final @Nullable PalettedContainer<BlockState> blockPalette;
    private final boolean debugWorld;
    private final HeightLimitView heightLimitView;

    RenderedChunk(WorldChunk chunk, int sectionIndex) {
        this.heightLimitView = chunk;
        this.debugWorld = chunk.getWorld().isDebugWorld();
        this.blockEntities = ImmutableMap.copyOf((Map)chunk.getBlockEntities());
        if (chunk instanceof EmptyChunk) {
            this.blockPalette = null;
        } else {
            ChunkSection chunkSection;
            ChunkSection[] chunkSections = chunk.getSectionArray();
            this.blockPalette = sectionIndex < 0 || sectionIndex >= chunkSections.length ? null : ((chunkSection = chunkSections[sectionIndex]).isEmpty() ? null : chunkSection.getBlockStateContainer().copy());
        }
    }

    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return (BlockEntity)this.blockEntities.get(pos);
    }

    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (this.debugWorld) {
            BlockState blockState = null;
            if (j == 60) {
                blockState = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                blockState = DebugChunkGenerator.getBlockState((int)i, (int)k);
            }
            return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
        }
        if (this.blockPalette == null) {
            return Blocks.AIR.getDefaultState();
        }
        try {
            return (BlockState)this.blockPalette.get(i & 0xF, j & 0xF, k & 0xF);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> CrashReportSection.createPositionString((HeightLimitView)this.heightLimitView, (int)i, (int)j, (int)k));
            throw new CrashException(crashReport);
        }
    }
}

