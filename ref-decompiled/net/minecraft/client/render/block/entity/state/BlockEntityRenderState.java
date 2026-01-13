/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderState
implements FabricRenderState {
    public BlockPos pos = BlockPos.ORIGIN;
    public BlockState blockState = Blocks.AIR.getDefaultState();
    public BlockEntityType<?> type = BlockEntityType.TEST_BLOCK;
    public int lightmapCoordinates;
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay;

    public static void updateBlockEntityRenderState(BlockEntity blockEntity, BlockEntityRenderState state, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        state.pos = blockEntity.getPos();
        state.blockState = blockEntity.getCachedState();
        state.type = blockEntity.getType();
        state.lightmapCoordinates = blockEntity.getWorld() != null ? WorldRenderer.getLightmapCoordinates((BlockRenderView)blockEntity.getWorld(), (BlockPos)blockEntity.getPos()) : 0xF000F0;
        state.crumblingOverlay = crumblingOverlay;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("BlockEntityRenderState", (Object)this.getClass().getCanonicalName());
        crashReportSection.add("Position", (Object)this.pos);
        crashReportSection.add("Block state", () -> ((BlockState)this.blockState).toString());
    }
}

