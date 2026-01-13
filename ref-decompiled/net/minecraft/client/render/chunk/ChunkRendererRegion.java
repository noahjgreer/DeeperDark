/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.render.chunk.ChunkRendererRegion
 *  net.minecraft.client.render.chunk.RenderedChunk
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.BlockRenderView
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.ColorResolver
 *  net.minecraft.world.chunk.light.LightingProvider
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChunkRendererRegion
implements BlockRenderView {
    public static final int field_52160 = 1;
    public static final int SIDE_LENGTH_CHUNKS = 3;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final RenderedChunk[] renderedChunks;
    private final World world;

    ChunkRendererRegion(World world, int baseX, int baseY, int baseZ, RenderedChunk[] renderedChunks) {
        this.world = world;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.renderedChunks = renderedChunks;
    }

    public BlockState getBlockState(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord((int)pos.getX()), ChunkSectionPos.getSectionCoord((int)pos.getY()), ChunkSectionPos.getSectionCoord((int)pos.getZ())).getBlockState(pos);
    }

    public FluidState getFluidState(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord((int)pos.getX()), ChunkSectionPos.getSectionCoord((int)pos.getY()), ChunkSectionPos.getSectionCoord((int)pos.getZ())).getBlockState(pos).getFluidState();
    }

    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return this.getRenderedChunk(ChunkSectionPos.getSectionCoord((int)pos.getX()), ChunkSectionPos.getSectionCoord((int)pos.getY()), ChunkSectionPos.getSectionCoord((int)pos.getZ())).getBlockEntity(pos);
    }

    private RenderedChunk getRenderedChunk(int sectionX, int sectionY, int sectionZ) {
        return this.renderedChunks[ChunkRendererRegion.getIndex((int)this.baseX, (int)this.baseY, (int)this.baseZ, (int)sectionX, (int)sectionY, (int)sectionZ)];
    }

    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return this.world.getColor(pos, colorResolver);
    }

    public int getBottomY() {
        return this.world.getBottomY();
    }

    public int getHeight() {
        return this.world.getHeight();
    }

    public static int getIndex(int xOffset, int yOffset, int zOffset, int sectionX, int sectionY, int sectionZ) {
        return sectionX - xOffset + (sectionY - yOffset) * 3 + (sectionZ - zOffset) * 3 * 3;
    }
}

