/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.BreakingBlockRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.render.state.OutlineRenderState
 *  net.minecraft.client.render.state.SkyRenderState
 *  net.minecraft.client.render.state.WeatherRenderState
 *  net.minecraft.client.render.state.WorldBorderRenderState
 *  net.minecraft.client.render.state.WorldRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.state;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.BreakingBlockRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.render.state.WeatherRenderState;
import net.minecraft.client.render.state.WorldBorderRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldRenderState
implements FabricRenderState {
    public CameraRenderState cameraRenderState = new CameraRenderState();
    public final List<EntityRenderState> entityRenderStates = new ArrayList();
    public final List<BlockEntityRenderState> blockEntityRenderStates = new ArrayList();
    public boolean hasOutline;
    public @Nullable OutlineRenderState outlineRenderState;
    public final List<BreakingBlockRenderState> breakingBlockRenderStates = new ArrayList();
    public final WeatherRenderState weatherRenderState = new WeatherRenderState();
    public final WorldBorderRenderState worldBorderRenderState = new WorldBorderRenderState();
    public final SkyRenderState skyRenderState = new SkyRenderState();
    public long time;

    public void clear() {
        this.entityRenderStates.clear();
        this.blockEntityRenderStates.clear();
        this.breakingBlockRenderStates.clear();
        this.hasOutline = false;
        this.outlineRenderState = null;
        this.weatherRenderState.clear();
        this.worldBorderRenderState.clear();
        this.skyRenderState.clear();
        this.time = 0L;
    }
}

