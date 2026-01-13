/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.debug.GameTestDebugRenderer
 *  net.minecraft.client.render.debug.GameTestDebugRenderer$Marker
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.debug.GameTestDebugRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer {
    private static final int MARKER_LIFESPAN_MS = 10000;
    private static final float MARKER_BOX_SIZE = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos absolutePos, BlockPos relativePos) {
        String string = relativePos.toShortString();
        this.markers.put(absolutePos, new Marker(0x6000FF00, string, Util.getMeasuringTimeMs() + 10000L));
    }

    public void clear() {
        this.markers.clear();
    }

    public void render() {
        long l = Util.getMeasuringTimeMs();
        this.markers.entrySet().removeIf(marker -> l > ((Marker)marker.getValue()).removalTime);
        this.markers.forEach((pos, marker) -> this.render(pos, marker));
    }

    private void render(BlockPos blockPos, Marker marker) {
        GizmoDrawing.box((BlockPos)blockPos, (float)0.02f, (DrawStyle)DrawStyle.filled((int)marker.color()));
        if (!marker.message.isEmpty()) {
            GizmoDrawing.text((String)marker.message, (Vec3d)Vec3d.add((Vec3i)blockPos, (double)0.5, (double)1.2, (double)0.5), (TextGizmo.Style)TextGizmo.Style.left().scaled(0.16f)).ignoreOcclusion();
        }
    }
}

